package com.github.dennispoliciano.escalas.groupmembership;

import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.group.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("group-membership")
public class GroupMembershipController {

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public List<GroupMembership> findByGroupAndActiveTrue(@RequestParam Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        return groupMembershipRepository.findByGroupAndActiveTrue(group);
    }

    @PostMapping
    public GroupMembership save(@RequestBody GroupMembership groupMembership) {
        return groupMembershipRepository.save(groupMembership);
    }

}
