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

    // コンストラクタ
    public Item() {}
    
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

    // getter/setter（省略）
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... 他のgetter/setter
}