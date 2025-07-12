package com.example.demo.controller;

import com.example.demo.entity.Item;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ItemController {

    // サンプルデータ（実際はDBから取得）
    private List<Item> items = new ArrayList<>();

    public ItemController() {
        // 初期データ
        items.add(new Item(1L, "PC001", "ノートパソコン", "ThinkPad X1", true, "正常", LocalDateTime.now()));
        items.add(new Item(2L, "PR001", "プリンター", "Canon LBP6030", false, "トナー切れ", LocalDateTime.now()));
    }

    @GetMapping("/items")
    public List<Item> getAllItems() {
        return items;
    }

    @PostMapping("/items")
    public Item addItem(@RequestBody Item item) {
        item.setId((long) (items.size() + 1));
        item.setRegisteredAt(LocalDateTime.now());
        items.add(item);
        return item;
    }
}