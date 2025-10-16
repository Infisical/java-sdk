package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import com.infisical.sdk.util.Helper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteFolderInput {
  @SerializedName("workspaceId")
  private String projectId;

  @SerializedName("environment")
  private String environmentSlug;

  @SerializedName("folderId")
  private String folderId;

  @SerializedName("path")
  @Builder.Default
  private String folderPath = "/";

  public String validate() {
    if (Helper.isNullOrEmpty(projectId)) {
      return "Project ID is required";
    }

    if (Helper.isNullOrEmpty(environmentSlug)) {
      return "Environment Slug is required";
    }

    if (Helper.isNullOrEmpty(folderId)) {
      return "Folder ID is required";
    }

    if (Helper.isNullOrEmpty(folderPath)) {
      return "Folder Path is required";
    }

    return null;
  }
}
