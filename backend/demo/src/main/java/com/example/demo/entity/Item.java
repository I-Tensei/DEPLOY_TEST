package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_number", unique = true, nullable = false, length = 50)
    private String itemNumber;     // 備品番号
    
    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;       // 備品名称
    
    @Column(name = "model_number", length = 255)
    private String modelNumber;    // 型番
    
    @Column(name = "in_stock", nullable = false)
    private boolean inStock;       // 在庫有無
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;        // 備考
    
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt; // 登録時間
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新時間

    // デフォルトコンストラクタ
    public Item() {}
    
    // 全引数コンストラクタ
    public Item(Long id, String itemNumber, String itemName, String modelNumber, 
                boolean inStock, String remarks, LocalDateTime registeredAt) {
        this.id = id;
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.modelNumber = modelNumber;
        this.inStock = inStock;
        this.remarks = remarks;
        this.registeredAt = registeredAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    // JPA用ライフサイクルメソッド
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getItemNumber() { return itemNumber; }
    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }
    
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}