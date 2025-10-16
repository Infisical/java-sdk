package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class SingleSecretResponse {
  @SerializedName("secret")
  private Secret secret;

  @SerializedName("approval")
  private Approval approval;
}
