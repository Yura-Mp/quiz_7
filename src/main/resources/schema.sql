/* schema.sql: 主にCREATE(テーブル作成&初期化)系のコマンドを記述．
 * ※このsqlコマンドファイルは，サービス起動時に毎回適用されることを想定する．
 * ・CREATE TABLE文の場合は，すでにあるテーブルに重ねて作成されることによるエラーを防ぐため，必ず存在判定をすることとする．
*/

-- ユーザアカウントデータ格納用テーブル (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS userAccount (
  ID IDENTITY, -- ユーザID(主キー)．
  userName VARCHAR(20) UNIQUE NOT NULL, -- ユーザの名前．サービス上で表示される名前として用いられることを想定．
  pass VARCHAR NOT NULL, -- アカウントのパスワード．データとしてはbcryptでハッシュ化されたデータを保持することを想定．NULL(パスワード未設定)である場合，パスワードなしでログインできるものとする．
  available BOOLEAN DEFAULT TRUE -- アカウントが利用可能(有効)であるかを示す0/1フラグ．TRUEならば利用可能，FALSEならば利用不可能(アカウント停止)を示す．
);

CREATE TABLE IF NOT EXISTS roleList (
  ID IDENTITY PRIMARY KEY,
  roles VARCHAR NOT NULL,
  UNIQUE(roles)
);

-- ユーザアカウント役職(roles, authorities)管理テーブル (Author: Yura-Mp)
/* <Updated: 2024/11/24>
 * Spring Securityでは，単一のユーザアカウントに対して複数の役職
 * (roles, authorities) に属し得ることを想定しているため，ユーザアカウントと役職とを
 * 多対多関連で対としたテーブルを組み込む．
 * この事実により，当初のDBスキーマから要所において変更が成されている．
*/
CREATE TABLE IF NOT EXISTS userRoles (
  ID BIGINT REFERENCES userAccount(ID), -- ユーザID．ユーザアカウントデータテーブル(id)を外部キー参照．
  roles VARCHAR REFERENCES roleList(roles), -- ユーザが属する役職．
  PRIMARY KEY(id, roles) -- 複合主キー．
);

-- ゲームルーム管理テーブル (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS gameRoom (
  ID IDENTITY, -- ルームID．
  hostUserID BIGINT NOT NULL REFERENCES userAccount(ID), -- ルームのホスト．
  roomName VARCHAR(30), -- ルーム名．
  description VARCHAR -- ルームの説明文．
);

-- 問題形式リストテーブル (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS quizFormatList (
  ID IDENTITY, -- 問題形式ID．
  quizFormat VARCHAR(40) UNIQUE NOT NULL -- 問題形式文字列．
);

-- 問題管理テーブル(SQL-JSONのハイブリッド) (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS quizTable (
  ID IDENTITY, -- 問題ID．
  quizFormatID BIGINT NOT NULL REFERENCES quizFormatList(ID), -- 問題形式ID．
  authorID BIGINT NOT NULL REFERENCES userAccount(ID), -- 著者/所有者ID．
  title VARCHAR, -- 問題タイトル．
  description VARCHAR, -- 問題文．
  timelimit DOUBLE PRECISION NOT NULL DEFAULT 20.0 CHECK(timelimit >= 0.0), -- 問題制限時間．
  point BIGINT NOT NULL DEFAULT 10000, -- 問題点数．
  quizJSON JSON -- 問題JSON．
);

-- ルーム別問題管理テーブル (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS hasQuiz (
  roomID BIGINT REFERENCES gameRoom(ID), -- ルームID．
  quizID BIGINT REFERENCES quizTable(ID), -- 問題ID．
  index INT, -- 問題インデックス．
  PRIMARY KEY(roomID, quizID) -- 複合主キー．
);

COMMIT;
