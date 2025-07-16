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
        
        // 在庫フラグの正規化（null チェック）
        if (item.getInStock() == null) {
            item.setInStock(1);  // デフォルトは在庫有り
        }
        
        // 数量のデフォルト設定
        if (item.getQuantity() == null) {
            item.setQuantity(1);
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
            item.setInStock(updatedItem.getInStock());
            item.setRemarks(updatedItem.getRemarks());
            item.setQuantity(updatedItem.getQuantity());
            item.setPrice(updatedItem.getPrice());
            item.setCategoryId(updatedItem.getCategoryId());
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
    
    // 在庫状況でフィルタ（Integer型）
    public List<Item> getItemsByStockStatus(Integer inStock) {
        return itemRepository.findByInStock(inStock);
    }
    
    // 在庫状況でフィルタ（boolean型 - 互換性維持）
    public List<Item> getItemsByStockStatus(boolean inStock) {
        return itemRepository.findByInStock(inStock ? 1 : 0);
    }
    
    // 在庫有りアイテム取得
    public List<Item> getInStockItems() {
        return itemRepository.findInStockItems();
    }
    
    // 在庫切れアイテム取得
    public List<Item> getOutOfStockItems() {
        return itemRepository.findOutOfStockItems();
    }
    
    // 在庫僅少アイテム取得（数量が指定値以下）
    public List<Item> getLowStockItems(Integer threshold) {
        return itemRepository.findLowStockItems(threshold != null ? threshold : 5);
    }
    
    // 価格範囲で検索
    public List<Item> getItemsByPriceRange(Integer minPrice, Integer maxPrice) {
        return itemRepository.findByPriceRange(
            minPrice != null ? minPrice : 0,
            maxPrice != null ? maxPrice : Integer.MAX_VALUE
        );
    }
    
    // 統計情報取得
    public ItemStats getItemStats() {
        long totalItems = itemRepository.count();
        long inStockItems = itemRepository.countByInStock(1);
        long outOfStockItems = itemRepository.countByInStock(0);
        Long totalQuantity = itemRepository.getTotalInStockQuantity();
        Long totalAssetValue = itemRepository.getTotalAssetValue();
        
        return new ItemStats(totalItems, inStockItems, outOfStockItems, 
                           totalQuantity != null ? totalQuantity : 0L,
                           totalAssetValue != null ? totalAssetValue : 0L);
    }
    
    // 統計情報クラス
    public static class ItemStats {
        private final long totalItems;
        private final long inStockItems;
        private final long outOfStockItems;
        private final long totalQuantity;
        private final long totalAssetValue;
        
        public ItemStats(long totalItems, long inStockItems, long outOfStockItems, 
                        long totalQuantity, long totalAssetValue) {
            this.totalItems = totalItems;
            this.inStockItems = inStockItems;
            this.outOfStockItems = outOfStockItems;
            this.totalQuantity = totalQuantity;
            this.totalAssetValue = totalAssetValue;
        }
        
        public long getTotalItems() { return totalItems; }
        public long getInStockItems() { return inStockItems; }
        public long getOutOfStockItems() { return outOfStockItems; }
        public long getTotalQuantity() { return totalQuantity; }
        public long getTotalAssetValue() { return totalAssetValue; }
        public double getStockRatio() { 
            return totalItems > 0 ? (double) inStockItems / totalItems * 100 : 0; 
        }
    }
}