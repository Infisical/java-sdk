package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Folder {
  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  @SerializedName("version")
  private String version;

  @SerializedName("createdAt")
  private String createdAt;

  @SerializedName("updatedAt")
  private String updatedAt;

  @SerializedName("envId")
  private String environmentId;

  @SerializedName("parentId")
  private String parentId;

  @SerializedName("isReserved")
  private boolean isReserved;

  @SerializedName("description")
  private String description;

  @SerializedName("lastSecretModified")
  private String lastSecretModified;
}
