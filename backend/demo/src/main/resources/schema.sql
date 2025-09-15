-- MySQL / H2 両対応を意図していたが、本番 MySQL では Hibernate 管理。必要な場合のみ手動実行。
-- 注意: MySQL 8 では CREATE INDEX IF NOT EXISTS が未サポートのため使用しない。
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

-- 既に存在する可能性があるため、手動適用時はエラーになったら無視するか SHOW INDEX で確認
CREATE INDEX idx_item_number ON items (item_number);
CREATE INDEX idx_in_stock ON items (in_stock);
CREATE INDEX idx_registered_at ON items (registered_at);


CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(20) NOT NULL UNIQUE,      -- Unique user ID (login ID)
    username VARCHAR(50) NOT NULL,             -- Display name or real name of the user
    password_hash VARCHAR(255) NOT NULL,       -- Hashed password (e.g., bcrypt)
    role_level INT NOT NULL DEFAULT 0,         -- Role level (0 = normal user, 1 = admin, etc.)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Creation time
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Last update time
    PRIMARY KEY (user_id)
);
