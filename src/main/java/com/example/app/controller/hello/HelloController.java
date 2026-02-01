package com.example.app.controller.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HelloController {
    static final String API_PATH = "/hello";

    @GetMapping(API_PATH)
    public HelloResponse hello() {
        String message = "Hello, World!";
        String timestamp = Instant.now().toString();
        return new HelloResponse(message, timestamp);
    }
}
