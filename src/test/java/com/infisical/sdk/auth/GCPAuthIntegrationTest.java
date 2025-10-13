package com.infisical.sdk.auth;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class GCPAuthIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(GCPAuthIntegrationTest.class);
    @Test
    public void testGCPAuthAndFetchSecrets() throws Exception {

        // Load env variables
        var envVars = new EnvironmentVariables();

        // Get Machine Identity Id
        String machineIdentityId = System.getenv("INFISICAL_MACHINE_IDENTITY_ID");


        // Check if env variable machine identity is set others are already tested via env tests
        assertNotNull(machineIdentityId, "INFISICAL_MACHINE_IDENTITY_ID env variable must be set");


        // Create SDK instance
        var sdk = new InfisicalSdk(new SdkConfig.Builder()
                .withSiteUrl(envVars.getSiteUrl())
                .build()
        );

        // Authenticate using GCP Auth
        assertDoesNotThrow(() -> {
            sdk.Auth().GCPAuthLogin(machineIdentityId);
        });



        try {

            // Test if we have correctly logged in and we can list the secrets
            var secrets = sdk.Secrets().ListSecrets(
                                    envVars.getProjectId(), 
                                    "dev", 
                                    "/",
                                    null,
                                    null,
                                    null);
                
            assertNotNull(secrets, "Secrets list should not be null");
            assertFalse(secrets.isEmpty(), "Secrets list should not be empty");

            logger.info("TestGCPAuth Successful");
            logger.info("Secrets length :  {}", secrets.size());

        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
