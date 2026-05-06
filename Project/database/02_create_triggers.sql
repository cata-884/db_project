-- Trigger 1: Recalculare automata rating film
-- Recalculeaza filme.rating ca media notelor din recenzii
-- la fiecare INSERT/UPDATE/DELETE pe tabela recenzii.
-- Foloseste compound trigger pentru a evita problema mutating table
CREATE OR REPLACE TRIGGER trg_recalc_rating_film
    FOR INSERT OR UPDATE OR DELETE ON recenzii
    COMPOUND TRIGGER

    TYPE t_film_ids IS TABLE OF NUMBER;

g_film_ids t_film_ids := t_film_ids();

    AFTER EACH ROW IS
BEGIN
        g_film_ids.EXTEND;
        IF DELETING THEN
            g_film_ids(g_film_ids.LAST) := :OLD.id_film; --luam id-ul filmulul sters pentru a recalcula ratingul
ELSE
            g_film_ids(g_film_ids.LAST) := :NEW.id_film; --recalculeaza ratingul pentru filmul
END IF;
END AFTER EACH ROW;

    AFTER STATEMENT IS
BEGIN
FOR i IN 1 .. g_film_ids.COUNT LOOP
UPDATE filme f
SET rating = (
    SELECT NVL(ROUND(AVG(nota), 1), 0)
    FROM recenzii r
    WHERE r.id_film = f.id
)
WHERE id = g_film_ids(i);
END LOOP;
END AFTER STATEMENT;
END trg_recalc_rating_film;
/
SHOW ERRORS;


-- Trigger 2: Validare actor in distributie
-- La INSERT in recenzii_actori se verifica daca actorul respectiv
-- joaca efectiv in filmul recenziei. Altfel se arunca exceptie
-- pe care aplicatia Java o poate prinde.
CREATE OR REPLACE TRIGGER trg_validare_actor_recenzie
    BEFORE INSERT ON recenzii_actori
    FOR EACH ROW
DECLARE
v_id_film  recenzii.id_film%TYPE;
v_count    NUMBER;
BEGIN
    -- Filmul caruia ii apartine recenzia
SELECT id_film
INTO v_id_film
FROM recenzii
WHERE id = :NEW.id_recenzie;

-- Verifica daca actorul e in distributia filmului
SELECT COUNT(*)
INTO v_count
FROM distributie
WHERE id_film = v_id_film
  AND id_actor = :NEW.id_actor;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'Actorul cu id=' || :NEW.id_actor ||
            ' nu joaca in filmul cu id=' || v_id_film
        );
END IF;
END trg_validare_actor_recenzie;
/
SHOW ERRORS;


-- Trigger 3: Setare automata sentiment recenzie
-- Daca la INSERT/UPDATE clientul nu specifica sentiment,
-- acesta se completeaza automat din nota:
--   1-3  -> NEGATIV
--   4-7  -> NEUTRU
--   8-10 -> POZITIV
CREATE OR REPLACE TRIGGER trg_set_sentiment_recenzie
    BEFORE INSERT OR UPDATE ON recenzii
                                FOR EACH ROW
BEGIN
    IF :NEW.sentiment IS NULL AND :NEW.nota IS NOT NULL THEN
        IF :NEW.nota BETWEEN 1 AND 3 THEN
            :NEW.sentiment := 'NEGATIV';
        ELSIF :NEW.nota BETWEEN 4 AND 7 THEN
            :NEW.sentiment := 'NEUTRU';
ELSE
            :NEW.sentiment := 'POZITIV';
END IF;
END IF;
END trg_set_sentiment_recenzie;
/
SHOW ERRORS;