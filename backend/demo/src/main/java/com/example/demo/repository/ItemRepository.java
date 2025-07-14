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
    
    // 在庫状況で検索
    List<Item> findByInStock(boolean inStock);
    
    // 備品名で部分一致検索
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    
    // 型番で部分一致検索
    List<Item> findByModelNumberContainingIgnoreCase(String modelNumber);
    
    // カスタムクエリ：キーワード検索
    @Query("SELECT i FROM Item i WHERE " +
           "LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.itemNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.modelNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Item> findByKeyword(@Param("keyword") String keyword);
    
    // 在庫切れアイテム数をカウント
    long countByInStock(boolean inStock);
    
    // 登録日時順で取得
    List<Item> findAllByOrderByRegisteredAtDesc();
}