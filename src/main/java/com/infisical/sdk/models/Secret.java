package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Secret {
  @SerializedName("id")
  private String id;

  @SerializedName("workspace")
  private String projectId;

  @SerializedName("environment")
  private String environment;

  @SerializedName("version")
  private String version;

  @SerializedName("type")
  private String type;

  @SerializedName("secretKey")
  private String secretKey;

  @SerializedName("secretValue")
  private String secretValue;

  @SerializedName("secretComment")
  private String secretComment;

  @SerializedName("createdAt")
  private String createdAt;

  @SerializedName("updatedAt")
  private String updatedAt;

  @SerializedName("secretPath")
  private String secretPath;
}
