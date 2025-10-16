package com.infisical.sdk.config;

public class SdkConfig {
  private final String siteUrl;

  private SdkConfig(Builder builder) {
    this.siteUrl = builder.siteUrl;
  }

  public String getSiteUrl() {
    return siteUrl;
  }

  public static class Builder {
    private String siteUrl = "https://app.infisical.com";

    public Builder withSiteUrl(String siteUrl) {
      this.siteUrl = siteUrl;
      return this;
    }

    public SdkConfig build() {
      if (siteUrl == null || siteUrl.isEmpty()) {
        throw new IllegalStateException("Token must be provided");
      }

      return new SdkConfig(this);
    }
  }
}
