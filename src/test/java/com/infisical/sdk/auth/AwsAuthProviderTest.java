package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthParameters;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

class AwsAuthProviderTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testFromCredentials() throws JsonProcessingException {
    final AwsAuthProvider provider =
        AwsAuthProvider.builder().overrideInstant(Instant.ofEpochSecond(1759446719)).build();
    final AwsAuthParameters loginInput =
        provider.fromCredentials(
            "us-west-2",
            AwsBasicCredentials.create("MOCK_ACCESS_KEY", "MOCK_SECRET_KEY"),
            "MOCK_SESSION_TOKEN");
    assertEquals("POST", loginInput.getIamHttpRequestMethod());

    final String decodedBody =
        new String(
            Base64.getDecoder().decode(loginInput.getIamRequestBody()), StandardCharsets.UTF_8);
    final Map<String, List<String>> bodyParams =
        Arrays.stream(decodedBody.split("&"))
            .map(
                item -> {
                  final String[] parts =
                      URLDecoder.decode(item, StandardCharsets.UTF_8).split("=", 2);
                  return Map.entry(parts[0], List.of(parts[1]));
                })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    assertEquals(provider.getParams(), bodyParams);

    final String decodedHeaders =
        new String(
            Base64.getDecoder().decode(loginInput.getIamRequestHeaders()), StandardCharsets.UTF_8);
    final Map<String, String> actualHeaders =
        objectMapper.readValue(decodedHeaders, new TypeReference<>() {});
    assertEquals(
        Map.ofEntries(
            Map.entry("Host", "sts.us-west-2.amazonaws.com"),
            Map.entry("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"),
            Map.entry("Content-Length", "43"),
            Map.entry(
                "x-amz-content-sha256",
                "ab821ae955788b0e33ebd34c208442ccfc2d406e2edc5e7a39bd6458fbb4f843"),
            Map.entry("X-Amz-Security-Token", "MOCK_SESSION_TOKEN"),
            Map.entry("X-Amz-Date", "20251002T231159Z"),
            Map.entry(
                "Authorization",
                "AWS4-HMAC-SHA256 Credential=MOCK_ACCESS_KEY/20251002/us-west-2/sts/aws4_request,"
                    + " SignedHeaders=content-type;host;x-amz-content-sha256;x-amz-date;x-amz-security-token,"
                    + " Signature=9b1b93454bea36297168ed67a861df12d17136f47cbdf5d23b1daa0fe704742b")),
        actualHeaders);
  }

  static Stream<Arguments> encodeParametersCases() {
    return Stream.of(
        // empty
        Arguments.of(Map.of(), ""),
        // simple
        Arguments.of(
            Map.ofEntries(Map.entry("a", List.of("123")), Map.entry("b", List.of("456"))),
            "a=123&b=456"),
        // sorting the key
        Arguments.of(
            Map.ofEntries(
                Map.entry("d", List.of("3")),
                Map.entry("a", List.of("0")),
                Map.entry("c", List.of("2")),
                Map.entry("b", List.of("1"))),
            "a=0&b=1&c=2&d=3"),
        Arguments.of(
            Map.ofEntries(Map.entry("a", List.of("!@#$%^&*(){}[]"))),
            "a=%21%40%23%24%25%5E%26*%28%29%7B%7D%5B%5D"));
  }

  @ParameterizedTest
  @MethodSource("encodeParametersCases")
  void testEncodeParameters(Map<String, List<String>> params, String expected) {
    assertEquals(expected, AwsAuthProvider.encodeParameters(params));
  }
}
