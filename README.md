# Spring Boot Project Template

## 概要

Dependabot アラート実験用の Spring Boot テンプレートプロジェクトです。Java 17、Spring Boot 2.7.18（意図的にダウングレード）、Gradle Kotlin DSL、自動化された CI/CD、依存関係管理ツールを含む包括的な開発環境を提供します。

## 前提条件

- Java 17 (Temurin 推奨)
- Docker / Docker Compose (MySQL 起動用)
- (オプション) Just task runner
- (オプション) SonarQube サーバーまたは SonarCloud アカウント（静的解析用）

## セットアップ

1. リポジトリをクローン

   ```bash
   git clone <repository-url>
   cd spring-boot-template
   ```

2. SonarQube の設定（後述の「設定」セクション参照）

3. MySQL コンテナの起動

   ```bash
   docker compose -f tools/local/docker-compose.yml up -d
   # または
   just db-start
   ```

   ※ 初回起動時にスキーマ作成とテストデータ投入が自動で行われます

4. プロジェクトのビルド

   ```bash
   ./gradlew build
   ```

5. テストの実行（H2 使用、MySQL 不要）

   ```bash
   ./gradlew test
   ```

6. アプリケーションの起動
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   # または
   just app-run
   ```

## 設定

### SonarQube セットアップ

GitHub Secrets に以下を設定してください：

- `SONAR_TOKEN`: SonarQube 認証トークン
- `SONAR_HOST_URL`: SonarQube サーバー URL（例：https://sonarcloud.io）

## 使用方法

### Gradle を使用する場合

- ビルド: `./gradlew build`
- テスト: `./gradlew test`
- 実行: `./gradlew bootRun`
- 静的解析: `./gradlew sonar`
- クリーン: `./gradlew clean`

### Just を使用する場合

- タスク一覧: `just` または `just --list`
- DB 起動: `just db-start`
- DB 停止: `just db-stop`
- DB 削除: `just db-clean`
- DB ログ: `just db-logs`
- 実行: `just app-run` (DB 自動起動)
- ビルド: `just app-build`
- テスト: `just app-test` (H2 使用)
- クリーン: `just app-clean`

## プロジェクト構造

```
.
├── src/
│   ├── main/
│   │   ├── java/          # アプリケーションソースコード
│   │   │   └── com/example/springboottemplate/
│   │   │       ├── order/         # 注文ドメイン
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderController.java
│   │   │       │   ├── OrderRepository.java
│   │   │       │   └── OrderStatus.java
│   │   │       └── ...
│   │   └── resources/
│   │       ├── application.yml        # 共通設定
│   │       ├── application-local.yml  # ローカル(MySQL)設定
│   │       ├── schema.sql             # DBスキーマ
│   │       └── data.sql               # 初期データ
│   └── test/
│       ├── java/          # テストコード
│       └── resources/
│           └── application.yml  # テスト用(H2)設定
├── .github/
│   ├── workflows/         # GitHub Actionsワークフロー
│   ├── dependabot.yml     # Dependabot設定
│   └── labeler.yml        # PR自動ラベリング設定
├── docker-compose.yml     # MySQL コンテナ定義
├── build.gradle.kts       # Gradleビルド設定
├── settings.gradle.kts    # Gradleプロジェクト設定
├── Justfile               # Justタスクランナー設定
└── README.md
```

## データベース

### MySQL (ローカル開発)

- **バージョン**: MySQL 8.4
- **ホスト**: localhost:3306
- **データベース**: app_db
- **ユーザー**: app_user / app_password

### H2 (テスト)

テスト実行時は H2 インメモリデータベースを使用します。MySQL 不要で CI/ローカルテストが実行可能です。

## CI/CD

- **GitHub Actions**: PR および main ブランチへの push 時に自動実行

  - 静的解析（SonarQube）
  - ビルド
  - テスト実行

- **Dependabot**: 毎週金曜日の朝 9 時（JST）に依存関係の自動更新をチェック

  - minor/patch バージョンのみグループ化して更新
  - major バージョンは個別 PR

- **自動ラベリング**: 変更されたファイルに応じて自動的に PR にラベルを付与
  - `app`: src/main 配下の変更
  - `workflow`: .github/workflows/または.github/actions/配下の.yml ファイルの変更
  - `docs`: .md ファイルの変更、または docs/ブランチ
  - `dependencies`: build.gradle.kts の変更

## API エンドポイント

### GET /api/hello

簡単な動作確認用のサンプルエンドポイント

**レスポンス例:**

```json
{
  "message": "Hello, World!",
  "timestamp": "2025-12-04T00:00:00Z"
}
```

**使用例:**

```bash
curl http://localhost:8080/api/hello
```

### GET /api/orders

注文一覧を取得します。

**レスポンス例:**

```json
[
  {
    "id": 1,
    "orderNumber": "ORD-001",
    "customerName": "山田太郎",
    "totalAmount": 15000.0,
    "status": "COMPLETED",
    "createdAt": "2025-12-01T10:00:00",
    "updatedAt": "2025-12-01T10:00:00"
  }
]
```

**使用例:**

```bash
curl http://localhost:8080/api/orders
```

### GET /api/orders/{id}

指定した ID の注文を取得します。

**使用例:**

```bash
curl http://localhost:8080/api/orders/1
```

### POST /api/orders

新しい注文を作成します。

**リクエストボディ:**

```json
{
  "customerName": "新規顧客",
  "totalAmount": 10000.0
}
```

**使用例:**

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "新規顧客", "totalAmount": 10000.00}'
```

## 開発ガイド

### ローカル開発

1. MySQL コンテナを起動

   ```bash
   just db-start
   # または docker compose up -d
   ```

2. アプリケーションを起動

   ```bash
   just app-run
   # または ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

3. API にアクセス
   ```bash
   curl http://localhost:8080/api/hello
   curl http://localhost:8080/api/orders
   ```

### テスト

- 全テストを実行: `./gradlew test` または `just app-test`
- テストレポートは `build/reports/tests/test/index.html` で確認可能
- テストは H2 インメモリ DB を使用（MySQL 不要）

## 利用可能な Gradle/Just タスク

| タスク     | Gradle コマンド                                             | Just コマンド    | 説明                         |
| ---------- | ----------------------------------------------------------- | ---------------- | ---------------------------- |
| タスク一覧 | -                                                           | `just`           | 利用可能なタスクを表示       |
| DB 起動    | -                                                           | `just db-start`  | MySQL コンテナを起動         |
| DB 停止    | -                                                           | `just db-stop`   | MySQL コンテナを停止         |
| DB 削除    | -                                                           | `just db-clean`  | MySQL コンテナとデータを削除 |
| ビルド     | `./gradlew build`                                           | `just app-build` | プロジェクトをビルド         |
| テスト     | `./gradlew test`                                            | `just app-test`  | テストを実行（H2 使用）      |
| 実行       | `./gradlew bootRun --args='--spring.profiles.active=local'` | `just app-run`   | アプリケーションを起動       |
| クリーン   | `./gradlew clean`                                           | `just app-clean` | ビルド成果物を削除           |
| 静的解析   | `./gradlew sonar`                                           | -                | SonarQube 解析を実行         |

## 注意事項

- Spring Boot 2.7.18 は意図的にダウングレードされており、Dependabot アラートの実験目的で使用されています
- 本番環境では最新の Spring Boot バージョンの使用を推奨します
- SonarQube 解析には SonarQube サーバーまたは SonarCloud のセットアップが必要です
