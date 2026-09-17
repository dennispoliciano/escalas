package com.github.dennispoliciano.escalas.organization;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.auth.JwtService;
import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembership;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.orgmembership.Role;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrganizationOnboardingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    void whenAuthenticatedUserOnboardsOrganization_thenOrgMembershipIsPersistedWithOrgAdmin() throws Exception {
        User user = new User("joao@email.com", passwordEncoder.encode("senha123"));
        userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(
                user, orgMembershipRepository.findByUserId(user.getId())));

        mockMvc.perform(post("/organizations/onboarding")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                            {"code":"001","name":"Igreja Batista Central","type":"Igreja","address":"Av. Paulista, 999"}
                            """))
                .andExpect(status().isOk());

        Organization organization = organizationRepository.findByName("Igreja Batista Central").orElseThrow();
        OrgMembership membership = orgMembershipRepository
                .findByUserAndOrganization(user, organization)
                .orElseThrow();

        assertEquals(Role.ORG_ADMIN, membership.getRole());
        assertEquals(user.getId(), membership.getUser().getId());
    }

    @Test
    void whenNoTokenIsProvided_thenReturns401() throws Exception {
        mockMvc.perform(post("/organizations/onboarding")
                        .contentType("application/json")
                        .content("""
                            {"code":"001","name":"Igreja Batista Central","type":"Igreja","address":"Av. Paulista, 999"}
                            """))
                .andExpect(status().isUnauthorized());
    }
}