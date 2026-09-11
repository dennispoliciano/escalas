package com.github.dennispoliciano.escalas.auth;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.user.AuthProvider;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class OAuth2AuthenticationSuccessHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private OAuth2AuthenticationToken authenticationFor(String email) {
        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"),
                Map.of("email", email, "sub", "google-" + email),
                "email"
        );
        return new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "google");
    }

    @Test
    void whenFirstGoogleLogin_thenCreatesUserWithGoogleProviderAndReturnsValidToken() throws Exception {
        String email = "novo.usuario@gmail.com";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        successHandler.onAuthenticationSuccess(request, response, authenticationFor(email));

        AuthController.TokenResponse tokenResponse =
                OBJECT_MAPPER.readValue(response.getContentAsString(), AuthController.TokenResponse.class);

        assertTrue(jwtService.isTokenValid(tokenResponse.token()));
        assertEquals(email, jwtService.extractEmail(tokenResponse.token()));

        User persistedUser = userRepository.findByEmail(email).orElseThrow();
        assertEquals(AuthProvider.GOOGLE, persistedUser.getAuthProvider());
    }

    @Test
    void whenGoogleLoginWithEmailOfExistingLocalUser_thenLinksToExistingUserWithoutDuplicating() throws Exception {
        String email = "joao@email.com";
        userRepository.save(new User(email, passwordEncoder.encode("senha123")));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        successHandler.onAuthenticationSuccess(request, response, authenticationFor(email));

        long countWithEmail = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .count();
        assertEquals(1, countWithEmail);

        User persistedUser = userRepository.findByEmail(email).orElseThrow();
        assertEquals(AuthProvider.LOCAL, persistedUser.getAuthProvider());

        AuthController.TokenResponse tokenResponse =
                OBJECT_MAPPER.readValue(response.getContentAsString(), AuthController.TokenResponse.class);
        assertTrue(jwtService.isTokenValid(tokenResponse.token()));
    }
}