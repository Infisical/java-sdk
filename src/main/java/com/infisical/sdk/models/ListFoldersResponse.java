package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListFoldersResponse {
  @SerializedName("folders")
  private List<Folder> folders;

  public List<Folder> getFolders() {
    return folders;
  }
}
