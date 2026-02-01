# Java Project Sample

Java 17 + Spring Boot 3.5.9 のサンプルプロジェクトです。

## 使用方法

- `just` - 利用可能なタスクを表示
- `just db-start` - DB を起動
- `just db-stop` - DB を停止
- `just db-clean` - DB を削除
- `just app-run` - アプリケーションを起動（DB 自動起動）
- `just app-build` - ビルド
- `just app-test` - テスト実行
- `just app-clean` - ビルド成果物を削除

## プロジェクト構造

```
src/main/java/com/example/app/
├── SpringBootTemplateApplication.java
├── controller/
├── domain/
├── usecase/
└── infra/
```

## ライブラリ

| カテゴリ    | ライブラリ             | バージョン |
|---------|-------------------|-------|
| フレームワーク | Spring Boot       | 3.5.9 |
| Web     | Spring MVC        | (管理)  |
| データアクセス | Spring Data JPA   | (管理)  |
| AWS     | Spring Cloud AWS  | 3.4.2 |
| DB ドライバ | MySQL Connector/J | (管理)  |
| テスト DB  | H2 Database       | (管理)  |
| JSON    | Jackson           | (管理)  |
| ユーティリティ | Lombok            | (管理)  |
