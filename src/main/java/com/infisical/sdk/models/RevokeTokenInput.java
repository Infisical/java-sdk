package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import com.infisical.sdk.util.Helper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevokeTokenInput {
  @SerializedName("accessToken")
  private String accessToken;

  public String validate() {
    if (Helper.isNullOrEmpty(accessToken)) {
      return "Access token is required";
    }
    return null;
  }
}
