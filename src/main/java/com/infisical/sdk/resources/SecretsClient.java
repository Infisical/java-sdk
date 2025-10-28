package com.infisical.sdk.resources;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.api.QueryBuilder;
import com.infisical.sdk.models.CreateSecretInput;
import com.infisical.sdk.models.DeleteSecretInput;
import com.infisical.sdk.models.ListSecretsResponse;
import com.infisical.sdk.models.Secret;
import com.infisical.sdk.models.SingleSecretResponse;
import com.infisical.sdk.models.UpdateSecretInput;
import com.infisical.sdk.util.Helper;
import com.infisical.sdk.util.InfisicalException;
import java.util.List;
import java.util.Map;

public class SecretsClient {
  private final ApiClient httpClient;

  public SecretsClient(ApiClient apiClient) {
    this.httpClient = apiClient;
  }

  // Overload to avoid breaking changes for new setSecretsOnSystemProperties
  // parameter
  public List<Secret> ListSecrets(
      String projectId,
      String environmentSlug,
      String secretPath,
      Boolean expandSecretReferences,
      Boolean recursive,
      Boolean includeImports)
      throws InfisicalException {
    return ListSecrets(
        projectId,
        environmentSlug,
        secretPath,
        expandSecretReferences,
        recursive,
        includeImports,
        false);
  }

  public List<Secret> ListSecrets(
      String projectId,
      String environmentSlug,
      String secretPath,
      Boolean expandSecretReferences,
      Boolean recursive,
      Boolean includeImports,
      Boolean setSecretsOnSystemProperties)
      throws InfisicalException {
    String url = String.format("%s%s", this.httpClient.GetBaseUrl(), "/api/v3/secrets/raw");

    Map<String, String> queryParameters = new QueryBuilder()
        .add("workspaceId", projectId)
        .add("environment", environmentSlug)
        .add("secretPath", secretPath)
        .add("expandSecretReferences", Helper.booleanToString(expandSecretReferences))
        .add("recursive", Helper.booleanToString(recursive))
        .add("includeImports", Helper.booleanToString(includeImports))
        .build();

    ListSecretsResponse listSecrets = this.httpClient.get(url, queryParameters, ListSecretsResponse.class);

    if (setSecretsOnSystemProperties) {
      for (Secret secret : listSecrets.getSecrets()) {
        System.setProperty(secret.getSecretKey(), secret.getSecretValue());
      }
    }

    return listSecrets.getSecrets();
  }

  public Secret GetSecret(
      String secretName,
      String projectId,
      String environmentSlug,
      String secretPath,
      Boolean expandSecretReferences,
      Boolean includeImports,
      String secretType)
      throws InfisicalException {
    String url = String.format(
        "%s%s",
        this.httpClient.GetBaseUrl(), String.format("/api/v3/secrets/raw/%s", secretName));

    Map<String, String> queryParameters = new QueryBuilder()
        .add("workspaceId", projectId)
        .add("environment", environmentSlug)
        .add("secretPath", secretPath)
        .add("expandSecretReferences", Helper.booleanToString(expandSecretReferences))
        .add("includeImports", Helper.booleanToString(includeImports))
        .add("type", secretType)
        .build();

    SingleSecretResponse result = this.httpClient.get(url, queryParameters, SingleSecretResponse.class);

    return result.getSecret();
  }

  public Secret UpdateSecret(
      String secretName,
      String projectId,
      String environmentSlug,
      String secretPath,
      String newSecretValue,
      String newSecretName)
      throws InfisicalException {
    String url = String.format(
        "%s%s",
        this.httpClient.GetBaseUrl(), String.format("/api/v3/secrets/raw/%s", secretName));

    UpdateSecretInput.UpdateSecretInputBuilder inputBuilder = UpdateSecretInput.builder()
        .secretPath(secretPath)
        .projectId(projectId)
        .environmentSlug(environmentSlug)
        .newSecretName(newSecretName)
        .secretValue(newSecretValue);

    if (newSecretName != null)
      inputBuilder.newSecretName(newSecretName);
    if (newSecretName != null)
      inputBuilder.secretValue(newSecretValue);

    UpdateSecretInput requestInput = inputBuilder.build();

    SingleSecretResponse result = this.httpClient.patch(url, requestInput, SingleSecretResponse.class);

    return result.getSecret();
  }

  public Secret CreateSecret(
      String secretName,
      String secretValue,
      String projectId,
      String environmentSlug,
      String secretPath)
      throws InfisicalException {
    String url = String.format(
        "%s%s",
        this.httpClient.GetBaseUrl(), String.format("/api/v3/secrets/raw/%s", secretName));

    CreateSecretInput createSecretInput = CreateSecretInput.builder()
        .secretPath(secretPath)
        .projectId(projectId)
        .environmentSlug(environmentSlug)
        .secretValue(secretValue)
        .build();

    createSecretInput.setSecretValue(!secretValue.isEmpty() ? secretValue : "");

    SingleSecretResponse result = this.httpClient.post(url, createSecretInput, SingleSecretResponse.class);

    return result.getSecret();
  }

  public Secret DeleteSecret(
      String secretName, String projectId, String environmentSlug, String secretPath)
      throws InfisicalException {
    String url = String.format(
        "%s%s",
        this.httpClient.GetBaseUrl(), String.format("/api/v3/secrets/raw/%s", secretName));

    DeleteSecretInput deleteSecretInput = DeleteSecretInput.builder()
        .secretPath(secretPath)
        .projectId(projectId)
        .environmentSlug(environmentSlug)
        .build();

    SingleSecretResponse result = this.httpClient.delete(url, deleteSecretInput, SingleSecretResponse.class);
    return result.getSecret();
  }
}
