package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganizationInviteRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OrganizationInviteRepository organizationInviteRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void whenInviteIsCreated_thenShouldBePersistedAsUnusedWithExpiration() {
        Organization org = organizationRepository.save(new Organization("org-a", "Organization A", "church", "Address A"));
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

        OrganizationInvite saved = organizationInviteRepository.saveAndFlush(
                new OrganizationInvite("token-abc", org, expiresAt));

        OrganizationInvite found = organizationInviteRepository.findById(saved.getId()).orElseThrow();
        assertEquals("token-abc", found.getToken());
        assertEquals(org.getId(), found.getOrganization().getId());
        assertFalse(found.getUsed());
        assertEquals(expiresAt, found.getExpiresAt());
    }

    @Test
    void whenTokenExists_thenFindByTokenShouldReturnInvite() {
        Organization org = organizationRepository.save(new Organization("org-b", "Organization B", "church", "Address B"));
        organizationInviteRepository.save(new OrganizationInvite("token-xyz", org, Instant.now().plus(7, ChronoUnit.DAYS)));

        Optional<OrganizationInvite> found = organizationInviteRepository.findByToken("token-xyz");

        assertTrue(found.isPresent());
        assertEquals(org.getId(), found.get().getOrganization().getId());
    }

    @Test
    void whenTokenDoesNotExist_thenFindByTokenShouldReturnEmpty() {
        Optional<OrganizationInvite> found = organizationInviteRepository.findByToken("token-inexistente");

        assertTrue(found.isEmpty());
    }

}
