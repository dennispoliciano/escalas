package com.github.dennispoliciano.escalas.accessrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    boolean existsByUserIdAndOrganizationIdAndStatus(Long userId, Long organizationId, AccessRequestStatus status);

    List<AccessRequest> findByOrganizationIdAndStatus(Long organizationId, AccessRequestStatus status);

    Optional<AccessRequest> findByIdAndOrganizationId(Long id, Long organizationId);

}
