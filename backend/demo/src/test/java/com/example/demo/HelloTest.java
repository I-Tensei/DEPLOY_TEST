package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {

    @Test
    void testHelloMessage() {
        String actual = "Hello from Spring Boot!";
        String expected = "Hello from Spring Boot!";
        assertEquals(expected, actual);
    }
}
