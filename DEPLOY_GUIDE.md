# GitHub Actions デプロイ設定手順

## 1. GitHub Secrets の設定

GitHubリポジトリの「Settings」→「Secrets and variables」→「Actions」で以下のSecretを追加してください：

### 必要なSecrets:
- `EC2_SSH_PRIVATE_KEY`: SSH秘密鍵の内容（MyKeyPair.pemファイルの中身全体）
- `EC2_HOST`: バスチョンホストのパブリックIP（3.106.239.127）

### SSH秘密鍵の取得方法:
```bash
# Windows PowerShellで実行
Get-Content "C:\Users\user\.ssh\MyKeyPair.pem" | Out-String
```

この出力内容をそのまま `EC2_SSH_PRIVATE_KEY` に設定してください。

## 2. デプロイの流れ

1. **開発者がコードをプッシュ**
   ```bash
   git add .
   git commit -m "新機能追加"
   git push origin main
   ```

2. **GitHub Actionsが自動実行**
   - バックエンド（Spring Boot）のビルド
   - フロントエンド（React）のビルド
   - EC2への自動デプロイ
   - Nginxの再起動

3. **即座に本番環境に反映**
   - http://3.106.239.127 で確認可能

## 3. 今後の開発ワークフロー

### バックエンド開発:
- `backend/demo/src/main/java/` 以下のファイルを編集
- 新しいAPIエンドポイントの追加
- データベーススキーマの変更

### フロントエンド開発:
- `frontend/src/` 以下のファイルを編集
- 新しいページ・コンポーネントの追加
- APIとの連携強化

### 開発・テスト・デプロイサイクル:
```bash
# 1. 機能開発
git checkout -b feature/new-feature

# 2. コード編集
# (ItemService.java, React components など)

# 3. ローカルテスト
cd backend/demo && mvn test
cd frontend && npm test

# 4. プッシュでCI/CD実行
git add .
git commit -m "Add new feature"
git push origin feature/new-feature

# 5. Pull Request → main ブランチマージ
# 6. 自動デプロイ実行
```

## 4. 継続的な改善
- データベースマイグレーション
- テスト自動化の強化
- ロードバランサーの追加
- SSL証明書の設定
