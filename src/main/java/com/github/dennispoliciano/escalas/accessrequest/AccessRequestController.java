package com.github.dennispoliciano.escalas.accessrequest;

import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("organizations")
public class AccessRequestController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AccessRequestRepository accessRequestRepository;


    @PostMapping("access-requests")
    public AccessRequestResponse create(@Valid @RequestBody AccessRequestCreateRequest body, @AuthenticationPrincipal UserPrincipal principal) {

        Organization org = organizationRepository.findByCode(body.organizationCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organização não encontrada"));

        User user = principal.getUser();
        if (accessRequestRepository.existsByUserIdAndOrganizationIdAndStatus(user.getId(), org.getId(), AccessRequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe solicitação pendente");
        }

        return AccessRequestResponse.from(accessRequestRepository.save(new AccessRequest(user, org)));
    }

    @GetMapping("{id}/access-requests")
    public List<AccessRequestResponse> list(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PENDING") AccessRequestStatus status) {
        return accessRequestRepository.findByOrganizationIdAndStatus(id, status)
                .stream()
                .map(AccessRequestResponse::from)
                .toList();
    }

    record AccessRequestCreateRequest(
            @NotBlank(message = "O campo 'organizationCode' não pode ser vazio.")
            String organizationCode) {
    }

    record AccessRequestResponse(
            Long id,
            Long userId,
            String userEmail,
            Long organizationId,
            AccessRequestStatus status,
            Instant requestedAt) {

        static AccessRequestResponse from(AccessRequest accessRequest) {
            return new AccessRequestResponse(
                    accessRequest.getId(),
                    accessRequest.getUser().getId(),
                    accessRequest.getUser().getEmail(),
                    accessRequest.getOrganization().getId(),
                    accessRequest.getStatus(),
                    accessRequest.getRequestedAt());
        }
    }
}
