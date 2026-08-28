package com.github.dennispoliciano.escalas.organization;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("organizations")
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @PostMapping
    public Organization createOrganization(@Valid @RequestBody Organization organization) {
        organizationRepository.save(organization);
        return organization;
    }

    @GetMapping
    public List<Organization> getAllOrganizations(){
        return organizationRepository.findAll();
    }
}
