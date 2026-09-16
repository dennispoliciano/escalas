package com.github.dennispoliciano.escalas.group;

import tools.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GroupAuthorizationTest extends AbstractIntegrationTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenFor(String email, Role role) {
        User user = new User(email, passwordEncoder.encode("senha123"));
        userRepository.save(user);

        Organization organization = new Organization("igr-01", "Igreja Central", "Igreja", "Rua Principal, 100");
        organizationRepository.save(organization);
        orgMembershipRepository.save(new OrgMembership(user, organization, role));

        return jwtService.generateToken(new UserPrincipal(user, orgMembershipRepository.findByUserId(user.getId())));
    }

    @Test
    void whenUserHasOrgAdminRole_thenCreateGroupReturns200() throws Exception {
        String token = tokenFor("admin@email.com", Role.ORG_ADMIN);
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");

        mockMvc.perform(post("/groups")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserDoesNotHaveOrgAdminRole_thenCreateGroupReturns403() throws Exception {
        String token = tokenFor("membro@email.com", Role.MEMBER);
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");

        mockMvc.perform(post("/groups")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isForbidden());
    }
}
