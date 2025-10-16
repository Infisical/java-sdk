package com.infisical.sdk.resources;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.auth.AwsAuthProvider;
import com.infisical.sdk.auth.GCPAuthProvider;
import com.infisical.sdk.models.AwsAuthLoginInput;
import com.infisical.sdk.models.LdapAuthLoginInput;
import com.infisical.sdk.models.MachineIdentityCredential;
import com.infisical.sdk.models.UniversalAuthLoginInput;
import com.infisical.sdk.util.InfisicalException;
import java.util.function.Consumer;

public class AuthClient {
  private final ApiClient apiClient;
  private final Consumer<String> onAuthenticate;

  public AuthClient(ApiClient apiClient, Consumer<String> onAuthenticate) {
    this.apiClient = apiClient;
    this.onAuthenticate = onAuthenticate;
  }

  public void UniversalAuthLogin(String clientId, String clientSecret) throws InfisicalException {
    var params =
        UniversalAuthLoginInput.builder().clientId(clientId).clientSecret(clientSecret).build();

    var url =
        String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/universal-auth/login");
    var credential = this.apiClient.post(url, params, MachineIdentityCredential.class);
    this.onAuthenticate.accept(credential.getAccessToken());
  }

  public void LdapAuthLogin(LdapAuthLoginInput input) throws InfisicalException {
    var validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    var url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/ldap-auth/login");
    var credential = this.apiClient.post(url, input, MachineIdentityCredential.class);
    this.onAuthenticate.accept(credential.getAccessToken());
  }

  public void AwsAuthLogin(String identityId) throws InfisicalException {
    AwsAuthLogin(AwsAuthProvider.defaultProvider().fromInstanceProfile().toLoginInput(identityId));
  }

  public void AwsAuthLogin(AwsAuthLoginInput input) throws InfisicalException {
    var validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    var url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/aws-auth/login");
    var credential = this.apiClient.post(url, input, MachineIdentityCredential.class);
    this.onAuthenticate.accept(credential.getAccessToken());
  }

  public void GCPAuthLogin(String identityId) throws InfisicalException {

    if (identityId == null||identityId.isEmpty())

      throw new InfisicalException("Infisical Identity ID is required");

    var url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/gcp-auth/login");

    var input = GCPAuthProvider.getGCPAuthInput(identityId);
    var credential = this.apiClient.post(url, input, MachineIdentityCredential.class);
    this.onAuthenticate.accept(credential.getAccessToken());
  }

  public void SetAccessToken(String accessToken) {
    this.onAuthenticate.accept(accessToken);
  }
}
