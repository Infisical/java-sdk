package com.infisical.sdk.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class RevokeTokenInputTest {

  @Test
  public void validate_returnsNull_whenAccessTokenIsSet() {
    RevokeTokenInput input = RevokeTokenInput.builder().accessToken("token-123").build();
    assertNull(input.validate());
  }

  @Test
  public void validate_returnsMessage_whenAccessTokenIsNull() {
    RevokeTokenInput input = RevokeTokenInput.builder().accessToken(null).build();
    assertNotNull(input.validate());
  }

  @Test
  public void validate_returnsMessage_whenAccessTokenIsEmpty() {
    RevokeTokenInput input = RevokeTokenInput.builder().accessToken("").build();
    assertNotNull(input.validate());
  }

  @Test
  public void validate_returnsMessage_whenAccessTokenIsWhitespace() {
    RevokeTokenInput input = RevokeTokenInput.builder().accessToken("   ").build();
    assertNotNull(input.validate());
  }
}
