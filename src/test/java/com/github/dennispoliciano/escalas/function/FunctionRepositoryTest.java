package com.github.dennispoliciano.escalas.function;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.group.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FunctionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void mustSaveAndReturnAFunction() {
        Group group = new Group("Louvor","pln-lv-01", "Louvor");
        Group persistedGroup = groupRepository.save(group);

        Function function = new Function("violão","🎸", persistedGroup);
        Function persistedFunction = functionRepository.save(function);
        assertNotNull(persistedFunction.getId());
        assertNotNull(persistedFunction.getGroup());
    }

}