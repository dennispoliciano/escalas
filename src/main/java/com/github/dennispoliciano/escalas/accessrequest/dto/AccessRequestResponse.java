package com.github.dennispoliciano.escalas.accessrequest.dto;

import com.github.dennispoliciano.escalas.accessrequest.AccessRequest;
import com.github.dennispoliciano.escalas.accessrequest.AccessRequestStatus;

import java.time.Instant;

public record AccessRequestResponse(
        Long id,
        Long userId,
        String userEmail,
        Long organizationId,
        AccessRequestStatus status,
        Instant requestedAt) {

    public static AccessRequestResponse from(AccessRequest accessRequest) {
        return new AccessRequestResponse(
                accessRequest.getId(),
                accessRequest.getUser().getId(),
                accessRequest.getUser().getEmail(),
                accessRequest.getOrganization().getId(),
                accessRequest.getStatus(),
                accessRequest.getRequestedAt());
    }
}
