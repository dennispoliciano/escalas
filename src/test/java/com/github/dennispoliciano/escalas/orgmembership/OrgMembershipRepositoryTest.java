package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrgMembershipRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenSameUserHasMembershipsInDifferentOrganizations_thenRolesShouldBeIndependentPerOrganization() {
        User user = userRepository.save(new User("john@email.com", "password"));
        Organization orgA = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        Organization orgB = organizationRepository.save(new Organization("org-b", "Organization B", "church", "Address B"));

        orgMembershipRepository.save(new OrgMembership(user, orgA, Role.ORG_ADMIN));
        orgMembershipRepository.save(new OrgMembership(user, orgB, Role.MEMBER));

        Optional<OrgMembership> membershipInOrgA = orgMembershipRepository.findByUserAndOrganization(user, orgA);
        Optional<OrgMembership> membershipInOrgB = orgMembershipRepository.findByUserAndOrganization(user, orgB);

        assertTrue(membershipInOrgA.isPresent());
        assertTrue(membershipInOrgB.isPresent());
        assertEquals(Role.ORG_ADMIN, membershipInOrgA.get().getRole());
        assertEquals(Role.MEMBER, membershipInOrgB.get().getRole());

        assertEquals(2, orgMembershipRepository.findByUserId(user.getId()).size());
    }

}
