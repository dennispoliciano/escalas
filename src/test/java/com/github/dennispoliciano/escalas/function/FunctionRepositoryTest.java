package com.github.dennispoliciano.escalas.function;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
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

    @Test
    void mustSaveAndReturnAFunction() {
        Function function = new Function("violão","🎸");
        Function persistedFunction = functionRepository.save(function);
        assertNotNull(persistedFunction.getId());
    }

}