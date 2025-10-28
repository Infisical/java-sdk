package com.infisical.sdk.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthParameters;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

@Data
@Builder
public class AwsAuthProvider {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @NonNull
  @Builder.Default
  private final String serviceName = "sts";
  @NonNull
  @Builder.Default
  private final SdkHttpMethod httpMethod = SdkHttpMethod.POST;
  @NonNull
  @Builder.Default
  private final String endpointTemplate = "https://sts.%s.amazonaws.com";

  @NonNull
  @Builder.Default
  private final String contentType = "application/x-www-form-urlencoded; charset=utf-8";

  @NonNull
  @Builder.Default
  private final Map<String, List<String>> params = createParams();

  private final Instant overrideInstant;

  private static Map<String, List<String>> createParams() {
    Map<String, List<String>> map = new HashMap<>();
    map.put("Action", Collections.singletonList("GetCallerIdentity"));
    map.put("Version", Collections.singletonList("2011-06-15"));
    return Collections.unmodifiableMap(map);
  }

  /**
   * Create AwsAuthLoginInput from given AWS credentials.
   *
   * @param region       region of AWS identity
   * @param credentials  AWS credentials for creating the login input
   * @param sessionToken Session token for creating the login input
   * @return the AwsAuthLoginInput created from the given credentials for
   *         exchanging access token
   */
  public AwsAuthParameters fromCredentials(
      String region, AwsCredentials credentials, String sessionToken) {
    final AwsV4HttpSigner signer = AwsV4HttpSigner.create();
    final String iamRequestURL = String.format(endpointTemplate, region);
    final String iamRequestBody = encodeParameters(params);
    final SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
        .uri(URI.create(iamRequestURL))
        .method(httpMethod)
        .appendHeader("Content-Type", contentType);
    if (sessionToken != null) {
      requestBuilder.appendHeader("X-Amz-Security-Token", sessionToken);
    }
    final SdkHttpFullRequest request = requestBuilder.build();
    final SdkHttpRequest signedRequest = signer
        .sign(
            signingRequest -> {
              SignRequest.Builder<AwsCredentialsIdentity> req = signingRequest
                  .request(request)
                  .identity(credentials)
                  .payload(
                      ContentStreamProvider.fromByteArray(
                          iamRequestBody.getBytes(StandardCharsets.UTF_8)))
                  .putProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, serviceName)
                  .putProperty(AwsV4HttpSigner.REGION_NAME, region);
              if (overrideInstant != null) {
                req.putProperty(
                    HttpSigner.SIGNING_CLOCK, Clock.fixed(overrideInstant, ZoneOffset.UTC));
              }
            })
        .request();

    final Map<String, String> requestHeaders = signedRequest.headers().entrySet().stream()
        .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get(0)))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    requestHeaders.put("Content-Length", String.valueOf(iamRequestBody.length()));
    final String encodedHeader;
    try {
      encodedHeader = Base64.getEncoder()
          .encodeToString(
              objectMapper.writeValueAsString(requestHeaders).getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    final String encodedBody = Base64.getEncoder().encodeToString(iamRequestBody.getBytes(StandardCharsets.UTF_8));
    return AwsAuthParameters.builder()
        .iamHttpRequestMethod(httpMethod.name())
        .iamRequestHeaders(encodedHeader)
        .iamRequestBody(encodedBody)
        .build();
  }

  /**
   * Create AwsAuthLoginInput from the instance profile in the current
   * environment.
   *
   * @return the AwsAuthLoginInput created from the current instance profile for
   *         exchanging access
   *         token
   */
  public AwsAuthParameters fromInstanceProfile() {
    try (InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.create()) {
      final AwsSessionCredentials credentials = (AwsSessionCredentials) provider.resolveCredentials();
      final DefaultAwsRegionProviderChain regionProvider = DefaultAwsRegionProviderChain.builder().build();
      final Region region = regionProvider.getRegion();
      final String sessionToken = credentials.sessionToken();
      return fromCredentials(region.id(), credentials, sessionToken);
    }
  }

  /**
   * Encode given parameters with URL encoding for the body of form posting
   * request.
   *
   * @param params parameters mapping key to values to encode
   * @return URL-encoded string of the parameters
   */
  public static String encodeParameters(Map<String, List<String>> params) {
    return params.entrySet().stream()
        .flatMap(entry -> entry.getValue().stream()
            .map(item -> new AbstractMap.SimpleEntry<>(entry.getKey(), item)))
        .sorted(Map.Entry.comparingByKey())
        .map(entry -> {
          try {
            return String.format(
                "%s=%s",
                URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()),
                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.joining("&"));
  }

  public static AwsAuthProvider defaultProvider() {
    return builder().build();
  }
}
