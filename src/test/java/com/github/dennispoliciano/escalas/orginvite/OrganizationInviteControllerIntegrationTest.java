package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.auth.JwtService;
import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrganizationInviteControllerIntegrationTest extends AbstractIntegrationTest {

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
    void whenOrgAdminGeneratesInvite_thenReturnsTokenAndExpiration() throws Exception {
        User admin = userRepository.save(new User("admin.invite@email.com", passwordEncoder.encode("senha123")));
        Organization org = organizationRepository.save(new Organization("igreja-invite", "Igreja Invite", "church", "Rua Invite, 1"));
        orgMembershipRepository.save(new OrgMembership(admin, org, Role.ORG_ADMIN));

        mockMvc.perform(post("/organizations/" + org.getId() + "/invites")
                        .header("Authorization", "Bearer " + tokenFor(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationId").value(org.getId()))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());

        assertEquals(1, organizationInviteRepository.findAll().size());
    }

    @Test
    void whenNonOrgAdminGeneratesInvite_thenReturns403() throws Exception {
        User member = userRepository.save(new User("member.invite@email.com", passwordEncoder.encode("senha123")));
        Organization org = organizationRepository.save(new Organization("igreja-invite-member", "Igreja Invite Member", "church", "Rua Invite, 2"));
        orgMembershipRepository.save(new OrgMembership(member, org, Role.MEMBER));

        mockMvc.perform(post("/organizations/" + org.getId() + "/invites")
                        .header("Authorization", "Bearer " + tokenFor(member)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenOrganizationDoesNotExist_thenReturns404() throws Exception {
        User admin = userRepository.save(new User("admin.invite.404@email.com", passwordEncoder.encode("senha123")));
        Organization org = organizationRepository.save(new Organization("igreja-invite-404", "Igreja Invite 404", "church", "Rua Invite, 3"));
        orgMembershipRepository.save(new OrgMembership(admin, org, Role.ORG_ADMIN));

        mockMvc.perform(post("/organizations/999999/invites")
                        .header("Authorization", "Bearer " + tokenFor(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenNoTokenIsProvided_thenReturns401() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-invite-401", "Igreja Invite 401", "church", "Rua Invite, 4"));

        mockMvc.perform(post("/organizations/" + org.getId() + "/invites"))
                .andExpect(status().isUnauthorized());
    }
}
