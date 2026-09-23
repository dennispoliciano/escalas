package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.orginvite.dto.OrganizationInviteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrganizationInviteController {

    @Autowired
    private OrganizationInviteService organizationInviteService;

    @PostMapping("organizations/{id}/invites")
    @PreAuthorize("hasRole('ORG_ADMIN')")
    public OrganizationInviteResponse create(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return OrganizationInviteResponse.from(organizationInviteService.create(id, principal.getUser()));
    }

    @PostMapping("invites/{token}/accept")
    public OrganizationInviteResponse accept(@PathVariable String token, @AuthenticationPrincipal UserPrincipal principal) {
        return OrganizationInviteResponse.from(organizationInviteService.accept(token, principal.getUser()));
    }
}
