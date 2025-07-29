package com.example.demo.controller;

import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    // 全件取得 (API版) 
    @GetMapping("/api/items")
    public ResponseEntity<List<Item>> getAllItemsApi() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    // 全件取得 (従来版)
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    // ID指定取得 (API版)
    @GetMapping("/api/items/{id}")
    public ResponseEntity<Item> getItemByIdApi(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // 新規作成 (API版)
    @PostMapping("/api/items")
    public ResponseEntity<Item> createItemApi(@RequestBody Item item) {
        try {
            Item createdItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 更新
    @PutMapping("/api/items/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        try {
            Item updatedItem = itemService.updateItem(id, item);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 削除
    @DeleteMapping("/api/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // キーワード検索
    @GetMapping("/api/items/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String keyword) {
        List<Item> items = itemService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }

    // 在庫状況でフィルタ（修正版）
    @GetMapping("/api/items/filter")
    public ResponseEntity<List<Item>> filterByStock(@RequestParam boolean inStock) {
        List<Item> items = inStock ? itemService.getInStockItems() : itemService.getOutOfStockItems();
        return ResponseEntity.ok(items);
    }
    
    // 統計情報取得 (API版)
    @GetMapping("/api/items/stats")
    public ResponseEntity<ItemService.StatsInfo> getItemStats() {
        ItemService.StatsInfo stats = itemService.getStats();
        return ResponseEntity.ok(stats);
    }
}