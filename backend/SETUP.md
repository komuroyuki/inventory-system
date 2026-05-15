# 在庫管理システム 開発環境構築・総合手順書

本資料は、Docker環境の構築から、個別のMySQLコンテナ作成、そしてGitHubリポジトリを用いたチーム開発環境の立ち上げまでを網羅した手順書です。

## 第1部：Docker 環境構築手順書（改訂版）

### 1. 目的

本手順書は、開発環境として Docker Desktop を利用できる状態にすることを目的とする。

### 2. 前提

- Windows / Mac のいずれかを使用
- ターミナル（コマンド入力）が使えること
- コマンドはすべてターミナルで実行する（Docker Desktopの画面では入力しない）

### 3. Docker Desktopのインストール

- Docker公式サイトからDocker Desktopをインストールする
- インストール完了後、Docker Desktopを起動する

### 4. ターミナルの起動

コマンド入力は以下で行う。

#### VSCodeの場合（推奨）
```
Terminal → New Terminal
```

#### Mac標準の場合
```
アプリ → Terminal
```

### 5. Dockerの動作確認

以下コマンドを実行する。

```bash
docker -v
```

##### 正常時の表示例
```
Docker version 24.x.x, build xxxx
```
※バージョンは異なっていても問題なし

##### エラー時
```
command not found
```
→ Docker Desktopが起動していない、または未インストール

### 6. Docker Composeの確認

Docker の機能確認を行う。以下コマンドを実行：

```bash
docker compose version
```

##### 正常時の表示例
```
Docker Compose version v2.x.x
```

##### エラー時
```
command not found
```
→ Docker Desktopが起動していない可能性あり

### 7. Docker Desktopの役割

Docker Desktopは以下の役割を持つ：

- コンテナ実行環境の提供
- コンテナ状態の可視化（GUI）

👉 コマンド操作は行わない

### 8. コマンド実行ルール

| 項目 | 実行場所 |
|-----|--------|
| docker -v | ターミナル |
| docker compose | ターミナル |
| コンテナ操作 | ターミナル |
| 状態確認 | Docker Desktop画面 |

### 9. 正常確認チェックリスト

以下がすべて成功すれば環境構築完了：

- [ ] docker -v
- [ ] docker compose version

### 10. 次のステップ

環境構築完了後は以下へ進む：

- MySQLコンテナ作成
- Spring Boot接続設定
- React連携

### まとめ

- Docker Desktop = 実行エンジン
- ターミナル = 操作場所

---

## 第2部：MySQL環境構築手順（Docker利用）

### 1. MySQL公式イメージを取得

Macのターミナルを開き、以下コマンドを実行します。

```bash
docker image pull mysql:8.0
```

##### 実行後の表示例
```
8.0: Pulling from library/mysql
Digest: sha256:xxxxxxxxxxxxxxxx
Status: Downloaded newer image for mysql:8.0
docker.io/library/mysql:8.0
```

※ バージョンを固定することで、開発メンバー間の環境差異を防ぎます。

### 2. Data Volume を作成

以下コマンドを実行し、「inventory」という名前のData Volumeを作成します。

```bash
docker volume create inventory
```

##### 実行後の表示例
```
inventory
```

※ Data Volume を利用することで、コンテナを削除してもDBデータを保持できます。

### 3. MySQLコンテナを作成・起動

以下コマンドを実行して、MySQLコンテナを作成します。

```bash
docker container run --name inventory-mysql \
-v inventory:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=teamB \
-p 3306:3306 \
-d mysql:8.0
```

##### 実行後の表示例
```
c1d2e3f4g5h6i7j8k9l0
```

※ 長い英数字（コンテナID）が表示されれば成功です。

#### オプション説明

| オプション | 内容 |
|----------|------|
| --name inventory-mysql | コンテナ名を指定 |
| -v inventory:/var/lib/mysql | Data Volumeをマウント |
| -e MYSQL_ROOT_PASSWORD=teamB | rootユーザーのパスワード設定 |
| -p 3306:3306 | PC側からMySQLへ接続可能にする |
| -d | バックグラウンド起動 |

#### ポート番号について
- 3306 は MySQL の標準ポート番号です。
- -p 3306:3306 は、PC側ポート : コンテナ側ポート を表しています。
- 通常は 3306:3306 を利用しますが、PC側で3306が既に使用されている場合は、以下のように変更します。

```bash
-p 3307:3306
```

### 4. 起動中コンテナ確認

以下コマンドを実行します。

```bash
docker ps
```

##### 実行後の表示例
```
CONTAINER ID   IMAGE       COMMAND                  STATUS         PORTS                    NAMES
abcd1234efgh   mysql:8.0   "docker-entrypoint.s…"   Up 10 seconds  0.0.0.0:3306->3306/tcp inventory-mysql
```

inventory-mysql が表示されていれば成功です。

### 5. MySQLコンテナへ接続

以下コマンドを実行します。

