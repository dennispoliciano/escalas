package com.github.dennispoliciano.escalas.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public List<Group> findAll() {
       return groupRepository.findAll();
    }

    @PostMapping
    public Group save(@RequestBody Group group) {
        return groupRepository.save(group);
    }
}
