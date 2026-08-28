package com.github.dennispoliciano.escalas.group;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupTest {

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
    void whenAllFieldsAreValidThenHasNoViolations() {
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");

        Set<ConstraintViolation<Group>> violations = validator.validate(group);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenNameIsNullOrBlankThenHasViolation(String invalidName) {
        Group group = new Group(invalidName, "pln-lv-01", "Louvor");

        Set<ConstraintViolation<Group>> violations = validator.validate(group);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenCodeIsNullOrBlankThenHasViolation(String invalidCode) {
        Group group = new Group("Louvor", invalidCode, "Louvor");

        Set<ConstraintViolation<Group>> violations = validator.validate(group);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenTypeIsNullOrBlankThenHasViolation(String invalidType) {
        Group group = new Group("Louvor", "pln-lv-01", invalidType);

        Set<ConstraintViolation<Group>> violations = validator.validate(group);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
    }
}
