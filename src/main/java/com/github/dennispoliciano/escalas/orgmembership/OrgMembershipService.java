package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrgMembershipService {

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    public OrgMembership createMember(User user, Organization organization) {
        return orgMembershipRepository.findByUserAndOrganization(user, organization)
                .orElseGet(() -> orgMembershipRepository.save(new OrgMembership(user, organization, Role.MEMBER)));
    }

    public void requireOrgAdmin(User user, Organization organization) {
        boolean isOrgAdminOfThisOrganization = orgMembershipRepository.findByUserAndOrganization(user, organization)
                .map(membership -> membership.getRole() == Role.ORG_ADMIN)
                .orElse(false);

        if (!isOrgAdminOfThisOrganization) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não é administrador desta organização");
        }
    }
}
