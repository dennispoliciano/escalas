package com.github.dennispoliciano.escalas.accessrequest;

import com.github.dennispoliciano.escalas.accessrequest.dto.AccessRequestCreateRequest;
import com.github.dennispoliciano.escalas.accessrequest.dto.AccessRequestResponse;
import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("organizations")
public class AccessRequestController {

    @Autowired
    private AccessRequestService accessRequestService;

    @PostMapping("access-requests")
    public AccessRequestResponse create(@Valid @RequestBody AccessRequestCreateRequest body, @AuthenticationPrincipal UserPrincipal principal) {
        return AccessRequestResponse.from(accessRequestService.create(principal.getUser(), body.organizationCode()));
    }

    @GetMapping("{id}/access-requests")
    public List<AccessRequestResponse> list(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PENDING") AccessRequestStatus status) {
        return accessRequestService.list(id, status)
                .stream()
                .map(AccessRequestResponse::from)
                .toList();
    }

    @PutMapping("{orgId}/access-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ORG_ADMIN')")
    public AccessRequestResponse approve(@PathVariable Long orgId, @PathVariable Long requestId, @AuthenticationPrincipal UserPrincipal principal) {
        return AccessRequestResponse.from(accessRequestService.approve(orgId, requestId, principal.getUser()));
    }

    @PutMapping("{orgId}/access-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ORG_ADMIN')")
    public AccessRequestResponse reject(@PathVariable Long orgId, @PathVariable Long requestId, @AuthenticationPrincipal UserPrincipal principal) {
        return AccessRequestResponse.from(accessRequestService.reject(orgId, requestId, principal.getUser()));
    }
}