```bash
docker exec -it inventory-mysql bash
```

##### 実行後の表示例
```
bash-5.1#
```

※ この表示になれば、現在はMySQLコンテナ内部に入っています。

### 6. MySQLへログイン

以下コマンドを実行します。

```bash
mysql -u root -p
```

##### 実行後の表示例
```
Enter password:
```

パスワード入力を求められるので、コンテナ作成時に設定した `teamB` を入力してください。

ログイン成功後は以下のように表示されます。
```
Welcome to the MySQL monitor.
mysql>
```

### 7. サンプルDB作成

以下コマンドを順番に実行します。

```sql
create database test;
use test;
create table test(
    id int,
    name varchar(10)
);
insert into test(id, name)
values (1, "yamada");
```

##### 実行後の表示例
```
Query OK, 1 row affected
```

### 8. データ確認

以下SQLを実行します。

```sql
select * from test;
```

##### 実行後の表示例
```
+------+--------+
| id   | name   |
+------+--------+
| 1    | yamada |
+------+--------+
```

上記のように表示されれば成功です。

### 9. コンテナから抜ける

以下コマンドを実行します。

```bash
exit
```

##### 実行後の流れ

1. 1回目の exit：`root@abcd1234efgh:/#` へ戻る
2. 2回目の exit：MacBook-Air %（自身のPC環境）へ戻る

※ MySQL → コンテナ → OS の順で戻るため、exit を2回実行してください。

---

## 第3部：チームメンバー向け 開発環境構築手順書

本手順書は、GitHub上に作成済みの在庫管理システムプロジェクトを各メンバーのPCへ取得し、開発できる状態にするための手順です。

### 開発構成：
```
React(frontend) → Spring Boot(backend) → MySQL(Docker)
```

### 事前準備

以下ソフトウェアをインストールしてください。

| ソフトウェア | 用途 |
|-----------|------|
| Docker Desktop | MySQL実行 |
| Visual Studio Code | 開発 |
| Node.js | React実行 |
| Java 17 | Spring Boot実行 |
| Git | ソースコード取得 |

### 1. GitHubリポジトリ取得

リーダーから共有されたGitHub URLを使用します。

例：`https://github.com/komuroyuki/inventory-system.git`

1. VSCodeを起動
2. ターミナルを開く：メニューの `Terminal → New Terminal`
3. デスクトップへ移動：`cd Desktop`
4. GitHubリポジトリを取得：
   ```bash
   git clone https://github.com/komuroyuki/inventory-system.git
   ```
   実行例：`Cloning into 'inventory-system'...`

### 2. プロジェクトをVSCodeで開く

1. inventory-systemへ移動：`cd inventory-system`
2. VSCodeで開く：`code .`

実行後、以下構成が表示されれば成功です。

```
inventory-system
├ backend
└ frontend
```

### 3. MySQL(Docker)起動

1. Docker Desktop起動：Docker Desktopが起動していることを確認します。
2. backendへ移動：VSCodeターミナルで `cd backend`
3. docker-compose.yml確認：ファイルが存在することを確認します。
4. MySQLコンテナ起動：`docker compose up -d`
   実行例：`Creating inventory-mysql ...`
5. コンテナ確認：`docker ps` で inventory-mysql が表示されれば成功です。

### 4. Spring Boot起動

1. backendフォルダ確認：`pwd` で `.../inventory-system/backend` にいることを確認。
2. Spring Boot起動：`./gradlew bootRun`
   実行例：`Started BackendApplication`

### 5. React起動

1. 新しいターミナルを開く：VSCodeで `Terminal → New Terminal`
2. frontendへ移動：`cd frontend`（backendにいる場合は `cd ../frontend`）
3. Node Modulesインストール：初回のみ `npm install`
4. React起動：`npm run dev`
   実行例：`Local: http://localhost:5173/`

### 6. 画面確認

ブラウザで以下URLを開きます。

```
http://localhost:5173/
```

React画面が表示されれば成功です。

### 7. 開発時の起動状態

開発時は以下3つが必要です。

- Docker：MySQL
- Spring Boot：API
- React：画面

### 8. 開発時のターミナル構成

#### ターミナル①
```bash
cd backend
docker compose up -d
./gradlew bootRun
```

#### ターミナル②
```bash
cd frontend
npm run dev
```

### 9. よくあるエラー

| エラー | 原因 | 対応 |
|-------|------|------|
| npm: command not found | Node.js 未インストール | Node.js をインストール |
| Port 3306 already in use | MySQLが既に起動している | docker-compose.yml のポート番号変更 |
| Failed to connect to database | Docker未起動 | Docker Desktopを起動 |

### 10. 開発開始前に確認すること

以下3つが起動していること。

| 確認項目 | URL / 確認方法 |
|--------|----------|
| React | http://localhost:5173 |
| Spring Boot | http://localhost:8080 |
| MySQL | docker ps で確認 |
