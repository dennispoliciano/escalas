package com.github.dennispoliciano.escalas.accessrequest.dto;

import jakarta.validation.constraints.NotBlank;

public record AccessRequestCreateRequest(
        @NotBlank(message = "O campo 'organizationCode' não pode ser vazio.")
        String organizationCode) {
}
