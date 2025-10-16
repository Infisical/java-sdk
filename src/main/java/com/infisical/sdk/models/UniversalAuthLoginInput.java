package com.infisical.sdk.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UniversalAuthLoginInput {
  private String clientId;
  private String clientSecret;
}
