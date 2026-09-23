package com.github.dennispoliciano.escalas.accessrequest;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.auth.JwtService;
import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccessRequestControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String tokenFor(User user) {
        return jwtService.generateToken(new UserPrincipal(
                user, orgMembershipRepository.findByUserId(user.getId())));
    }

    @Test
    void whenAuthenticatedUserRequestsAccessWithValidCode_thenCreatesPendingAccessRequest() throws Exception {
        User user = userRepository.save(new User("joao@email.com", passwordEncoder.encode("senha123")));
        organizationRepository.save(new Organization("igreja-001", "Igreja Batista Central", "church", "Av. Paulista, 999"));

        mockMvc.perform(post("/organizations/access-requests")
                        .header("Authorization", "Bearer " + tokenFor(user))
                        .contentType("application/json")
                        .content("""
                            {"organizationCode":"igreja-001"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.requestedAt").isNotEmpty());
    }

    @Test
    void whenOrganizationCodeDoesNotExist_thenReturns404() throws Exception {
        User user = userRepository.save(new User("maria@email.com", passwordEncoder.encode("senha123")));

        mockMvc.perform(post("/organizations/access-requests")
                        .header("Authorization", "Bearer " + tokenFor(user))
                        .contentType("application/json")
                        .content("""
                            {"organizationCode":"codigo-inexistente"}
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUserAlreadyHasPendingRequestForOrganization_thenReturns409() throws Exception {
        User user = userRepository.save(new User("pedro@email.com", passwordEncoder.encode("senha123")));
        Organization org = organizationRepository.save(new Organization("igreja-002", "Igreja Fonte", "church", "Rua A, 1"));
        accessRequestRepository.save(new AccessRequest(user, org));

        mockMvc.perform(post("/organizations/access-requests")
                        .header("Authorization", "Bearer " + tokenFor(user))
                        .contentType("application/json")
                        .content("""
                            {"organizationCode":"igreja-002"}
                            """))
                .andExpect(status().isConflict());
    }

    @Test
    void whenNoTokenIsProvided_thenReturns401() throws Exception {
        mockMvc.perform(post("/organizations/access-requests")
                        .contentType("application/json")
                        .content("""
                            {"organizationCode":"igreja-001"}
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenListingAccessRequestsByOrganization_thenReturnsOnlyPendingFromThatOrganization() throws Exception {
        User john = userRepository.save(new User("john@email.com", passwordEncoder.encode("senha123")));
        User mary = userRepository.save(new User("mary2@email.com", passwordEncoder.encode("senha123")));
        User peter = userRepository.save(new User("peter2@email.com", passwordEncoder.encode("senha123")));
        Organization orgA = organizationRepository.save(new Organization("igreja-a", "Organization A", "church", "Address A"));
        Organization orgB = organizationRepository.save(new Organization("igreja-b", "Organization B", "church", "Address B"));

        AccessRequest pendingInOrgA = accessRequestRepository.save(new AccessRequest(john, orgA));
        AccessRequest approvedInOrgA = new AccessRequest(mary, orgA);
        approvedInOrgA.setStatus(AccessRequestStatus.APPROVED);
        accessRequestRepository.save(approvedInOrgA);
        accessRequestRepository.save(new AccessRequest(peter, orgB));

        mockMvc.perform(get("/organizations/" + orgA.getId() + "/access-requests")
                        .header("Authorization", "Bearer " + tokenFor(john))
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(pendingInOrgA.getId()));
    }

    @Test
    void whenNoTokenIsProvidedForListing_thenReturns401() throws Exception {
        Organization org = organizationRepository.save(new Organization("igreja-c", "Organization C", "church", "Address C"));

        mockMvc.perform(get("/organizations/" + org.getId() + "/access-requests"))
                .andExpect(status().isUnauthorized());
    }

}
