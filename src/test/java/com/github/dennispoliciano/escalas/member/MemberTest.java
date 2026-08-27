package com.github.dennispoliciano.escalas.member;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberTest {

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
    void whenEmailAndPhoneAreValidThenHasNoViolations() {
        Member member = new Member("João Silva", "joao@email.com", "+5511999999999", LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"joao", "joao@", "@email.com", "joao email.com", "joao@@email.com"})
    void whenEmailIsInvalidThenHasViolation(String invalidEmail) {
        Member member = new Member("João Silva", invalidEmail, "+5511999999999", LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void whenEmailIsBlankThenHasViolation() {
        Member member = new Member("João Silva", "", "+5511999999999", LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "123", "11999999999999999", "+55 11 99999-9999", "telefone"})
    void whenPhoneIsInvalidThenHasViolation(String invalidPhone) {
        Member member = new Member("João Silva", "joao@email.com", invalidPhone, LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @Test
    void whenPhoneIsBlankThenHasViolation() {
        Member member = new Member("João Silva", "joao@email.com", "", LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1199999999", "+5511999999999", "551199999999999", "999999999999999"})
    void whenPhoneIsValidThenHasNoPhoneViolation(String validPhone) {
        Member member = new Member("João Silva", "joao@email.com", validPhone, LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }
}
