package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class RevokeTokenResponse {
  @SerializedName("message")
  private String message;
}
