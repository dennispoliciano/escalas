package com.github.dennispoliciano.escalas.auth;

import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.user.AuthProvider;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OrgMembershipRepository orgMembershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OAuth2AuthenticationSuccessHandler(
            UserRepository userRepository,
            OrgMembershipRepository orgMembershipRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.orgMembershipRepository = orgMembershipRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email));

        UserPrincipal userPrincipal = new UserPrincipal(user, orgMembershipRepository.findByUserId(user.getId()));
        String token = jwtService.generateToken(userPrincipal);

        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), new AuthController.TokenResponse(token));
    }

    private User createGoogleUser(String email) {
        String unusablePassword = passwordEncoder.encode(UUID.randomUUID().toString());
        User user = new User(email, unusablePassword);
        user.setAuthProvider(AuthProvider.GOOGLE);
        return userRepository.save(user);
    }
}