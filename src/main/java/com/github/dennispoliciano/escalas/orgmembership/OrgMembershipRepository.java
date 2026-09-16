package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgMembershipRepository extends JpaRepository<OrgMembership, Long> {
    List<OrgMembership> findByUserId(Long userId);

    List<OrgMembership> findByOrganization(Organization organization);

    Optional<OrgMembership> findByUserAndOrganization(User user, Organization organization);
}
