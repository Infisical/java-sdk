package com.infisical.sdk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.EnvironmentVariables;
import com.infisical.sdk.util.InfisicalException;
import com.infisical.sdk.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfisicalSdkTest {
  private static final Logger logger = LoggerFactory.getLogger(InfisicalSdkTest.class);

  @Test
  public void TestListSecrets() {
    var envVars = new EnvironmentVariables();

    var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      var secrets =
          sdk.Secrets().ListSecrets(envVars.getProjectId(), "dev", "/", false, false, false, false);
      logger.info("Secrets length {}", secrets.size());

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestGetSecret() {
    var envVars = new EnvironmentVariables();

    var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      var secret =
          sdk.Secrets().GetSecret("SECRET", envVars.getProjectId(), "dev", "/", null, null, null);

      logger.info("TestGetSecret: Secret {}", secret);

      if (secret == null) {
        throw new AssertionError("Secret not found");
      }

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestUpdateSecret() {
    var envVars = new EnvironmentVariables();

    var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      var updatedSecret =
          sdk.Secrets()
              .UpdateSecret("SECRET", envVars.getProjectId(), "dev", "/", "new-value-123", null);

      logger.info("TestUpdateSecret: Secret {}", updatedSecret);

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestCreateSecret() {
    var envVars = new EnvironmentVariables();

    var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      var secretValue = RandomUtil.generateRandomString(36);
      var secretName = RandomUtil.generateRandomString(36);

      var secret =
          sdk.Secrets().CreateSecret(secretName, secretValue, envVars.getProjectId(), "dev", "/");

      if (secret == null) {
        throw new AssertionError("Secret not found");
      }

      logger.info("TestCreateSecret: Secret {}", secret);
      sdk.Secrets().DeleteSecret(secretName, envVars.getProjectId(), "dev", "/");

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestDeleteSecret() {
    var envVars = new EnvironmentVariables();

    var sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      var secretValue = RandomUtil.generateRandomString(36);
      var secretName = RandomUtil.generateRandomString(36);

      sdk.Secrets().CreateSecret(secretName, secretValue, envVars.getProjectId(), "dev", "/");

      var deletedSecret =
          sdk.Secrets().DeleteSecret(secretName, envVars.getProjectId(), "dev", "/");

      if (deletedSecret == null) {
        throw new AssertionError("Secret not found");
      }

      logger.info("TestDeleteSecret: Secret {}", deletedSecret);

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }
}
