-- H2データベース用初期データ投入
INSERT INTO items (item_number, item_name, model_number, in_stock, remarks, category_id, quantity, price, registered_at, updated_at) VALUES
('PC001', 'ノートパソコン', 'Lenovo ThinkPad E14 Gen3', 1, 'IT部門用', 1, 15, 89800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PR001', 'プリンター', 'Canon PIXUS TS3330', 1, '総務部用', 1, 8, 8500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MS001', 'マウス', 'ワイヤレスマウス Logicool M705', 1, '共用備品', 1, 25, 4500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('KB001', 'キーボード', 'メカニカルキーボード FILCO Majestouch', 1, '開発部用', 1, 12, 15800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MN001', 'モニター', '24インチ液晶モニター Dell P2414H', 1, '会議室用', 1, 6, 25000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PN001', 'ボールペン', '三色ボールペン', 1, '文房具', 2, 100, 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PP001', 'コピー用紙', 'A4コピー用紙 500枚', 1, 'プリンター用', 2, 50, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('FL001', 'ファイル', 'クリアファイル A4 20ポケット', 1, '書類整理用', 2, 30, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ST001', 'ホッチキス', 'ステープラー MAX HD-10D', 1, '文房具', 2, 20, 800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ER001', '消しゴム', 'MONO消しゴム', 1, '文房具', 2, 80, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TB001', '会議室テーブル', '8人用会議テーブル', 1, '第1会議室', 3, 4, 45000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CH001', 'オフィスチェア', 'エルゴノミクスチェア', 1, '総務部用', 3, 18, 28000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CB001', '書庫', '3段スチール書庫', 1, '資料保管用', 3, 8, 18000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('WB001', 'ホワイトボード', '壁掛けホワイトボード 120cm', 1, '会議室用', 3, 6, 8500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VC001', '掃除機', 'コードレス掃除機 Dyson V8', 1, '清掃用', 4, 3, 45000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CL001', '洗剤', '多目的洗剤 500ml', 1, '清掃用', 4, 25, 350, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GB001', 'ゴミ箱', '分別ゴミ箱 3分別', 1, '各フロア用', 4, 15, 2500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USB001', 'USBメモリ', '32GB USBメモリ SanDisk', 1, 'データ転送用', 1, 40, 1200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LAN001', 'LANケーブル', 'Cat6 LANケーブル 3m', 1, 'ネットワーク機器', 1, 35, 800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PWR001', '電源タップ', '6口電源タップ 雷サージ保護', 1, 'PC周辺機器', 1, 20, 1500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);