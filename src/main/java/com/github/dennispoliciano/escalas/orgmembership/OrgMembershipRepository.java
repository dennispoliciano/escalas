package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrgMembershipRepository extends JpaRepository<OrgMembership, Long> {
    List<OrgMembership> findByUser(User user);

    List<OrgMembership> findByOrganization(Organization organization);

    Optional<OrgMembership> findByUserAndOrganization(User user, Organization organization);
}
