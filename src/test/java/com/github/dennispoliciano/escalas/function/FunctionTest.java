package com.github.dennispoliciano.escalas.function;

import com.github.dennispoliciano.escalas.group.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionTest {

    @Test
    public void whenGroupIsNullThenThrowsException() {
        Group group = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Function("Vocal", "🎤", group)
        );

        String exceptionMessage = "O campo 'group' não pode ser nulo.";
        assertEquals(exceptionMessage, exception.getMessage());

    }
}
