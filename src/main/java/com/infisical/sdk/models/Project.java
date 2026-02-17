package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Project {

  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  @SerializedName("slug")
  private String slug;

  @SerializedName("orgId")
  private String orgId;

  @SerializedName("description")
  private String description;
}
