package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthParameters;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
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
    final AwsAuthProvider provider = AwsAuthProvider.builder().overrideInstant(Instant.ofEpochSecond(1759446719))
        .build();
    final AwsAuthParameters loginInput = provider.fromCredentials(
        "us-west-2",
        AwsBasicCredentials.create("MOCK_ACCESS_KEY", "MOCK_SECRET_KEY"),
        "MOCK_SESSION_TOKEN");
    assertEquals("POST", loginInput.getIamHttpRequestMethod());

    final String decodedBody = new String(
        Base64.getDecoder().decode(loginInput.getIamRequestBody()), StandardCharsets.UTF_8);
    final Map<String, List<String>> bodyParams = Arrays.stream(decodedBody.split("&"))
        .map(item -> {
          try {
            final String[] parts = URLDecoder.decode(item, StandardCharsets.UTF_8.name()).split("=", 2);
            return new AbstractMap.SimpleEntry<String, List<String>>(
                parts[0],
                Collections.singletonList(parts[1]));
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    assertEquals(provider.getParams(), bodyParams);

    final String decodedHeaders = new String(
        Base64.getDecoder().decode(loginInput.getIamRequestHeaders()), StandardCharsets.UTF_8);
    final Map<String, String> actualHeaders = objectMapper.readValue(decodedHeaders,
        new TypeReference<Map<String, String>>() {
        });

    Map<String, String> expectedMap = new HashMap<>();
    expectedMap.put("Host", "sts.us-west-2.amazonaws.com");
    expectedMap.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    expectedMap.put("Content-Length", "43");
    expectedMap.put(
        "x-amz-content-sha256",
        "ab821ae955788b0e33ebd34c208442ccfc2d406e2edc5e7a39bd6458fbb4f843");
    expectedMap.put("X-Amz-Security-Token", "MOCK_SESSION_TOKEN");
    expectedMap.put("X-Amz-Date", "20251002T231159Z");
    expectedMap.put(
        "Authorization",
        "AWS4-HMAC-SHA256 Credential=MOCK_ACCESS_KEY/20251002/us-west-2/sts/aws4_request,"
            + " SignedHeaders=content-type;host;x-amz-content-sha256;x-amz-date;x-amz-security-token,"
            + " Signature=9b1b93454bea36297168ed67a861df12d17136f47cbdf5d23b1daa0fe704742b");

    assertEquals(expectedMap, actualHeaders);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, List<String>> createMap(Object... keyValues) {
    if (keyValues.length % 2 != 0) {
      throw new IllegalArgumentException("Must have even number of arguments");
    }
    Map<String, List<String>> map = new HashMap<>();
    for (int i = 0; i < keyValues.length; i += 2) {
      map.put((String) keyValues[i], (List<String>) keyValues[i + 1]);
    }
    return map;
  }

  static Stream<Arguments> encodeParametersCases() {
    return Stream.of(
        // empty
        Arguments.of(Collections.emptyMap(), ""),
        // simple
        Arguments.of(
            createMap(
                "a", Collections.singletonList("123"),
                "b", Collections.singletonList("456")),
            "a=123&b=456"),
        // sorting the key
        Arguments.of(
            createMap(
                "d", Collections.singletonList("3"),
                "a", Collections.singletonList("0"),
                "c", Collections.singletonList("2"),
                "b", Collections.singletonList("1")),
            "a=0&b=1&c=2&d=3"),
        Arguments.of(
            createMap("a", Collections.singletonList("!@#$%^&*(){}[]")),
            "a=%21%40%23%24%25%5E%26*%28%29%7B%7D%5B%5D"));
  }

  @ParameterizedTest
  @MethodSource("encodeParametersCases")
  void testEncodeParameters(Map<String, List<String>> params, String expected) {
    assertEquals(expected, AwsAuthProvider.encodeParameters(params));
  }
}
