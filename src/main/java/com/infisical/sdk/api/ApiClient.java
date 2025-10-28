package com.infisical.sdk.api;

import com.google.gson.Gson;
import com.infisical.sdk.util.InfisicalException;
import com.squareup.okhttp.*;
import com.squareup.okhttp.Request;
import java.io.IOException;
import java.util.Map;

public class ApiClient {
  private final OkHttpClient client;

  private String accessToken;
  private String baseUrl;

  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public ApiClient(String baseUrl, String accessToken) {
    this.client = new OkHttpClient();
    this.baseUrl = baseUrl;

    if (accessToken != null) {
      this.accessToken = accessToken;
    }

    this.formatBaseUrl();
  }

  private void formatBaseUrl() {
    // Remove trailing slash if present
    if (this.baseUrl.endsWith("/")) {
      this.baseUrl = this.baseUrl.substring(0, this.baseUrl.length() - 1);
    }

    // Check if URL starts with protocol
    if (!this.baseUrl.matches("^[a-zA-Z]+://.*")) {
      this.baseUrl = "http://" + this.baseUrl;
    }

    // Add /api if not present
    if (this.baseUrl.endsWith("/api")) {
      this.baseUrl = this.baseUrl.substring(0, this.baseUrl.length() - 4);
    }
  }

  public String GetBaseUrl() {
    return this.baseUrl;
  }

  @SuppressWarnings(value = "lombok")
  public OkHttpClient getClient() {
    return this.client;
  }

  private String formatErrorMessage(Response response) throws IOException {
    String message = String.format("Unexpected response: %s", response);

    try (ResponseBody errorBody = response.body()) {
      if (errorBody != null) {
        String bodyString = errorBody.string();
        if (!bodyString.isEmpty()) {
          message += String.format(" - %s", bodyString);
        }
      }
    }

    return message;
  }

  public <T, R> R post(String url, T requestBody, Class<R> responseType) throws InfisicalException {
    try {
      // convert request to json
      Gson gson = new Gson();
      String jsonBody = gson.toJson(requestBody);

      // Build request
      Request.Builder requestBuilder = new Request.Builder()
          .url(url)
          .post(RequestBody.create(JSON, jsonBody))
          .header("Accept", "application/json");

      if (this.accessToken != null && !this.accessToken.isEmpty()) {
        requestBuilder.addHeader("Authorization", "Bearer " + this.accessToken);
      }
      Request request = requestBuilder.build();

      Response response = client.newCall(request).execute();
      try (ResponseBody responseBody = response.body()) {
        if (!response.isSuccessful()) {
          throw new IOException(this.formatErrorMessage(response));
        }

        if (responseBody == null) {
          throw new IOException("Response body is null");
        }

        String responseJson = responseBody.string();
        return gson.fromJson(responseJson, responseType);
      }
    } catch (IOException e) {
      throw new InfisicalException(e);
    }
  }

  public <R> R get(String baseUrl, Map<String, String> queryParams, Class<R> responseType)
      throws InfisicalException {
    try {
      HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

      if (queryParams != null) {
        queryParams.forEach(urlBuilder::addQueryParameter);
      }

      Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build()).get().header("Accept",
          "application/json");

      if (this.accessToken != null && !this.accessToken.isEmpty()) {
        requestBuilder.addHeader("Authorization", "Bearer " + this.accessToken);
      }

      Request request = requestBuilder.build();

      Response response = client.newCall(request).execute();
      try (ResponseBody responseBody = response.body()) {
        if (!response.isSuccessful()) {
          throw new IOException(this.formatErrorMessage(response));
        }

        if (responseBody == null) {
          throw new IOException("Response body is null");
        }

        Gson gson = new Gson();
        String responseJson = responseBody.string();
        return gson.fromJson(responseJson, responseType);
      }
    } catch (IOException e) {
      throw new InfisicalException(e);
    }
  }

  public <T, R> R patch(String url, T requestBody, Class<R> responseType)
      throws InfisicalException {
    try {
      // convert request to json
      Gson gson = new Gson();
      String jsonBody = gson.toJson(requestBody);

      // Build request
      Request.Builder requestBuilder = new Request.Builder()
          .url(url)
          .patch(RequestBody.create(JSON, jsonBody))
          .header("Accept", "application/json");

      if (this.accessToken != null && !this.accessToken.isEmpty()) {
        requestBuilder.addHeader("Authorization", "Bearer " + this.accessToken);
      }
      Request request = requestBuilder.build();

      Response response = client.newCall(request).execute();
      try (ResponseBody responseBody = response.body()) {
        if (!response.isSuccessful()) {
          throw new IOException(this.formatErrorMessage(response));
        }

        if (responseBody == null) {
          throw new IOException("Response body is null");
        }

        String responseJson = responseBody.string();
        return gson.fromJson(responseJson, responseType);
      }
    } catch (IOException e) {
      throw new InfisicalException(e);
    }
  }

  public <T, R> R delete(String url, T requestBody, Class<R> responseType)
      throws InfisicalException {
    try {
      // convert request to json
      Gson gson = new Gson();
      String jsonBody = gson.toJson(requestBody);

      // Build request
      Request.Builder requestBuilder = new Request.Builder()
          .url(url)
          .delete(RequestBody.create(JSON, jsonBody))
          .header("Accept", "application/json");

      if (this.accessToken != null && !this.accessToken.isEmpty()) {
        requestBuilder.addHeader("Authorization", "Bearer " + this.accessToken);
      }
      Request request = requestBuilder.build();

      Response response = client.newCall(request).execute();
      try (ResponseBody responseBody = response.body()) {
        if (!response.isSuccessful()) {
          throw new IOException(this.formatErrorMessage(response));
        }

        if (responseBody == null) {
          throw new IOException("Response body is null");
        }

        String responseJson = responseBody.string();
        return gson.fromJson(responseJson, responseType);
      }
    } catch (IOException e) {
      throw new InfisicalException(e);
    }
  }
}
