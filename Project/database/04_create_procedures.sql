-- Toate procedurile returneaza SYS_REFCURSOR ca sa fie usor
-- consumate din Java (JdbcTemplate.execute cu CallableStatement).

-- Returneaza filme recomandate pentru un client pe baza
-- categoriilor pe care le vizioneaza cel mai des, excluzand
-- filmele deja vazute. Maxim 10 rezultate, sortate dupa rating.
CREATE OR REPLACE PROCEDURE p_recomandari_pentru_client(
    p_id_client IN  NUMBER,
    p_rezultat  OUT SYS_REFCURSOR
)
IS
BEGIN
OPEN p_rezultat FOR
        -- tabela temporara, common table expression (CTE)
        WITH categorii_pref AS (
            -- Top 3 categorii vizionate de client
            SELECT f.id_categorie, COUNT(*) AS nr_vizionari
              FROM vizualizari v
              JOIN versiuni_film vf ON v.id_versiune = vf.id
              JOIN filme f          ON vf.id_film = f.id
             WHERE v.id_client = p_id_client
             GROUP BY f.id_categorie
             ORDER BY nr_vizionari DESC
             FETCH FIRST 3 ROWS ONLY
        ),
        filme_vazute AS (
            -- Filme deja vizionate de client (exclude din recomandari)
            SELECT DISTINCT vf.id_film
              FROM vizualizari v
              JOIN versiuni_film vf ON v.id_versiune = vf.id
             WHERE v.id_client = p_id_client
        )
SELECT f.id,
       f.titlu,
       f.descriere,
       f.rating,
       c.nume AS categorie,
       f.data_lansare
FROM filme f
         JOIN categorii c ON f.id_categorie = c.id
-- Recomanda 10 filme din categoriile preferate, excluzand cele deja vazute
WHERE f.id_categorie IN (SELECT id_categorie FROM categorii_pref)
  AND f.id NOT IN (SELECT id_film FROM filme_vazute)
ORDER BY f.rating DESC, f.data_lansare DESC
    FETCH FIRST 10 ROWS ONLY;
END p_recomandari_pentru_client;
/
SHOW ERRORS;


-- Returneaza profilul unui client: categoria preferata,
-- actorul preferat, rating mediu acordat, total filme vazute,
-- total recenzii lasate, sentiment dominant in recenzii.
CREATE OR REPLACE PROCEDURE p_profil_cinematografic(
    p_id_client IN  NUMBER,
    p_rezultat  OUT SYS_REFCURSOR
)
IS
BEGIN
OPEN p_rezultat FOR
SELECT
    (
        -- Categoria preferata
        SELECT c.nume
        FROM vizualizari v
                 JOIN versiuni_film vf ON v.id_versiune = vf.id
                 JOIN filme f          ON vf.id_film = f.id
                 JOIN categorii c      ON f.id_categorie = c.id
        WHERE v.id_client = p_id_client
        GROUP BY c.nume
        ORDER BY COUNT(*) DESC
            FETCH FIRST 1 ROW ONLY
    ) AS categorie_preferata,
    (
        -- Actor preferat (cel mai des intalnit in vizionari)
        SELECT a.nume || ' ' || a.prenume
        FROM vizualizari v
                 JOIN versiuni_film vf ON v.id_versiune = vf.id
                 JOIN distributie d    ON vf.id_film = d.id_film
                 JOIN actori a         ON d.id_actor = a.id
        WHERE v.id_client = p_id_client
        GROUP BY a.nume, a.prenume
        ORDER BY COUNT(*) DESC
            FETCH FIRST 1 ROW ONLY
    ) AS actor_preferat,
    (
        SELECT ROUND(AVG(nota), 2)
        FROM recenzii
        WHERE id_client = p_id_client
    ) AS rating_mediu,
    (
        SELECT COUNT(*)
        FROM vizualizari
        WHERE id_client = p_id_client
    ) AS total_filme_vazute,
    (
        SELECT COUNT(*)
        FROM recenzii
        WHERE id_client = p_id_client
    ) AS total_recenzii,
    (
        -- Sentimentul dominant din recenzii
        SELECT sentiment
        FROM recenzii
        WHERE id_client = p_id_client
          AND sentiment IS NOT NULL
        GROUP BY sentiment
        ORDER BY COUNT(*) DESC
            FETCH FIRST 1 ROW ONLY
    ) AS sentiment_dominant
FROM dual;
END p_profil_cinematografic;
/
SHOW ERRORS;

-- Pentru fiecare luna a anului returneaza top 5 categorii
-- vizionate. Util pentru predictii de filme in functie de
-- sezon (ex: Craciun -> Familie/Animatie; Halloween -> Horror).
CREATE OR REPLACE PROCEDURE p_analiza_sezoniera(
    p_rezultat OUT SYS_REFCURSOR
)
IS
BEGIN
OPEN p_rezultat FOR
SELECT luna, categorie, nr_vizionari, rank_categorie
FROM (
         SELECT
             EXTRACT(MONTH FROM v.data_vizualizare) AS luna,
             c.nume                                  AS categorie,
             COUNT(*)                                AS nr_vizionari,
             RANK() OVER (
                    PARTITION BY EXTRACT(MONTH FROM v.data_vizualizare)
                    ORDER BY COUNT(*) DESC
                ) AS rank_categorie
         FROM vizualizari v
                  JOIN versiuni_film vf ON v.id_versiune = vf.id
                  JOIN filme f          ON vf.id_film = f.id
                  JOIN categorii c      ON f.id_categorie = c.id
         WHERE v.data_vizualizare IS NOT NULL
         GROUP BY EXTRACT(MONTH FROM v.data_vizualizare), c.nume
     )
