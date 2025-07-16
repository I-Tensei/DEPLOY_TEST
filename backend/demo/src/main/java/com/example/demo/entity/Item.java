package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_item_number", columnList = "item_number"),
    @Index(name = "idx_in_stock", columnList = "in_stock"),
    @Index(name = "idx_registered_at", columnList = "registered_at")
})
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    // 備品番号: 必須、一意、最大20文字
    @Column(name = "item_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "備品番号は必須です")
    @Size(max = 20, message = "備品番号は20文字以内で入力してください")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "備品番号は英数字、ハイフン、アンダースコアのみ使用可能です")
    private String itemNumber;
    
    // 備品名称: 必須、最大100文字
    @Column(name = "item_name", nullable = false, length = 100)
    @NotBlank(message = "備品名称は必須です")
    @Size(max = 100, message = "備品名称は100文字以内で入力してください")
    private String itemName;
    
    // 型番: 任意、最大50文字
    @Column(name = "model_number", length = 50)
    @Size(max = 50, message = "型番は50文字以内で入力してください")
    private String modelNumber;
    
    // 在庫有無: データベースはINTEGER、JSONではboolean (H2データベース対応)
    @Column(name = "in_stock", nullable = false)
    @NotNull(message = "在庫状況は必須です")
    private Integer inStockInt = 1;  // データベース用: 0=在庫切れ, 1=在庫有り
    
    // 備考: 任意、最大500文字
    @Column(name = "remarks", length = 500)
    @Size(max = 500, message = "備考は500文字以内で入力してください")
    private String remarks;
    
    // 登録時間: 必須、自動設定
    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;
    
    // 更新時間: 任意、自動更新
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // カテゴリID（将来の拡張用）
    @Column(name = "category_id")
    private Integer categoryId;

    // 数量（将来の拡張用）
    @Column(name = "quantity")
    private Integer quantity;

    // 価格（将来の拡張用）
    @Column(name = "price")
    private Integer price;

    // デフォルトコンストラクタ
    public Item() {}

    // フルコンストラクタ
    public Item(String itemNumber, String itemName, String modelNumber, boolean inStock, String remarks) {
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.modelNumber = modelNumber;
        this.inStockInt = inStock ? 1 : 0;
        this.remarks = remarks;
    }

    // ゲッター・セッターメソッド
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    // フロントエンド用のboolean型getter/setter（JSON用）
    @JsonProperty("inStock")
    public boolean isInStock() {
        return inStockInt != null && inStockInt == 1;
    }

    @JsonProperty("inStock")
    public void setInStock(boolean inStock) {
        this.inStockInt = inStock ? 1 : 0;
    }

    // データベース用のInteger型getter/setter
    public Integer getInStockInt() {
        return inStockInt;
    }

    public void setInStockInt(Integer inStockInt) {
        this.inStockInt = inStockInt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    // JPA ライフサイクルメソッド
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // toString メソッド
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemNumber='" + itemNumber + '\'' +
                ", itemName='" + itemName + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", inStock=" + isInStock() +
                ", remarks='" + remarks + '\'' +
                ", categoryId=" + categoryId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", registeredAt=" + registeredAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}