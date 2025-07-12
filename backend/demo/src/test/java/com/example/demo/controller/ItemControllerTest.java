package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemControllerTest {
    @Test
    void testGetAllItems() {
        ItemController controller = new ItemController();
        var items = controller.getAllItems();
        assertFalse(items.isEmpty());
        assertEquals("PC001", items.get(0).getItemNumber());
    }
}