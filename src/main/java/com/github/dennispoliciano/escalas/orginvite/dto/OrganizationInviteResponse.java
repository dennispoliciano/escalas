package com.github.dennispoliciano.escalas.orginvite.dto;

import com.github.dennispoliciano.escalas.orginvite.OrganizationInvite;

import java.time.Instant;

public record OrganizationInviteResponse(
        Long id,
        Long organizationId,
        String token,
        Instant expiresAt,
        Boolean used) {

    public static OrganizationInviteResponse from(OrganizationInvite invite) {
        return new OrganizationInviteResponse(
                invite.getId(),
                invite.getOrganization().getId(),
                invite.getToken(),
                invite.getExpiresAt(),
                invite.getUsed());
    }
}
