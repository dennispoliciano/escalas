package com.github.dennispoliciano.escalas.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProtectedController {

    @GetMapping("/test/protected")
    public String protectedEndpoint() {
        return "acesso autorizado";
    }
}