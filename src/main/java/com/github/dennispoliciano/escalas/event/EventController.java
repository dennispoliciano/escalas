package com.github.dennispoliciano.escalas.event;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("events")
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @GetMapping
    public List<Event> findAllByActiveTrue(){
        return eventRepository.findAll();
    }

    @PostMapping
    public Event save(@Valid @RequestBody Event event){
        return eventRepository.save(event);
    }
}
