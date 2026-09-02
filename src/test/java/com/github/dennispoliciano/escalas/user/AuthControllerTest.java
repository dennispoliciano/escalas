package com.github.dennispoliciano.escalas.user;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    void whenEmailDoesNotExistThenLoginFails() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                    {"email":"naoexiste@email.com","password":"senha123"}
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenUserIsRegisteredThenPasswordIsNeverExposedInResponse() throws Exception {
        String rawPassword = "senha123";

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("""
                    {"email":"maria@email.com","password":"%s"}
                    """.formatted(rawPassword)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        User persistedUser = userRepository.findByEmail("maria@email.com").orElseThrow();
        assertNotEquals(rawPassword, persistedUser.getPassword());
        assertTrue(passwordEncoder.matches(rawPassword, persistedUser.getPassword()));
    }
}