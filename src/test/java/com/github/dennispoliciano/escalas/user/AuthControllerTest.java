package com.github.dennispoliciano.escalas.user;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void whenCredentialsAreValidThenLoginSucceeds() throws Exception {
        User user = new User("joao@email.com", passwordEncoder.encode("senha123"));
        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                    {"email":"joao@email.com","password":"senha123"}
                    """))
                .andExpect(status().isOk());
    }

    @Test
    void whenPasswordIsWrongThenLoginFails() throws Exception {
        User user = new User("joao@email.com", passwordEncoder.encode("senha123"));
        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                    {"email":"joao@email.com","password":"senhaErrada"}
                    """))
                .andExpect(status().isUnauthorized());
    }
}