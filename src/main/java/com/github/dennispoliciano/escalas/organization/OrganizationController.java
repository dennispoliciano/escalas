package com.github.dennispoliciano.escalas.organization;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("organization")
public class OrganizationController {

    private OrganizationRepository organizationRepository;

    @PostMapping
    public Organization createOrganization(Organization organization) {
        organizationRepository.save(organization);
        return organization;
    }

    @GetMapping
    public List<Organization> getAllOrganizations(){
        return organizationRepository.findAll();
    }
}
