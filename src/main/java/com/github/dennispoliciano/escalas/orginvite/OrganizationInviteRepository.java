package com.github.dennispoliciano.escalas.orginvite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {

    Optional<OrganizationInvite> findByToken(String token);
}
