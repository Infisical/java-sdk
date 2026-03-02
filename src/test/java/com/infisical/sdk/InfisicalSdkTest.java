package com.infisical.sdk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.models.Secret;
import com.infisical.sdk.util.EnvironmentVariables;
import com.infisical.sdk.util.InfisicalException;
import com.infisical.sdk.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfisicalSdkTest {
  private static final Logger logger = LoggerFactory.getLogger(InfisicalSdkTest.class);

  @Test
  public void TestRevokeToken() {
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(() -> {
      sdk.Auth().UniversalAuthLogin(envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
    });

    assertDoesNotThrow(() -> sdk.Auth().RevokeToken());

    // Verify the token is actually revoked — revoking it again should fail
    assertThrows(InfisicalException.class, () -> sdk.Auth().RevokeToken());
  }

  @Test
  public void TestListSecrets() {
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      List<Secret> secrets = sdk.Secrets().ListSecrets(envVars.getProjectId(), "dev", "/", false, false, false, false);
      logger.info("Secrets length {}", secrets.size());

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestGetSecret() {
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      Secret secret = sdk.Secrets().GetSecret("SECRET", envVars.getProjectId(), "dev", "/", null, null, null);

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
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      Secret updatedSecret = sdk.Secrets()
          .UpdateSecret("SECRET", envVars.getProjectId(), "dev", "/", "new-value-123", null);

      logger.info("TestUpdateSecret: Secret {}", updatedSecret);

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }

  @Test
  public void TestCreateSecret() {
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      String secretValue = RandomUtil.generateRandomString(36);
      String secretName = RandomUtil.generateRandomString(36);

      Secret secret = sdk.Secrets().CreateSecret(secretName, secretValue, envVars.getProjectId(), "dev", "/");

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
    EnvironmentVariables envVars = new EnvironmentVariables();

    InfisicalSdk sdk = new InfisicalSdk(new SdkConfig.Builder().withSiteUrl(envVars.getSiteUrl()).build());

    assertDoesNotThrow(
        () -> {
          sdk.Auth()
              .UniversalAuthLogin(
                  envVars.getMachineIdentityClientId(), envVars.getMachineIdentityClientSecret());
        });

    try {
      String secretValue = RandomUtil.generateRandomString(36);
      String secretName = RandomUtil.generateRandomString(36);

      sdk.Secrets().CreateSecret(secretName, secretValue, envVars.getProjectId(), "dev", "/");

      Secret deletedSecret = sdk.Secrets().DeleteSecret(secretName, envVars.getProjectId(), "dev", "/");

      if (deletedSecret == null) {
        throw new AssertionError("Secret not found");
      }

      logger.info("TestDeleteSecret: Secret {}", deletedSecret);

    } catch (InfisicalException e) {
      throw new AssertionError(e);
    }
  }
}
