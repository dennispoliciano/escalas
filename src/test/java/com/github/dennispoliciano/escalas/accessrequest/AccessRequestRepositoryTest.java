package com.github.dennispoliciano.escalas.accessrequest;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccessRequestRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenAccessRequestIsCreated_thenShouldBePersistedAsPendingWithRequestedAt() {
        User user = userRepository.save(new User("john@email.com", "password"));
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));

        AccessRequest saved = accessRequestRepository.saveAndFlush(new AccessRequest(user, org));

        AccessRequest found = accessRequestRepository.findById(saved.getId()).orElseThrow();
        assertEquals(AccessRequestStatus.PENDING, found.getStatus());
        assertNotNull(found.getRequestedAt());
    }

    @Test
    void whenUserHasPendingRequestForOrganization_thenExistsShouldReturnTrue() {
        User user = userRepository.save(new User("john@email.com", "password"));
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        accessRequestRepository.save(new AccessRequest(user, org));

        boolean exists = accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(
                user.getId(), org.getId(), AccessRequestStatus.PENDING);

        assertTrue(exists);
    }

    @Test
    void whenUserHasPendingRequestOnlyForAnotherOrganization_thenExistsShouldReturnFalse() {
        User user = userRepository.save(new User("john@email.com", "password"));
        Organization orgA = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        Organization orgB = organizationRepository.save(new Organization("org-b", "Organization B", "church", "Address B"));
        accessRequestRepository.save(new AccessRequest(user, orgA));

        boolean exists = accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(
                user.getId(), orgB.getId(), AccessRequestStatus.PENDING);

        assertFalse(exists);
    }

    @Test
    void whenAnotherUserHasPendingRequestForSameOrganization_thenExistsShouldReturnFalse() {
        User john = userRepository.save(new User("john@email.com", "password"));
        User mary = userRepository.save(new User("mary@email.com", "password"));
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        accessRequestRepository.save(new AccessRequest(john, org));

        boolean exists = accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(
                mary.getId(), org.getId(), AccessRequestStatus.PENDING);

        assertFalse(exists);
    }

    @Test
    void whenRequestIsNoLongerPending_thenExistsPendingShouldReturnFalse() {
        User user = userRepository.save(new User("john@email.com", "password"));
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        AccessRequest request = new AccessRequest(user, org);
        request.setStatus(AccessRequestStatus.REJECTED);
        accessRequestRepository.save(request);

        boolean exists = accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(
                user.getId(), org.getId(), AccessRequestStatus.PENDING);

        assertFalse(exists);
    }

    @Test
    void whenListingByOrganizationAndStatus_thenShouldReturnOnlyPendingRequestsFromThatOrganization() {
        User john = userRepository.save(new User("john@email.com", "password"));
        User mary = userRepository.save(new User("mary@email.com", "password"));
        User peter = userRepository.save(new User("peter@email.com", "password"));
        Organization orgA = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        Organization orgB = organizationRepository.save(new Organization("org-b", "Organization B", "church", "Address B"));

        AccessRequest pendingInOrgA = accessRequestRepository.save(new AccessRequest(john, orgA));
        AccessRequest approvedInOrgA = new AccessRequest(mary, orgA);
        approvedInOrgA.setStatus(AccessRequestStatus.APPROVED);
        accessRequestRepository.save(approvedInOrgA);
        accessRequestRepository.save(new AccessRequest(peter, orgB));

        List<AccessRequest> result = accessRequestRepository.findByOrganizationIdAndStatus(
                orgA.getId(), AccessRequestStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(pendingInOrgA.getId(), result.get(0).getId());
    }

    @Test
    void whenOrganizationHasNoPendingRequests_thenListingShouldReturnEmptyList() {
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));

        List<AccessRequest> result = accessRequestRepository.findByOrganizationIdAndStatus(
                org.getId(), AccessRequestStatus.PENDING);

        assertTrue(result.isEmpty());
    }

}
