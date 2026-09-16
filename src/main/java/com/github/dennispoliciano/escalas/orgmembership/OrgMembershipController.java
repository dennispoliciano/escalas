package com.github.dennispoliciano.escalas.orgmembership;

import com.github.dennispoliciano.escalas.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("org-membership")
public class OrgMembershipController {
    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @PostMapping
    public OrgMembership save(@RequestBody OrgMembership orgMembership) {
        return orgMembershipRepository.save(orgMembership);
    }

    @GetMapping
    public List<OrgMembership> findAll() {
        return orgMembershipRepository.findAll();
    }

    @GetMapping
    public List<OrgMembership> findByUserId(@RequestParam User user) {
        return orgMembershipRepository.findByUser(user);
    }

}
