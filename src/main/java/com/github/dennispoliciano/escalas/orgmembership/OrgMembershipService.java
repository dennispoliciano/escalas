package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgMembershipService {

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    public OrgMembership createMember(User user, Organization organization) {
        return orgMembershipRepository.findByUserAndOrganization(user, organization)
                .orElseGet(() -> orgMembershipRepository.save(new OrgMembership(user, organization, Role.MEMBER)));
    }
}
