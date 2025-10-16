package com.infisical.sdk.models;

import com.infisical.sdk.util.Helper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LdapAuthLoginInput {
  private String identityId;
  private String username;
  private String password;

  public String validate() {
    if (Helper.isNullOrEmpty(identityId)) {
      return "Identity ID is required";
    }

    if (Helper.isNullOrEmpty(username)) {
      return "Username is required";
    }

    if (Helper.isNullOrEmpty(password)) {
      return "Password is required";
    }

    return null;
  }
}
