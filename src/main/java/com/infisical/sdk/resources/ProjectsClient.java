package com.infisical.sdk.resources;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.models.Project;
import com.infisical.sdk.util.Helper;
import com.infisical.sdk.util.InfisicalException;
import java.util.regex.Pattern;

public class ProjectsClient {
  private final ApiClient apiClient;

  /** Allowed slug characters: alphanumeric, hyphen, underscore. Prevents path traversal. */
  private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,64}$");

  public ProjectsClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Fetches project details by slug. Requires authentication.
   *
   * @param slug the project slug (e.g. from the project URL)
   * @return the project including id, name, slug, orgId, etc.
   * @throws InfisicalException when slug is invalid or the API request fails
   */
  public Project GetBySlug(String slug) throws InfisicalException {
    if (Helper.isNullOrEmpty(slug)) {
      throw new InfisicalException("Project slug is required");
    }
    String trimmed = slug.trim();
    if (!SLUG_PATTERN.matcher(trimmed).matches()) {
      throw new InfisicalException(
          "Project slug must be 1–64 characters and contain only letters, numbers, hyphens, and underscores");
    }

    String url =
        String.format(
            "%s/api/v1/projects/slug/%s",
            this.apiClient.GetBaseUrl(), trimmed);
    return this.apiClient.get(url, null, Project.class);
  }

  /**
   * Returns the project ID for the given project slug. Convenience method that
   * calls GetBySlug and returns {@link Project#getId()}.
   *
   * @param slug the project slug
   * @return the project id
   * @throws InfisicalException when slug is invalid or the API request fails
   */
  public String GetProjectIdBySlug(String slug) throws InfisicalException {
    return GetBySlug(slug).getId();
  }
}
