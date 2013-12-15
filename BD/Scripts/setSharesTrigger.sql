CREATE OR REPLACE TRIGGER setShares AFTER UPDATE OF numshares ON "Share"
DECLARE
BEGIN
  DELETE FROM "Share" WHERE numshares = 0;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
END;