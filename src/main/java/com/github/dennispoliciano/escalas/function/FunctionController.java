package com.github.dennispoliciano.escalas.function;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("functions")
public class FunctionController {

    @Autowired
    private FunctionRepository functionRepository;

    @PostMapping
    public Function save(@Valid @RequestBody Function function) {
        return functionRepository.save(function);
    }

    @GetMapping
    public List<Function> findAll() {
        return functionRepository.findAll();
    }
}
