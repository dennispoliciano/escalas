package com.github.dennispoliciano.escalas.orginvite;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {

    Optional<OrganizationInvite> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrganizationInvite> findWithLockByToken(String token);
}
