package com.github.dennispoliciano.escalas.organization;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganizationRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    public void mustSaveAndReturnAnOrganization() {
        Organization organization = new Organization("001", "Igreja Batista Central", "IGREJA", "Rua Example, 123");
        Organization persistedOrganization = organizationRepository.save(organization);
        assertNotNull(persistedOrganization.getId());
    }
}
