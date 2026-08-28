package com.github.dennispoliciano.escalas.organization;

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

public class OrganizationTest {

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
        Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", "Av. Paulista, 999");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenCodeIsNullOrBlankThenHasViolation(String invalidCode) {
        Organization organization = new Organization(invalidCode, "Igreja Batista Central", "Igreja", "Av. Paulista, 999");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenNameIsNullOrBlankThenHasViolation(String invalidName) {
        Organization organization = new Organization("001", invalidName, "Igreja", "Av. Paulista, 999");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenTypeIsNullOrBlankThenHasViolation(String invalidType) {
        Organization organization = new Organization("001", "Igreja Batista Central", invalidType, "Av. Paulista, 999");

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void whenAddressIsNullOrBlankThenHasViolation(String invalidAddress) {
        Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", invalidAddress);

        Set<ConstraintViolation<Organization>> violations = validator.validate(organization);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("address")));
    }
}
