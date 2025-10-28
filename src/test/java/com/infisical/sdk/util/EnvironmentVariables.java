package com.infisical.sdk.util;

public class EnvironmentVariables {
  private String machineIdentityClientId;
  private String machineIdentityClientSecret;
  private String projectId;
  private String siteUrl;

  public EnvironmentVariables() throws RuntimeException {
    this.machineIdentityClientId = System.getenv("INFISICAL_MACHINE_IDENTITY_CLIENT_ID");
    this.machineIdentityClientSecret = System.getenv("INFISICAL_MACHINE_IDENTITY_CLIENT_SECRET");
    this.projectId = System.getenv("INFISICAL_PROJECT_ID");
    this.siteUrl = System.getenv("INFISICAL_SITE_URL");

    if (machineIdentityClientId == null
        || machineIdentityClientSecret == null
        || projectId == null
        || siteUrl == null
        || machineIdentityClientId.isEmpty()
        || machineIdentityClientSecret.isEmpty()
        || siteUrl.isEmpty()
        || projectId.isEmpty()) {
      throw new RuntimeException("One or more required environment variables are not set");
    }
  }

  public String getMachineIdentityClientId() {
    return machineIdentityClientId;
  }

  public String getMachineIdentityClientSecret() {
    return machineIdentityClientSecret;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getSiteUrl() {
    return siteUrl;
  }
}
