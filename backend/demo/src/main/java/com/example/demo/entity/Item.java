package com.example.demo.entity;

import java.time.LocalDateTime;

public class Item {
    private Long id;
    private String itemNumber;     // 備品番号
    private String itemName;       // 備品名称
    private String modelNumber;    // 型番
    private boolean inStock;       // 在庫有無
    private String remarks;        // 備考
    private LocalDateTime registeredAt; // 登録時間

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
}