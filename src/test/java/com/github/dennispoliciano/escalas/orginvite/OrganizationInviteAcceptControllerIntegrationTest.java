package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.auth.JwtService;
import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrganizationInviteAcceptControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private OrganizationInviteRepository organizationInviteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String tokenFor(User user) {
        return jwtService.generateToken(new UserPrincipal(
                user, orgMembershipRepository.findByUserId(user.getId())));
    }

    @Test
    void whenAcceptingValidInvite_thenCreatesMembershipAndMarksInviteUsed() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-accept", "Igreja Accept", "church", "Rua Accept, 1"));
        OrganizationInvite invite = organizationInviteRepository.save(
                new OrganizationInvite("token-valido", org, Instant.now().plus(1, ChronoUnit.DAYS)));
        User user = userRepository.save(new User("user.accept@email.com", passwordEncoder.encode("senha123")));

        mockMvc.perform(post("/invites/" + invite.getToken() + "/accept")
                        .header("Authorization", "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.used").value(true));

        assertTrue(orgMembershipRepository.findByUserAndOrganization(user, org)
                .filter(membership -> membership.getRole() == Role.MEMBER)
                .isPresent());
        assertTrue(organizationInviteRepository.findByToken("token-valido").orElseThrow().getUsed());
    }

    @Test
    void whenAcceptingExpiredInvite_thenReturns409() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-expired", "Igreja Expired", "church", "Rua Expired, 1"));
        OrganizationInvite invite = organizationInviteRepository.save(
                new OrganizationInvite("token-expirado", org, Instant.now().minus(1, ChronoUnit.DAYS)));
        User user = userRepository.save(new User("user.expired@email.com", passwordEncoder.encode("senha123")));

        mockMvc.perform(post("/invites/" + invite.getToken() + "/accept")
                        .header("Authorization", "Bearer " + tokenFor(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenAcceptingAlreadyUsedInvite_thenReturns409() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-used", "Igreja Used", "church", "Rua Used, 1"));
        OrganizationInvite invite = new OrganizationInvite("token-usado", org, Instant.now().plus(1, ChronoUnit.DAYS));
        invite.setUsed(true);
        organizationInviteRepository.save(invite);
        User user = userRepository.save(new User("user.used@email.com", passwordEncoder.encode("senha123")));

        mockMvc.perform(post("/invites/" + invite.getToken() + "/accept")
                        .header("Authorization", "Bearer " + tokenFor(user)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenTokenDoesNotExist_thenReturns404() throws Exception {
        User user = userRepository.save(new User("user.notfound@email.com", passwordEncoder.encode("senha123")));

        mockMvc.perform(post("/invites/token-inexistente/accept")
                        .header("Authorization", "Bearer " + tokenFor(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoTokenIsProvided_thenReturns401() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-401", "Igreja 401", "church", "Rua 401, 1"));
        OrganizationInvite invite = organizationInviteRepository.save(
                new OrganizationInvite("token-401", org, Instant.now().plus(1, ChronoUnit.DAYS)));

        mockMvc.perform(post("/invites/" + invite.getToken() + "/accept"))
                .andExpect(status().isUnauthorized());
    }
}
