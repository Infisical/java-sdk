package com.infisical.sdk.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.models.RevokeTokenInput;
import com.infisical.sdk.models.RevokeTokenResponse;
import com.infisical.sdk.util.InfisicalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthClientTest {

  @Mock
  private ApiClient apiClient;

  @Test
  public void RevokeToken_throwsWhenAccessTokenIsNull() {
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    InfisicalException ex = assertThrows(InfisicalException.class, () -> authClient.RevokeToken(null));
    assertEquals("Access token is required", ex.getMessage());
  }

  @Test
  public void RevokeToken_throwsWhenAccessTokenIsEmpty() {
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    InfisicalException ex = assertThrows(InfisicalException.class, () -> authClient.RevokeToken(""));
    assertEquals("Access token is required", ex.getMessage());
  }

  @Test
  public void RevokeToken_callsPostWithCorrectUrlAndBody() throws InfisicalException {
    when(apiClient.GetBaseUrl()).thenReturn("http://localhost");
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    authClient.RevokeToken("token-123");

    verify(apiClient).post(
        eq("http://localhost/api/v1/auth/token/revoke"),
        any(RevokeTokenInput.class),
        eq(RevokeTokenResponse.class));
  }

  @Test
  public void RevokeTokenById_throwsWhenTokenIdIsNull() {
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    InfisicalException ex =
        assertThrows(InfisicalException.class, () -> authClient.RevokeTokenById(null));
    assertEquals("Token ID is required", ex.getMessage());
  }

  @Test
  public void RevokeTokenById_throwsWhenTokenIdIsEmpty() {
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    InfisicalException ex =
        assertThrows(InfisicalException.class, () -> authClient.RevokeTokenById(""));
    assertEquals("Token ID is required", ex.getMessage());
  }

  @Test
  public void RevokeTokenById_callsPostWithCorrectUrl() throws InfisicalException {
    when(apiClient.GetBaseUrl()).thenReturn("http://localhost");
    when(apiClient.post(any(String.class), eq(RevokeTokenResponse.class)))
        .thenReturn(new RevokeTokenResponse());
    AuthClient authClient = new AuthClient(apiClient, token -> {});

    authClient.RevokeTokenById("id-123");

    verify(apiClient)
        .post(
            eq("http://localhost/api/v1/auth/token-auth/tokens/id-123/revoke"),
            eq(RevokeTokenResponse.class));
  }
}
