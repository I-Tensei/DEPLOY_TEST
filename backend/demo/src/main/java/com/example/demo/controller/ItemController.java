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

    // 既存のフロントエンドとの互換性維持
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping("/items")
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    // RESTful API用エンドポイント
    @GetMapping("/api/items")
    public ResponseEntity<List<Item>> getAllItemsApi() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    // ID で取得
    @GetMapping("/api/items/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
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

    // 検索
    @GetMapping("/api/items/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String keyword) {
        List<Item> items = itemService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }

    // 在庫状況でフィルタ
    @GetMapping("/api/items/filter")
    public ResponseEntity<List<Item>> filterByStock(@RequestParam boolean inStock) {
        List<Item> items = itemService.getItemsByStockStatus(inStock);
        return ResponseEntity.ok(items);
    }

    // 統計情報
    @GetMapping("/api/items/stats")
    public ResponseEntity<ItemService.ItemStats> getItemStats() {
        ItemService.ItemStats stats = itemService.getItemStats();
        return ResponseEntity.ok(stats);
    }
}