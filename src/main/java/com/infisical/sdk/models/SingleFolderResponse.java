package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;

public class SingleFolderResponse {
  @SerializedName("folder")
  private Folder folder;

  public Folder getFolder() {
    return folder;
  }
}
