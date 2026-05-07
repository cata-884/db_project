-- Adauga campuri username si parola la clienti.
-- Pentru proiect tinem parolele in clar (NU production-safe)

ALTER TABLE clienti ADD (
    username VARCHAR2(50),
    parola   VARCHAR2(100)
);

ALTER TABLE clienti ADD CONSTRAINT uq_clienti_username UNIQUE (username);

BEGIN
FOR c IN (SELECT id FROM clienti) LOOP
UPDATE clienti
SET username = 'client' || c.id,
    parola   = 'parola' || c.id
WHERE id = c.id;
END LOOP;
COMMIT;
END;
/