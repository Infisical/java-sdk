package com.infisical.sdk.resources;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.models.*;
import com.infisical.sdk.util.Helper;
import com.infisical.sdk.util.InfisicalException;
import com.infisical.sdk.util.ObjectToMapConverter;
import java.util.List;

public class FoldersClient {
  private final ApiClient httpClient;

  public FoldersClient(ApiClient apiClient) {
    this.httpClient = apiClient;
  }

  public Folder Create(CreateFolderInput input) throws InfisicalException {
    var url = String.format("%s%s", this.httpClient.GetBaseUrl(), "/api/v1/folders");

    var result = this.httpClient.post(url, input, SingleFolderResponse.class);

    return result.getFolder();
  }

  public Folder Get(String folderId) throws InfisicalException {
    if (Helper.isNullOrEmpty(folderId)) {
      throw new InfisicalException("Folder ID is required");
    }

    var url =
        String.format(
            "%s%s", this.httpClient.GetBaseUrl(), String.format("/api/v1/folders/%s", folderId));
    var result = this.httpClient.get(url, null, SingleFolderResponse.class);

    return result.getFolder();
  }

  public List<Folder> List(ListFoldersInput input) throws InfisicalException {
    var validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    var url = String.format("%s%s", this.httpClient.GetBaseUrl(), "/api/v1/folders");
    var result =
        this.httpClient.get(
            url, ObjectToMapConverter.toStringMap(input), ListFoldersResponse.class);

    return result.getFolders();
  }

  public Folder Update(UpdateFolderInput input) throws InfisicalException {
    var validationMsg = input.validate();

    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    var url =
        String.format(
            "%s%s",
            this.httpClient.GetBaseUrl(), String.format("/api/v1/folders/%s", input.getFolderId()));

    var result = this.httpClient.patch(url, input, SingleFolderResponse.class);

    return result.getFolder();
  }

  public Folder Delete(DeleteFolderInput input) throws InfisicalException {
    var validationMsg = input.validate();
    if (validationMsg != null) {
      throw new InfisicalException(validationMsg);
    }

    var url =
        String.format(
            "%s%s",
            this.httpClient.GetBaseUrl(), String.format("/api/v1/folders/%s", input.getFolderId()));

    var result = this.httpClient.delete(url, input, SingleFolderResponse.class);

    return result.getFolder();
  }
}
