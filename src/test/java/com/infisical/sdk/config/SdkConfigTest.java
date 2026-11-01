package com.infisical.sdk.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SdkConfigTest {

  @Test
  public void build_usesDefaultSiteUrl_whenNotSet() {
    SdkConfig config = new SdkConfig.Builder().build();
    assertEquals("https://app.infisical.com", config.getSiteUrl());
  }

  @Test
  public void build_throwsSiteUrlMessage_whenSiteUrlIsNull() {
    IllegalStateException e =
        assertThrows(IllegalStateException.class, () -> new SdkConfig.Builder().withSiteUrl(null).build());
    assertEquals("Site URL must be provided", e.getMessage());
  }

  @Test
  public void build_throwsSiteUrlMessage_whenSiteUrlIsEmpty() {
    IllegalStateException e =
        assertThrows(IllegalStateException.class, () -> new SdkConfig.Builder().withSiteUrl("").build());
    assertEquals("Site URL must be provided", e.getMessage());
  }
}
