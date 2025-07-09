package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalcControllerTest {
    @Test
    void testCalc() {
        CalcController controller = new CalcController();
        CalcController.CalcRequest req = new CalcController.CalcRequest();
        req.setNumber(5);
        assertEquals(10, controller.calc(req));
    }
}