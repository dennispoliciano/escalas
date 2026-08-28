package com.github.dennispoliciano.escalas.eventfunctionrequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("event-function-requirements")
public class EventFunctionRequirementController {

    @Autowired
    private EventFunctionRequirementRepository eventFunctionRequirementRepository;

    @GetMapping
    public List<EventFunctionRequirement> findAll() {
        return eventFunctionRequirementRepository.findAll();
    }

    @PostMapping
    public EventFunctionRequirement save(@RequestBody EventFunctionRequirement eventFunctionRequirement) {
        return eventFunctionRequirementRepository.save(eventFunctionRequirement);
    }
}
