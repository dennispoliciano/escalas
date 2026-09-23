package com.github.dennispoliciano.escalas.accessrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    boolean existsByUserIdAndOrganizationIdAndStatus(Long userId, Long organizationId, AccessRequestStatus status);

    List<AccessRequest> findByOrganizationIdAndStatus(Long organizationId, AccessRequestStatus status);

}
