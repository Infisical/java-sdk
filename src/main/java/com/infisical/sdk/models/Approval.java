package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

// Currently not supported for identities
@Data
public class Approval {
  @SerializedName("id")
  private String id;

  @SerializedName("policyId")
  private String policyId;

  @SerializedName("hasMerged")
  private boolean hasMerged;

  @SerializedName("status")
  private String status;

  @SerializedName("slug")
  private String slug;

  @SerializedName("folderId")
  private String folderId;

  @SerializedName("createdAt")
  private String createdAt;

  @SerializedName("updatedAt")
  private String updatedAt;

  @SerializedName("isReplicated")
  private boolean isReplicated;

  @SerializedName("committerUserId")
  private String committerUserId;

  @SerializedName("statusChangedByUserId")
  private String statusChangedByUserId;

  @SerializedName("bypassReason")
  private String bypassReason;
}
