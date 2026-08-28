package com.github.dennispoliciano.escalas.function;

import com.github.dennispoliciano.escalas.group.Group;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    public void whenGroupIsNull_thenThrowsException() {
        Group group = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Function("Vocal", "🎤", group)
        );

        String exceptionMessage = "O campo 'group' não pode ser nulo.";
        assertEquals(exceptionMessage, exception.getMessage());

    }

    @Test
    void whenAllFieldsAreValid_thenHasNoViolations() {
        Function function = new Function("Vocal", "🎤", new Group("Louvor", "pln-lv-01", "Louvor"));

        Set<ConstraintViolation<Function>> violations = validator.validate(function);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenNameIsNullOrBlank_thenHasViolation(String invalidName) {
        Function function = new Function(invalidName, "🎤", new Group("Louvor", "pln-lv-01", "Louvor"));

        Set<ConstraintViolation<Function>> violations = validator.validate(function);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenIconIsNullOrBlank_thenHasViolation(String invalidIcon) {
        Function function = new Function("Vocal", invalidIcon, new Group("Louvor", "pln-lv-01", "Louvor"));

        Set<ConstraintViolation<Function>> violations = validator.validate(function);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("icon")));
    }
}
