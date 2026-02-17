package com.infisical.sdk.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.models.Project;
import com.infisical.sdk.util.InfisicalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectsClientTest {

  @Mock
  private ApiClient apiClient;

  @Test
  public void GetBySlug_throwsWhenSlugIsNull() {
    ProjectsClient client = new ProjectsClient(apiClient);

    InfisicalException ex = assertThrows(InfisicalException.class, () -> client.GetBySlug(null));
    assertEquals("Project slug is required", ex.getMessage());
  }

  @Test
  public void GetBySlug_throwsWhenSlugIsEmpty() {
    ProjectsClient client = new ProjectsClient(apiClient);

    InfisicalException ex = assertThrows(InfisicalException.class, () -> client.GetBySlug(""));
    assertEquals("Project slug is required", ex.getMessage());
  }

  @Test
  public void GetBySlug_throwsWhenSlugContainsPathTraversal() {
    ProjectsClient client = new ProjectsClient(apiClient);

    InfisicalException ex =
        assertThrows(InfisicalException.class, () -> client.GetBySlug("../../../admin"));
    assertEquals(
        "Project slug must be 1–64 characters and contain only letters, numbers, hyphens, and underscores",
        ex.getMessage());
  }

  @Test
  public void GetBySlug_throwsWhenSlugContainsSlash() {
    ProjectsClient client = new ProjectsClient(apiClient);

    InfisicalException ex =
        assertThrows(InfisicalException.class, () -> client.GetBySlug("foo/bar"));
    assertEquals(
        "Project slug must be 1–64 characters and contain only letters, numbers, hyphens, and underscores",
        ex.getMessage());
  }

  @Test
  public void GetBySlug_throwsWhenSlugContainsInvalidCharacters() {
    ProjectsClient client = new ProjectsClient(apiClient);

    InfisicalException ex =
        assertThrows(InfisicalException.class, () -> client.GetBySlug("my project"));
    assertEquals(
        "Project slug must be 1–64 characters and contain only letters, numbers, hyphens, and underscores",
        ex.getMessage());
  }

  @Test
  public void GetBySlug_callsGetWithCorrectUrl() throws InfisicalException {
    when(apiClient.GetBaseUrl()).thenReturn("https://app.infisical.com");
    Project project = new Project();
    project.setId("proj-123");
    project.setSlug("my-project");
    when(apiClient.get(
            eq("https://app.infisical.com/api/v1/projects/slug/my-project"),
            eq(null),
            eq(Project.class)))
        .thenReturn(project);

    ProjectsClient client = new ProjectsClient(apiClient);
    Project result = client.GetBySlug("my-project");

    assertEquals("proj-123", result.getId());
    assertEquals("my-project", result.getSlug());
    verify(apiClient)
        .get(
            eq("https://app.infisical.com/api/v1/projects/slug/my-project"),
            eq(null),
            eq(Project.class));
  }

  @Test
  public void GetProjectIdBySlug_returnsIdFromGetBySlug() throws InfisicalException {
    when(apiClient.GetBaseUrl()).thenReturn("https://app.infisical.com");
    Project project = new Project();
    project.setId("proj-456");
    when(apiClient.get(
            eq("https://app.infisical.com/api/v1/projects/slug/acme"),
            eq(null),
            eq(Project.class)))
        .thenReturn(project);

    ProjectsClient client = new ProjectsClient(apiClient);
    String projectId = client.GetProjectIdBySlug("acme");

    assertEquals("proj-456", projectId);
  }
}
