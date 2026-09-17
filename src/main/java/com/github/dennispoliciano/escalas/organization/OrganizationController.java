package com.github.dennispoliciano.escalas.organization;

import com.github.dennispoliciano.escalas.auth.UserPrincipal;
import com.github.dennispoliciano.escalas.onboarding.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("organizations")
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping
    public Organization createOrganization(@Valid @RequestBody Organization organization) {
        organizationRepository.save(organization);
        return organization;
    }

    @GetMapping
    public List<Organization> getAllOrganizations(){
        return organizationRepository.findAll();
    }

    @PostMapping("onboarding")
    public Organization createOrganizationForCurrentUser(
            @Valid @RequestBody Organization organization,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return onboardingService.createOrganization(organization, userPrincipal.getUser());
    }
}
