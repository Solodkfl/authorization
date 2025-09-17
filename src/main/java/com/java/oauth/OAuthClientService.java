package com.java.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;

import com.java.domain.role.RoleEntity;
import com.java.domain.user.UserEntity;
import com.java.domain.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthClientService {

  @Value("${logout.redirect-url}")
  private String redirectUrl;

  private final UserRepository userRepository;
  private String msg = "Client not Found Exception: ";

  public RegisteredClient findById(String id) {
    UserEntity oAuthClient = userRepository.findById(Long.parseLong(id))
      .orElseThrow(() -> new IllegalArgumentException(msg + id));
    return loadClientByResult(oAuthClient);
  }

  public RegisteredClient findByClientId(String clientId) {
    UserEntity oAuthClient = userRepository.findByEmailAndUseYn(clientId, 'Y');
      // .orElseThrow(() -> new IllegalArgumentException(msg + clientId));
    return loadClientByResult(oAuthClient);
  }

  private RegisteredClient loadClientByResult(UserEntity user) {
    if(user != null) {
      return RegisteredClient
        .withId(user.getNo().toString())
        .clientId(user.getEmail())
        .clientName(user.getName())
        .clientSecret(user.getName())
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .redirectUri(redirectUrl)
        .postLogoutRedirectUri(redirectUrl)
        .scopes(scope -> {
          for (RoleEntity role : user.getRoles()) {
            scope.add(role.getName());
          }
        })
        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
        .build();
    }
    return null;
  }
  
}
