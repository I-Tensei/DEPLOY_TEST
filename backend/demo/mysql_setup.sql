-- MySQL 初期セットアップスクリプト
-- 想定: ローカル MySQL 8.x / 権限: root で実行

-- 1. データベースとユーザ作成
CREATE DATABASE IF NOT EXISTS demo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'demo_user'@'%' IDENTIFIED BY 'demo_Pass123!';
GRANT ALL PRIVILEGES ON demo_db.* TO 'demo_user'@'%';
FLUSH PRIVILEGES;

USE demo_db;

-- 2. テーブル (items)
DROP TABLE IF EXISTS items;
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_number VARCHAR(20) NOT NULL UNIQUE,
    item_name VARCHAR(100) NOT NULL,
    model_number VARCHAR(50),
    in_stock TINYINT NOT NULL DEFAULT 1,
    remarks VARCHAR(500),
    category_id INT,
    quantity INT,
    price INT,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_item_number (item_number),
    KEY idx_in_stock (in_stock),
    KEY idx_registered_at (registered_at)
);

-- 3. テーブル (users)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id VARCHAR(20) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_level INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

-- 4. 初期データ (items)
INSERT INTO items (item_number, item_name, model_number, in_stock, remarks, category_id, quantity, price, registered_at, updated_at) VALUES
('PC001', 'ノートパソコン', 'Lenovo ThinkPad E14 Gen3', 1, 'IT部門用', 1, 15, 89800, NOW(), NOW()),
('PR001', 'プリンター', 'Canon PIXUS TS3330', 1, '総務部用', 1, 8, 8500, NOW(), NOW()),
('MS001', 'マウス', 'Logicool M705', 1, '共用備品', 1, 25, 4500, NOW(), NOW());

-- 5. 初期データ (users)
-- password: pass1234 / admin123 (BCrypt ハッシュをそのまま投入)
INSERT INTO users (user_id, username, password_hash, role_level) VALUES
('user001', '田中', '$2a$10$5r7ls7N9rYAbUZQXdtKxK.GQO0gNfGRjUHRsx3KoRNYye8sPiqqPC', 0),
('admin01', '山田', '$2a$10$D03xdE8pSbg6ZrU7dHOdkO.nHVRFrsS5jvTk5AwJNPtscHzf0lg/G', 1);

-- 6. 動作確認クエリ例
-- SELECT COUNT(*) FROM items;
-- SELECT * FROM items LIMIT 5;

-- 実行後: Spring Boot を --spring.profiles.active=mysql で起動