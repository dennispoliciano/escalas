package com.github.dennispoliciano.escalas.group;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void mustSaveAndReturnAGroup() {
        Group group = new Group("Louvor","pln-lv-01", "Louvor");
        Group persistedGroup = groupRepository.save(group);
        assertNotNull(persistedGroup.getId());
    }


}
