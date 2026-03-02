package com.infisical.sdk.resources;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.auth.AwsAuthProvider;
import com.infisical.sdk.models.AwsAuthLoginInput;
import com.infisical.sdk.models.LdapAuthLoginInput;
import com.infisical.sdk.models.MachineIdentityCredential;
import com.infisical.sdk.models.RevokeTokenInput;
import com.infisical.sdk.models.UniversalAuthLoginInput;
import com.infisical.sdk.util.InfisicalException;
import java.util.function.Consumer;

public class AuthClient {
  private final ApiClient apiClient;
  private final Consumer<String> onAuthenticate;
  private String currentAccessToken;

  public AuthClient(ApiClient apiClient, Consumer<String> onAuthenticate) {
    this.apiClient = apiClient;
    this.onAuthenticate = onAuthenticate;
  }

  public AuthClient(ApiClient apiClient, Consumer<String> onAuthenticate, String initialToken) {
    this.apiClient = apiClient;
    this.onAuthenticate = onAuthenticate;
    this.currentAccessToken = initialToken;
  }

  public void UniversalAuthLogin(String clientId, String clientSecret) throws InfisicalException {
    UniversalAuthLoginInput params = UniversalAuthLoginInput.builder().clientId(clientId).clientSecret(clientSecret)
        .build();

    String url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/universal-auth/login");
    MachineIdentityCredential credential = this.apiClient.post(url, params, MachineIdentityCredential.class);
    this.currentAccessToken = credential.getAccessToken();
    this.onAuthenticate.accept(this.currentAccessToken);
  }

  public void LdapAuthLogin(LdapAuthLoginInput input) throws InfisicalException {
    String validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    String url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/ldap-auth/login");
    MachineIdentityCredential credential = this.apiClient.post(url, input, MachineIdentityCredential.class);
    this.currentAccessToken = credential.getAccessToken();
    this.onAuthenticate.accept(this.currentAccessToken);
  }

  public void AwsAuthLogin(String identityId) throws InfisicalException {
    AwsAuthLogin(AwsAuthProvider.defaultProvider().fromInstanceProfile().toLoginInput(identityId));
  }

  public void AwsAuthLogin(AwsAuthLoginInput input) throws InfisicalException {
    String validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    String url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/aws-auth/login");
    MachineIdentityCredential credential = this.apiClient.post(url, input, MachineIdentityCredential.class);
    this.currentAccessToken = credential.getAccessToken();
    this.onAuthenticate.accept(this.currentAccessToken);
  }

  public void SetAccessToken(String accessToken) {
    this.currentAccessToken = accessToken;
    this.onAuthenticate.accept(accessToken);
  }

  public void RevokeToken() throws InfisicalException {
    RevokeToken(this.currentAccessToken);
  }

  public void RevokeToken(String accessToken) throws InfisicalException {
    RevokeTokenInput input = RevokeTokenInput.builder().accessToken(accessToken).build();

    String validationMsg = input.validate();
    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    String url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/token/revoke");
    this.apiClient.post(url, input, Void.class);
  }
}
