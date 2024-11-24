-- デバッグ用ユーザアカウントのデータ
-- name: "A", pass: "a"
INSERT INTO userAccount (userName, pass)
  SELECT 'A', '$2y$05$jacX3FERwZJbF.UOuFe/9ON8FDyUGP4QIIKfVZSFyDfnb.B5GSM9O'
  WHERE NOT EXISTS (
    SELECT 1 FROM userAccount
      WHERE userName = 'A'
  );
INSERT INTO userRoles (id, roles)
  SELECT (SELECT id FROM userAccount WHERE userName = 'A'), 'ADMIN'
  WHERE NOT EXISTS (
    SELECT 1 FROM userRoles
      WHERE id = (SELECT id FROM userAccount WHERE userName = 'A')
        AND roles = 'ADMIN'
  );

COMMIT;
