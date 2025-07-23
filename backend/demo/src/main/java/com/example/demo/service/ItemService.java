package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    // 全件取得
    public List<Item> getAllItems() {
        return itemRepository.findAllByOrderByRegisteredAtDesc();
    }
    
    // ID で取得
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
    
    // 備品番号で取得
    public Optional<Item> getItemByItemNumber(String itemNumber) {
        return itemRepository.findByItemNumber(itemNumber);
    }
    
    // 新規作成
    public Item createItem(Item item) {
        // 備品番号の重複チェック
        if (itemRepository.findByItemNumber(item.getItemNumber()).isPresent()) {
            throw new RuntimeException("備品番号が既に存在します: " + item.getItemNumber());
        }
        
        // 数量のデフォルト設定
        if (item.getQuantity() == null) {
            item.setQuantity(1);
        }
        
        // 価格のデフォルト設定
        if (item.getPrice() == null) {
            item.setPrice(0.0);
        }
        
        // カテゴリIDのデフォルト設定
        if (item.getCategoryId() == null) {
            item.setCategoryId(1);
        }
        
        item.setRegisteredAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return itemRepository.save(item);
    }
    
    // 更新
    public Item updateItem(Long id, Item updatedItem) {
        Optional<Item> existingItem = itemRepository.findById(id);
        if (existingItem.isPresent()) {
            Item item = existingItem.get();
            
            // 備品番号の重複チェック（自分以外）
            Optional<Item> duplicateItem = itemRepository.findByItemNumber(updatedItem.getItemNumber());
            if (duplicateItem.isPresent() && !duplicateItem.get().getId().equals(id)) {
                throw new RuntimeException("備品番号が既に存在します: " + updatedItem.getItemNumber());
            }
            
            item.setItemNumber(updatedItem.getItemNumber());
            item.setItemName(updatedItem.getItemName());
            item.setModelNumber(updatedItem.getModelNumber());
            item.setInStock(updatedItem.isInStock());  // boolean値で設定
            item.setRemarks(updatedItem.getRemarks());
            
            // 既存の値を保持する項目
            if (updatedItem.getQuantity() != null) {
                item.setQuantity(updatedItem.getQuantity());
            }
            if (updatedItem.getPrice() != null) {
                item.setPrice(updatedItem.getPrice());
            }
            if (updatedItem.getCategoryId() != null) {
                item.setCategoryId(updatedItem.getCategoryId());
            }
            
            item.setUpdatedAt(LocalDateTime.now());
            
            return itemRepository.save(item);
        } else {
            throw new RuntimeException("アイテムが見つかりません: " + id);
        }
    }
    
    // 削除
    public void deleteItem(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            throw new RuntimeException("アイテムが見つかりません: " + id);
        }
    }
    
    // キーワード検索
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItems();
        }
        return itemRepository.findByKeyword(keyword.trim());
    }
    
    // 在庫有りアイテム取得
    public List<Item> getInStockItems() {
        return itemRepository.findByInStock(1);  // 1 = 在庫有り
    }
    
    // 在庫切れアイテム取得
    public List<Item> getOutOfStockItems() {
        return itemRepository.findByInStock(0);  // 0 = 在庫切れ
    }
    
    // カテゴリ別アイテム取得
    public List<Item> getItemsByCategory(Integer categoryId) {
        if (categoryId == null) {
            return getAllItems();
        }
        return itemRepository.findByCategoryId(categoryId);
    }
    
    // 統計情報クラス
    public static class StatsInfo {
        private long totalItems;
        private long inStockItems;
        private long outOfStockItems;
        private double stockRatio;
        
        public StatsInfo(long totalItems, long inStockItems, long outOfStockItems, double stockRatio) {
            this.totalItems = totalItems;
            this.inStockItems = inStockItems;
            this.outOfStockItems = outOfStockItems;
            this.stockRatio = stockRatio;
        }

        // Getters
        public long getTotalItems() { return totalItems; }
        public long getInStockItems() { return inStockItems; }
        public long getOutOfStockItems() { return outOfStockItems; }
        public double getStockRatio() { return stockRatio; }
    }
    
    // 統計情報取得
    public StatsInfo getStats() {
        long totalItems = itemRepository.count();
        long inStockItems = itemRepository.countByInStock(1);
        long outOfStockItems = totalItems - inStockItems;
        double stockRatio = totalItems > 0 ? (double) inStockItems / totalItems * 100.0 : 0.0;
        
        return new StatsInfo(totalItems, inStockItems, outOfStockItems, stockRatio);
    }
}