WHERE rank_categorie <= 5
ORDER BY luna, rank_categorie;
END p_analiza_sezoniera;
/
SHOW ERRORS;


-- p_clienti_similari
-- Returneaza top N clienti similari cu cel dat,
-- ordonati descrescator dupa scorul de similaritate.
-- Foloseste functia f_calcul_similaritate_clienti.
CREATE OR REPLACE PROCEDURE p_clienti_similari(
    p_id_client IN  NUMBER,
    p_top_n     IN  NUMBER DEFAULT 5,
    p_rezultat  OUT SYS_REFCURSOR
)
IS
BEGIN
OPEN p_rezultat FOR
SELECT id,
       nume || ' ' || prenume                  AS nume_complet,
       f_calcul_similaritate_clienti(p_id_client, id) AS scor_similaritate
FROM clienti
WHERE id != p_id_client
ORDER BY scor_similaritate DESC
    FETCH FIRST p_top_n ROWS ONLY;
END p_clienti_similari;
/
SHOW ERRORS;


-- p_predictii_sezoniere
-- Calculeaza un scor de predictie per film pentru luna data,
-- pe baza unui indice sezonier (avg vizionari in luna / avg vizionari annual)
-- aplicat peste rating-ul filmului.
CREATE OR REPLACE PROCEDURE p_predictii_sezoniere(
    p_luna     IN  NUMBER,
    p_top_n    IN  NUMBER DEFAULT 10,
    p_rezultat OUT SYS_REFCURSOR
)
IS
BEGIN
    OPEN p_rezultat FOR
        WITH viz_pe_luna AS (
            SELECT EXTRACT(MONTH FROM v.data_vizualizare) AS luna,
                   f.id_categorie,
                   COUNT(*) AS nr
              FROM vizualizari v
              JOIN versiuni_film vf ON v.id_versiune = vf.id
              JOIN filme f          ON vf.id_film = f.id
             WHERE v.data_vizualizare IS NOT NULL
             GROUP BY EXTRACT(MONTH FROM v.data_vizualizare), f.id_categorie
        ),
        medii AS (
            SELECT id_categorie, AVG(nr) AS media_lunara
              FROM viz_pe_luna
             GROUP BY id_categorie
        ),
        seasonal_index AS (
            SELECT v.luna,
                   v.id_categorie,
                   v.nr / NULLIF(m.media_lunara, 0) AS idx
              FROM viz_pe_luna v
              JOIN medii m ON v.id_categorie = m.id_categorie
        )
        SELECT f.id,
               f.titlu,
               c.nume AS categorie,
               f.rating,
               ROUND(si.idx, 2) AS factor_sezonier,
               ROUND(f.rating * si.idx, 2) AS scor_predictie
          FROM filme f
          JOIN categorii c ON f.id_categorie = c.id
          JOIN seasonal_index si ON f.id_categorie = si.id_categorie
         WHERE si.luna = p_luna
           AND f.rating IS NOT NULL
         ORDER BY scor_predictie DESC
         FETCH FIRST p_top_n ROWS ONLY;
END p_predictii_sezoniere;
/
SHOW ERRORS;


-- p_grupare_clienti
-- Realizeaza o grupare threshold-based a clientilor pe baza similaritatii
-- calculata cu f_calcul_similaritate_clienti (single-link clustering).
-- Clientii cu similaritate >= p_threshold fata de liderul grupei
-- sunt asignati aceleiasi grupe.
CREATE OR REPLACE PROCEDURE p_grupare_clienti(
    p_threshold IN  NUMBER DEFAULT 0.3,
    p_rezultat  OUT SYS_REFCURSOR
)
IS
    TYPE t_ids IS TABLE OF NUMBER;
    v_clienti       t_ids;
    v_grupe         t_ids := t_ids();
    v_grupa_curenta NUMBER := 0;
    v_sim           NUMBER;
BEGIN
    SELECT id BULK COLLECT INTO v_clienti FROM clienti ORDER BY id;
    v_grupe.EXTEND(v_clienti.COUNT);

    FOR i IN 1 .. v_clienti.COUNT LOOP
        v_grupe(i) := -1;
    END LOOP;

    FOR i IN 1 .. v_clienti.COUNT LOOP
        IF v_grupe(i) = -1 THEN
            v_grupa_curenta := v_grupa_curenta + 1;
            v_grupe(i) := v_grupa_curenta;

            FOR j IN i+1 .. v_clienti.COUNT LOOP
                IF v_grupe(j) = -1 THEN
                    v_sim := f_calcul_similaritate_clienti(v_clienti(i), v_clienti(j));
                    IF v_sim >= p_threshold THEN
                        v_grupe(j) := v_grupa_curenta;
                    END IF;
                END IF;
            END LOOP;
        END IF;
    END LOOP;

    DELETE FROM tmp_grupare_clienti;
    FOR i IN 1 .. v_clienti.COUNT LOOP
        INSERT INTO tmp_grupare_clienti (id_client, id_grupa)
        VALUES (v_clienti(i), v_grupe(i));
    END LOOP;

    OPEN p_rezultat FOR
        SELECT g.id_grupa,
               c.id   AS id_client,
               c.nume || ' ' || c.prenume AS nume_complet
          FROM tmp_grupare_clienti g
          JOIN clienti c ON g.id_client = c.id
         ORDER BY g.id_grupa, c.id;
END p_grupare_clienti;
/
SHOW ERRORS;


-- Exemple de utilizare din SQL*Plus
-- VARIABLE rc REFCURSOR;
-- EXEC p_recomandari_pentru_client(1, :rc);
-- PRINT rc;
--
-- EXEC p_profil_cinematografic(1, :rc);
-- PRINT rc;
--
-- EXEC p_analiza_sezoniera(:rc);
-- PRINT rc;