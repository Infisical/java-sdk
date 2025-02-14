package com.infisical.sdk.models;


import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateSecretInput {
    @SerializedName("environment")
    private String environmentSlug;

    @SerializedName("workspaceId")
    private String projectId;

    @SerializedName("secretPath")
    private String secretPath;

    @SerializedName("secretValue")
    private String secretValue;

    @SerializedName("newSecretName")
    private String newSecretName;
}