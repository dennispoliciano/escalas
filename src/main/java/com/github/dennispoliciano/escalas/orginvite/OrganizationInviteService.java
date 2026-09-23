package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipService;
import com.github.dennispoliciano.escalas.orgmembership.Role;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class OrganizationInviteService {

    private static final int EXPIRATION_DAYS = 7;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationInviteRepository organizationInviteRepository;

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private OrgMembershipService orgMembershipService;

    public OrganizationInvite create(Long organizationId, User actingUser) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada"));

        requireOrgAdmin(actingUser, org);

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS);

        return organizationInviteRepository.save(new OrganizationInvite(token, org, expiresAt));
    }


    @Transactional
    public OrganizationInvite accept(String token, User actingUser) {
        OrganizationInvite invite = organizationInviteRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite não encontrado"));

        if (Boolean.TRUE.equals(invite.getUsed())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Convite já foi utilizado");
        }

        if (invite.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Convite expirado");
        }

        orgMembershipService.createMember(actingUser, invite.getOrganization());

        invite.setUsed(true);
        return organizationInviteRepository.save(invite);
    }

    private void requireOrgAdmin(User user, Organization organization) {
        boolean isOrgAdminOfThisOrganization = orgMembershipRepository.findByUserAndOrganization(user, organization)
                .map(membership -> membership.getRole() == Role.ORG_ADMIN)
                .orElse(false);

        if (!isOrgAdminOfThisOrganization) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não é administrador desta organização");
        }
    }
}
