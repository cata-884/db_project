-- NOTA: am pus functiile inaintea procedurilor pentru ca
-- p_analiza_sezoniera nu depinde de ele, dar este o practica
-- buna sa creezi functiile inainte ca sa fie disponibile pentru
-- proceduri/triggere viitoare care le-ar putea folosi

-- f_analiza_sentiment
-- Scaneaza textul unui comentariu si returneaza POZITIV/NEGATIV/NEUTRU
-- pe baza numarului de cuvinte cheie identificate.
-- Apelata din Java prin: SELECT f_analiza_sentiment(?) FROM dual;
CREATE OR REPLACE FUNCTION f_analiza_sentiment(
    p_text VARCHAR2
) RETURN VARCHAR2
IS
    v_text          VARCHAR2(4000);
    v_scor_pozitiv  NUMBER := 0;
    v_scor_negativ  NUMBER := 0;

    TYPE t_cuvinte IS VARRAY(30) OF VARCHAR2(50);

    cuv_poz t_cuvinte := t_cuvinte(
        'excelent', 'minunat', 'superb', 'extraordinar', 'fantastic',
        'recomand',  'frumos',  'bun',    'placut',       'placuta',
        'captivant', 'genial',  'reusit', 'incredibil',   'magnific',
        'spectaculos','adorabil','inteligent','original',  'memorabil'
    );

    cuv_neg t_cuvinte := t_cuvinte(
        'plictisitor','groaznic','slab',     'prost',     'ratat',
        'dezamagit',  'rau',     'oribil',   'mediocru',  'lent',
        'banal',      'fals',    'fortat',   'previzibil','enervant',
        'stupid',     'ridicol', 'inutil',   'scarbos',   'penibil'
    );

    FUNCTION numara_aparitii(p_haystack VARCHAR2, p_needle VARCHAR2)
        RETURN NUMBER
    IS
BEGIN
        IF p_haystack IS NULL OR p_needle IS NULL THEN
            RETURN 0;
        END IF;
        RETURN (LENGTH(p_haystack) - LENGTH(REPLACE(p_haystack, p_needle))) / LENGTH(p_needle);
END;
BEGIN
    IF p_text IS NULL OR LENGTH(TRIM(p_text)) = 0 THEN
        RETURN 'NEUTRU';
END IF;

    v_text := LOWER(p_text);

FOR i IN 1 .. cuv_poz.COUNT LOOP
        v_scor_pozitiv := v_scor_pozitiv + numara_aparitii(v_text, cuv_poz(i));
END LOOP;

FOR i IN 1 .. cuv_neg.COUNT LOOP
        v_scor_negativ := v_scor_negativ + numara_aparitii(v_text, cuv_neg(i));
END LOOP;

    IF v_scor_pozitiv > v_scor_negativ THEN
        RETURN 'POZITIV';
    ELSIF v_scor_negativ > v_scor_pozitiv THEN
        RETURN 'NEGATIV';
ELSE
        RETURN 'NEUTRU';
END IF;
END f_analiza_sentiment;
/
SHOW ERRORS;


-- f_calcul_similaritate_clienti
-- Calculeaza scor de similaritate intre 2 clienti (0..1)
-- pe baza:
--   * Jaccard similarity intre filmele vizionate (70%)
--   * Apropierea notelor acordate la filme comune (30%)
CREATE OR REPLACE FUNCTION f_calcul_similaritate_clienti(
    p_id_client_1 NUMBER,
    p_id_client_2 NUMBER
) RETURN NUMBER
IS
    v_filme_comune    NUMBER := 0;
    v_total_filme_1   NUMBER := 0;
    v_total_filme_2   NUMBER := 0;
    v_diff_medie      NUMBER;
    v_jaccard         NUMBER := 0;
    v_scor_rating     NUMBER := 0;
    v_scor_final      NUMBER := 0;
BEGIN
    IF p_id_client_1 = p_id_client_2 THEN
        RETURN 1;
END IF;

    -- Total filme vizionate de fiecare client
SELECT COUNT(DISTINCT vf.id_film)
INTO v_total_filme_1
FROM vizualizari v
         JOIN versiuni_film vf ON v.id_versiune = vf.id
WHERE v.id_client = p_id_client_1;

SELECT COUNT(DISTINCT vf.id_film)
INTO v_total_filme_2
FROM vizualizari v
         JOIN versiuni_film vf ON v.id_versiune = vf.id
WHERE v.id_client = p_id_client_2;

-- Filme comune
SELECT COUNT(DISTINCT vf1.id_film)
INTO v_filme_comune
FROM vizualizari v1
         JOIN versiuni_film vf1 ON v1.id_versiune = vf1.id
WHERE v1.id_client = p_id_client_1
  AND vf1.id_film IN (
    SELECT vf2.id_film
    FROM vizualizari v2
             JOIN versiuni_film vf2 ON v2.id_versiune = vf2.id
    WHERE v2.id_client = p_id_client_2
);

-- Jaccard: |A ∩ B| / |A ∪ B|
IF (v_total_filme_1 + v_total_filme_2 - v_filme_comune) > 0 THEN
        v_jaccard := v_filme_comune / (v_total_filme_1 + v_total_filme_2 - v_filme_comune);
END IF;

    -- Diferenta medie de nota la filmele recenzate de ambii
SELECT NVL(AVG(ABS(r1.nota - r2.nota)), 9)
INTO v_diff_medie
FROM recenzii r1
         JOIN recenzii r2 ON r1.id_film = r2.id_film
WHERE r1.id_client = p_id_client_1
  AND r2.id_client = p_id_client_2;

-- Scor rating: 1 daca diferenta = 0, 0 daca diferenta = 9
v_scor_rating := GREATEST(0, 1 - (v_diff_medie / 9));

    -- 70% similaritate filme + 30% similaritate rating
    v_scor_final := (v_jaccard * 0.7) + (v_scor_rating * 0.3);

RETURN ROUND(v_scor_final, 3);
END f_calcul_similaritate_clienti;
/
SHOW ERRORS;


-- Exemple de utilizare
-- SELECT f_analiza_sentiment('Filmul a fost minunat si emotionant') FROM dual;
-- SELECT f_calcul_similaritate_clienti(1, 2) FROM dual;