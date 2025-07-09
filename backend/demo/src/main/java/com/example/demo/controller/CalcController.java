package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class CalcController {

    @PostMapping("/calc")
    public int calc(@RequestBody CalcRequest req) {
        // 例：2倍して返す
        return req.getNumber() * 2;
    }

    public static class CalcRequest {
        private int number;
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
    }
}