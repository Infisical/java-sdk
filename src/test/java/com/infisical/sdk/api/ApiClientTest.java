package com.infisical.sdk.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.infisical.sdk.util.InfisicalException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiClientTest {

  private MockWebServer server;

  public static class Payload {
    private final String name;

    public Payload(String name) {
      this.name = name;
    }
  }

  public static class Reply {
    private String message;

    public String getMessage() {
      return message;
    }
  }

  @BeforeEach
  public void startServer() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  public void stopServer() throws IOException {
    server.shutdown();
  }

  private ApiClient apiClient(String accessToken) {
    return new ApiClient(server.url("/").toString(), accessToken);
  }

  @Test
  public void post_sendsJsonBodyWithAuthHeaders() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"created\"}"));

    Reply reply = apiClient("token-123")
        .post(server.url("/api/v3/secrets/raw").toString(), new Payload("SECRET"), Reply.class);

    assertEquals("created", reply.getMessage());

    RecordedRequest request = server.takeRequest();
    assertEquals("POST", request.getMethod());
    assertEquals("/api/v3/secrets/raw", request.getPath());
    assertEquals("Bearer token-123", request.getHeader("Authorization"));
    assertEquals("application/json", request.getHeader("Accept"));
    assertEquals("application/json; charset=utf-8", request.getHeader("Content-Type"));
    assertEquals("{\"name\":\"SECRET\"}", request.getBody().readUtf8());
  }

  @Test
  public void get_appendsQueryParametersAndAuthHeader() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"ok\"}"));

    Map<String, String> queryParams = new LinkedHashMap<>();
    queryParams.put("workspaceId", "project-1");
    queryParams.put("environment", "dev");

    apiClient("token-123").get(server.url("/api/v3/secrets/raw").toString(), queryParams, Reply.class);

    RecordedRequest request = server.takeRequest();
    assertEquals("GET", request.getMethod());

    HttpUrl url = request.getRequestUrl();
    assertEquals("/api/v3/secrets/raw", url.encodedPath());
    assertEquals("project-1", url.queryParameter("workspaceId"));
    assertEquals("dev", url.queryParameter("environment"));
    assertEquals("Bearer token-123", request.getHeader("Authorization"));
  }

  @Test
  public void patch_usesPatchVerbAndSendsBody() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"updated\"}"));

    Reply reply = apiClient("token-123")
        .patch(server.url("/api/v3/secrets/raw/SECRET").toString(), new Payload("SECRET"), Reply.class);

    assertEquals("updated", reply.getMessage());

    RecordedRequest request = server.takeRequest();
    assertEquals("PATCH", request.getMethod());
    assertEquals("{\"name\":\"SECRET\"}", request.getBody().readUtf8());
  }

  @Test
  public void delete_usesDeleteVerbAndSendsBody() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"deleted\"}"));

    Reply reply = apiClient("token-123")
        .delete(server.url("/api/v3/secrets/raw/SECRET").toString(), new Payload("SECRET"), Reply.class);

    assertEquals("deleted", reply.getMessage());

    RecordedRequest request = server.takeRequest();
    assertEquals("DELETE", request.getMethod());
    assertEquals("{\"name\":\"SECRET\"}", request.getBody().readUtf8());
  }

  @Test
  public void post_withoutAccessToken_omitsAuthorizationHeader() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"ok\"}"));

    apiClient(null)
        .post(server.url("/api/v1/auth/universal-auth/login").toString(), new Payload("id"), Reply.class);

    assertNull(server.takeRequest().getHeader("Authorization"));
  }

  @Test
  public void post_withEmptyResponseBody_returnsNull() throws Exception {
    server.enqueue(new MockResponse().setResponseCode(200));

    Void result = apiClient("token-123")
        .post(server.url("/api/v1/auth/token/revoke").toString(), new Payload("token"), Void.class);

    assertNull(result);
  }

  @Test
  public void errorResponse_throwsInfisicalExceptionCarryingResponseBody() {
    server.enqueue(
        new MockResponse().setResponseCode(403).setBody("{\"message\":\"permission denied\"}"));

    InfisicalException exception = assertThrows(
        InfisicalException.class,
        () -> apiClient("token-123")
            .get(server.url("/api/v3/secrets/raw").toString(), Collections.emptyMap(), Reply.class));

    assertTrue(exception.getMessage().contains("permission denied"), exception.getMessage());
  }

  @Test
  public void get_withMalformedBaseUrl_throwsInsteadOfNullPointer() {
    assertThrows(
        IllegalArgumentException.class,
        () -> apiClient("token-123").get("not-a-url", Collections.emptyMap(), Reply.class));
  }

  // CVE-2021-0341 guard. Builds its own client, since ApiClient cannot be given the test CA.
  @Test
  public void https_certificateIssuedForAnotherHost_isRejected() throws Exception {
    HeldCertificate rootCertificate = new HeldCertificate.Builder().certificateAuthority(0).build();
    HeldCertificate serverCertificate = new HeldCertificate.Builder()
        .commonName("example.com")
        .addSubjectAlternativeName("example.com")
        .signedBy(rootCertificate)
        .build();

    HandshakeCertificates serverCertificates = new HandshakeCertificates.Builder()
        .heldCertificate(serverCertificate, rootCertificate.certificate())
        .build();
    server.useHttps(serverCertificates.sslSocketFactory(), false);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"ok\"}"));

    HandshakeCertificates clientCertificates = new HandshakeCertificates.Builder()
        .addTrustedCertificate(rootCertificate.certificate())
        .build();
    OkHttpClient client = new OkHttpClient.Builder()
        .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager())
        .build();

    Request request = new Request.Builder().url(server.url("/api/v3/secrets/raw")).build();

    assertThrows(SSLPeerUnverifiedException.class, () -> client.newCall(request).execute());
  }

  @Test
  public void https_untrustedCertificate_failsBeforeTheRequestIsSent() throws Exception {
    HeldCertificate serverCertificate = new HeldCertificate.Builder()
        .commonName("localhost")
        .addSubjectAlternativeName("localhost")
        .build();
    HandshakeCertificates serverCertificates = new HandshakeCertificates.Builder()
        .heldCertificate(serverCertificate)
        .build();
    server.useHttps(serverCertificates.sslSocketFactory(), false);
    server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"message\":\"ok\"}"));

    assertThrows(
        InfisicalException.class,
        () -> apiClient("token-123")
            .get(server.url("/api/v3/secrets/raw").toString(), Collections.emptyMap(), Reply.class));

    assertEquals(0, server.getRequestCount());
  }
}
