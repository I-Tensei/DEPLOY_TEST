package com.example.demo.repository;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // 備品番号で検索
    Optional<Item> findByItemNumber(String itemNumber);
    
    // 在庫状況で検索（Integer型）
    List<Item> findByInStock(Integer inStock);
    
    // 在庫有りのアイテムのみ取得
    @Query("SELECT i FROM Item i WHERE i.inStock = 1")
    List<Item> findInStockItems();
    
    // 在庫切れのアイテムのみ取得
    @Query("SELECT i FROM Item i WHERE i.inStock = 0")
    List<Item> findOutOfStockItems();
    
    // 備品名で部分一致検索
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    
    // 型番で部分一致検索
    List<Item> findByModelNumberContainingIgnoreCase(String modelNumber);
    
    // カテゴリIDで検索
    List<Item> findByCategoryId(Integer categoryId);
    
    // 価格範囲で検索
    @Query("SELECT i FROM Item i WHERE i.price BETWEEN :minPrice AND :maxPrice")
    List<Item> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);
    
    // 数量が指定値以下のアイテム検索（在庫僅少チェック）
    @Query("SELECT i FROM Item i WHERE i.quantity <= :threshold AND i.inStock = 1")
    List<Item> findLowStockItems(@Param("threshold") Integer threshold);
    
    // カスタムクエリ：キーワード検索
    @Query("SELECT i FROM Item i WHERE " +
           "LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.itemNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.modelNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> findByKeyword(@Param("keyword") String keyword);
    
    // 在庫状況別のカウント
    long countByInStock(Integer inStock);
    
    // 在庫有りアイテムの総数量
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Item i WHERE i.inStock = 1")
    Long getTotalInStockQuantity();
    
    // 総資産額（在庫有りアイテムの価格×数量の合計）
    @Query("SELECT COALESCE(SUM(i.price * i.quantity), 0) FROM Item i WHERE i.inStock = 1 AND i.price IS NOT NULL")
    Long getTotalAssetValue();
    
    // 登録日時順で取得
    List<Item> findAllByOrderByRegisteredAtDesc();
    
    // 備品番号順で取得
    List<Item> findAllByOrderByItemNumberAsc();
    
    // 在庫状況と数量で並び替え（在庫切れ→在庫僅少→在庫十分の順）
    @Query("SELECT i FROM Item i ORDER BY i.inStock ASC, i.quantity ASC, i.itemNumber ASC")
    List<Item> findAllOrderByStockStatus();
}