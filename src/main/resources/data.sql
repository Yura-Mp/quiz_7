INSERT INTO roleList (roles)
  SELECT 'USER'
  WHERE NOT EXISTS (
    SELECT 1 FROM roleList
      WHERE id = (SELECT id FROM roleList WHERE roles = 'USER')
  );

INSERT INTO roleList (roles)
  SELECT 'ADMIN'
  WHERE NOT EXISTS (
    SELECT 1 FROM roleList
      WHERE id = (SELECT id FROM roleList WHERE roles = 'ADMIN')
  );

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

INSERT INTO quizFormatList (quizFormat)
  SELECT '4choices'
    WHERE NOT EXISTS (
      SELECT 1 FROM quizFormatList
        WHERE quizFormat = '4choices'
    );

INSERT INTO gameroom (hostUserID, roomName, description)
  SELECT (SELECT id FROM userAccount WHERE userName = 'A'), 'testroom', 'This is a test room.'
  WHERE NOT EXISTS (
    SELECT 1 FROM gameroom
      WHERE hostUserID = (SELECT id FROM userAccount WHERE userName = 'A')
        AND roomName = 'testroom'
  );

COMMIT;
