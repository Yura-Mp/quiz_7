-- デバッグ用ユーザアカウントのデータ
-- name: "A", pass: "a"
INSERT INTO userAccount (userName, pass, roles)
  SELECT 'A', '$2y$05$jacX3FERwZJbF.UOuFe/9ON8FDyUGP4QIIKfVZSFyDfnb.B5GSM9O', 'ADMIN'
  WHERE NOT EXISTS (
    SELECT 1 FROM userAccount
      WHERE userName = 'A'
  );
