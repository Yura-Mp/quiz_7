/* schema.sql: 主にCREATE(テーブル作成&初期化)系のコマンドを記述．
 * ※このsqlコマンドファイルは，サービス起動時に毎回適用されることを想定する．
 * ・CREATE TABLE文の場合は，すでにあるテーブルに重ねて作成されることによるエラーを防ぐため，必ず存在判定をすることとする．
*/

-- ユーザアカウントデータ格納用テーブル (Author: Yura-Mp)
CREATE TABLE IF NOT EXISTS userAccount (
  id IDENTITY, -- ユーザID(主キー)．
  userName VARCHAR(20) UNIQUE NOT NULL, -- ユーザの名前．サービス上で表示される名前として用いられることを想定．
  pass VARCHAR NOT NULL, -- アカウントのパスワード．データとしてはbcryptでハッシュ化されたデータを保持することを想定．NULL(パスワード未設定)である場合，パスワードなしでログインできるものとする．
  available BOOLEAN DEFAULT TRUE -- アカウントが利用可能(有効)であるかを示す0/1フラグ．TRUEならば利用可能，FALSEならば利用不可能(アカウント停止)を示す．
);

CREATE TABLE IF NOT EXISTS roleList (
  id IDENTITY PRIMARY KEY,
  roles VARCHAR,
  UNIQUE(roles)
);

-- ユーザアカウント役職(roles, authorities)管理テーブル (Author: Yura-Mp)
/* <Updated: 2024/11/24>
 * 　Spring Securityでは，単一のユーザアカウントに対して複数の役職
 * (roles, authorities) に属し得ることを想定しているため，ユーザアカウントと役職とを
 * 多対多関連で対としたテーブルを組み込む．
 * この事実により，当初のDBスキーマから要所において変更が成されている．
*/
CREATE TABLE IF NOT EXISTS userRoles (
  id IDENTITY REFERENCES userAccount(id), -- ユーザID．ユーザアカウントデータテーブル(id)を外部キー参照．
  roles VARCHAR REFERENCES roleList(roles), -- ユーザが属する役職．
  UNIQUE(id, roles) -- 複合主キー(扱い)．
);
