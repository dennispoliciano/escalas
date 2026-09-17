package com.github.dennispoliciano.escalas.onboarding;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembership;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.orgmembership.Role;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {

    private final OrganizationRepository organizationRepository;
    private final OrgMembershipRepository orgMembershipRepository;

    public OnboardingService(OrganizationRepository organizationRepository,
                             OrgMembershipRepository orgMembershipRepository) {
        this.organizationRepository = organizationRepository;
        this.orgMembershipRepository = orgMembershipRepository;
    }

    @Transactional
    public Organization createOrganization(Organization organization, User user) {
        organizationRepository.save(organization);
        orgMembershipRepository.save(new OrgMembership(user, organization, Role.ORG_ADMIN));
        return organization;
    }
}
