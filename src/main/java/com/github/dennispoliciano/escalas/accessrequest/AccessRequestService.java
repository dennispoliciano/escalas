package com.github.dennispoliciano.escalas.accessrequest;

import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipService;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccessRequestService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private OrgMembershipService orgMembershipService;

    public AccessRequest create(User user, String organizationCode) {
        Organization org = organizationRepository.findByCode(organizationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada"));

        if (accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(user.getId(), org.getId(), AccessRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe solicitação pendente");
        }

        return accessRequestRepository.save(new AccessRequest(user, org));
    }

    public List<AccessRequest> list(Long organizationId, AccessRequestStatus status) {
        return accessRequestRepository.findByOrganizationIdAndStatus(organizationId, status);
    }

    @Transactional
    public AccessRequest approve(Long orgId, Long requestId, User actingUser) {
        AccessRequest accessRequest = findAuthorizedPendingRequest(orgId, requestId, actingUser);

        accessRequest.setStatus(AccessRequestStatus.APPROVED);
        accessRequestRepository.save(accessRequest);

        orgMembershipService.createMember(accessRequest.getUser(), accessRequest.getOrganization());

        return accessRequest;
    }

    @Transactional
    public AccessRequest reject(Long orgId, Long requestId, User actingUser) {
        AccessRequest accessRequest = findAuthorizedPendingRequest(orgId, requestId, actingUser);

        accessRequest.setStatus(AccessRequestStatus.REJECTED);
        accessRequestRepository.save(accessRequest);

        return accessRequest;
    }

    private AccessRequest findAuthorizedPendingRequest(Long orgId, Long requestId, User actingUser) {
        AccessRequest accessRequest = accessRequestRepository.findByIdAndOrganizationId(requestId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação de acesso não encontrada"));

        orgMembershipService.requireOrgAdmin(actingUser, accessRequest.getOrganization());

        if (accessRequest.getStatus() != AccessRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solicitação de acesso já foi processada");
        }

        return accessRequest;
    }
}
