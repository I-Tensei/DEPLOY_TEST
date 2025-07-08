package com.example.demo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    void testGetGreeting() {
        String result = helloService.getGreeting();
        assertEquals("Hello, World!", result);
    }
}
