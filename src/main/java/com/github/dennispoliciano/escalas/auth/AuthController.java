package com.github.dennispoliciano.escalas.auth;

import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public String login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            return "Login realizado com sucesso.";
        } catch (BadCredentialsException e) {
            throw new ResponseStatusAuthException();
        }
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(request.email(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class ResponseStatusAuthException extends RuntimeException {
    }

    record LoginRequest(String email, String password) {
    }

    record RegisterRequest(
            @NotBlank(message = "O campo 'email' não pode ser vazio.")
            @Email(message = "O campo 'email' deve ser um endereço de e-mail válido.")
            String email,
            @NotBlank(message = "O campo 'password' não pode ser vazio.")
            String password) {
    }
}