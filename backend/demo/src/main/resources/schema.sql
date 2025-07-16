-- H2データベース用テーブル作成
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_number VARCHAR(20) NOT NULL UNIQUE,
    item_name VARCHAR(100) NOT NULL,
    model_number VARCHAR(50),
    in_stock INTEGER NOT NULL DEFAULT 1,
    remarks VARCHAR(500),
    category_id INTEGER,
    quantity INTEGER,
    price INTEGER,
    registered_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- インデックス作成
CREATE INDEX IF NOT EXISTS idx_item_number ON items (item_number);
CREATE INDEX IF NOT EXISTS idx_in_stock ON items (in_stock);
CREATE INDEX IF NOT EXISTS idx_registered_at ON items (registered_at);