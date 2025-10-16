package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListSecretsResponse {
  @SerializedName("secrets")
  private List<Secret> secrets;

  public List<Secret> getSecrets() {
    return secrets;
  }
}
