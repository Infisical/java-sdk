package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Tag {
  @SerializedName("id")
  private String id;

  @SerializedName("slug")
  private String slug;

  @SerializedName("color")
  private String color;

  @SerializedName("name")
  private String name;
}
