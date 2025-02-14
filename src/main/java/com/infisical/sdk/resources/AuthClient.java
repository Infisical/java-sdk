package com.infisical.sdk.resources;

import com.infisical.sdk.models.MachineIdentityCredential;
import com.infisical.sdk.util.InfisicalException;
import java.util.function.Consumer;
import com.infisical.sdk.api.ApiClient;

import com.infisical.sdk.models.UniversalAuthLoginInput;

public class AuthClient {
  private final ApiClient apiClient;
  private final Consumer<String> onAuthenticate;

  public AuthClient(ApiClient apiClient, Consumer<String> onAuthenticate) {
    this.apiClient = apiClient;
    this.onAuthenticate = onAuthenticate;
  }

  public void UniversalAuthLogin(String clientId, String clientSecret) throws InfisicalException {
      var params = UniversalAuthLoginInput.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();

      var url = String.format("%s%s", this.apiClient.GetBaseUrl(), "/api/v1/auth/universal-auth/login");
      var credential = this.apiClient.post(url, params, MachineIdentityCredential.class);
      this.onAuthenticate.accept(credential.getAccessToken());
  }

  public void SetAccessToken(String accessToken) {
    this.onAuthenticate.accept(accessToken);
  }
}