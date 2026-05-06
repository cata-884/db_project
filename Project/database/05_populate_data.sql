SET SERVEROUTPUT ON;

-- ROUGH DRAFT FOR DATA POPULATION. IT IS NOT PERFECT.
    -- DUPLICATES MAY EXIST EXCEPT FOR ID'S.
    -- TODO WILL BE TO ADD MORE DATA/UNIQUE CONSTRAINTS TO AVOID DUPLICATES AND TO MAKE THE DATA SET MORE REALISTIC.
DECLARE

TYPE varr IS VARRAY(100) OF VARCHAR2(500);

  l_cat varr := varr(
            'Acțiune',
            'Comedie',
            'Dramă',
            'Horror',
            'SF',
            'Documentar',
            'Animație',
            'Thriller',
            'Romance',
            'Aventură',
            'Istoric',
            'Western',
            'Musical',
            'Mister',
            'Biografie'
        );

  l_titluri varr := varr(
                'Moartea domnului Lăzărescu',
                'Filantropica',
                'Mirciulică',
                'Teambuilding',
                'Două Lozuri',
                'Aferim!',
                'Bacalaureat',
                'Poziția Copilului',
                'Nuntă Mută',
                'Restul e tăcere',
                'Occident',
                '4 luni, 3 săptămâni și 2 zile',
                'Sieranevada',
                'Câini',
                'Taximetriști'
            );

  l_desc varr := varr(
            'O poveste impresionantă despre societate.',
            'O comedie neagră despre dorința de îmbogățire.',
            'Aventurile unui tânăr în provincie.',
            'Satira mediului corporatist din România.',
            'Trei bărbați pornesc în căutarea unui bilet câștigător.',
            'O incursiune istorică în Țara Românească.',
            'Dilemele morale ale unui tată.',
            'Relația complexă dintre mamă și fiu.',
            'O nuntă celebrată în liniște deplină.',
            'Povestea realizării primului film românesc.'
         );

  l_nume varr := varr(
            'Popescu',
            'Ionescu',
            'Dumitru',
            'Stan',
            'Gheorghe',
            'Rusu',
            'Munteanu',
            'Matei',
            'Constantin',
            'Lupu'
         );
  l_prenume varr := varr(
                'Andrei',
                'Elena',
                'Cristian',
                'Maria',
                'Stefan',
                'Ioana',
                'Dragos',
                'Mihaela',
                'Adrian',
                'Laura'
            );
  l_nume_scena varr := varr('The Star', 'Veteranul', 'Noua Speranță', 'Talentul', 'Maestrul');

  l_roluri varr := varr('PROTAGONIST', 'ANTAGONIST', 'SECUNDAR', 'EPISODIC', 'DUBLURA');
  l_orase varr := varr('București', 'Cluj-Napoca', 'Iași', 'Timișoara', 'Constanța', 'Brașov', 'Craiova', 'Galați');
  l_stari varr := varr('IN_PROGRESS', 'COMPLETED', 'PAUSED', 'ABANDONED');

    TYPE eticheta_r IS RECORD (
      denumire   etichete.denumire%TYPE,
      sentiment  etichete.sentiment%TYPE
    );
    TYPE eticheta_list IS TABLE OF eticheta_r;
    l_etichete   eticheta_list := eticheta_list(
      eticheta_r('MI_A_PLACUT',                     'POZITIV'),
      eticheta_r('NU_MI_A_PLACUT',                  'NEGATIV'),
      eticheta_r('EMOTIONANT',                      'POZITIV'),
      eticheta_r('AMUZANT',                         'POZITIV'),
      eticheta_r('INSPAIMANTATOR',                  'NEUTRU'),
      eticheta_r('RELAXANT',                        'POZITIV'),
      eticheta_r('PLICTISITOR',                     'NEGATIV'),
      eticheta_r('SURPRINZATOR',                    'NEUTRU'),
      eticheta_r('AS_RECOMANDA',                    'POZITIV'),
      eticheta_r('NU_AS_RECOMANDA',                 'NEGATIV'),
      eticheta_r('AS_MAI_VIZIONA',                  'POZITIV'),
      eticheta_r('SCENARIU_BUN',                    'POZITIV'),
      eticheta_r('SCENARIU_SLAB',                   'NEGATIV'),
      eticheta_r('ACTOR_PRINCIPAL_APRECIAT',        'POZITIV'),
      eticheta_r('REGIE_EXCELENTA',                 'POZITIV'),
      eticheta_r('COLOANA_SONORA_BUNA',             'POZITIV'),
      eticheta_r('EFECTE_VIZUALE_IMPRESIONANTE',    'POZITIV'),
      eticheta_r('BUN_PENTRU_FAMILIE',              'NEUTRU'),
      eticheta_r('BUN_PENTRU_COPII',                'NEUTRU'),
      eticheta_r('PENTRU_ADULTI',                   'NEUTRU')
   );

  v_id_1 NUMBER;
  v_id_2 NUMBER;
  v_temp_id NUMBER;
  v_film_id NUMBER;
  v_idx NUMBER;
BEGIN

  -- CATEGORII
  DBMS_OUTPUT.PUT_LINE('Inserare categorii...');
FOR i IN 1..l_cat.COUNT LOOP
    INSERT INTO categorii (id, nume)
    VALUES (seq_categorii.NEXTVAL, l_cat(i));
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare categorii... GATA');

  -- FILME
  DBMS_OUTPUT.PUT_LINE('Inserare filme...');
FOR i IN 1..20 LOOP
-- fetch random category id for the film
SELECT id INTO v_id_1 FROM categorii ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO filme (id, titlu, descriere, data_lansare, id_categorie, rating)
VALUES (
           seq_filme.NEXTVAL,
            -- Random title from list
           l_titluri(TRUNC(DBMS_RANDOM.VALUE(1, l_titluri.COUNT + 1))),
            -- Random description
           l_desc(TRUNC(DBMS_RANDOM.VALUE(1, l_desc.COUNT + 1))),
            -- Random release date in last 20 years
           TO_DATE('2000-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 8000),
           v_id_1,
           -- TODO
           -- IT WILL BE DECIDED WHETHER THE RATING IS AFFECTED BY USER REVIEWS OR NOT.
           -- FOR NOW, IT IS GENERATED RANDOMLY.
           -- MAYBE WE START FROM THIS RATING, THEN THE USERS CAN INFLUENCE IT WITH THEIR REVIEWS.
           ROUND(DBMS_RANDOM.VALUE(1, 10), 1)
       );
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare filme... GATA');

  -- VERSIUNI_FILM
  DBMS_OUTPUT.PUT_LINE('Inserare versiuni_film...');
FOR i IN 1..25 LOOP
SELECT id INTO v_id_1 FROM filme ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO versiuni_film (id, id_film, denumire_versiune, rezolutie, limbi, format)
VALUES (
            -- LANGUAGE, QUALITY, FORMAT RANDOMLY GENERATED
           seq_versiuni.NEXTVAL,
           v_id_1,
           'Versiunea ' || i,
           CASE TRUNC(DBMS_RANDOM.VALUE(0, 9))
               WHEN 0 THEN 'TINY' WHEN 1 THEN 'LOW' WHEN 2 THEN 'SD' WHEN 3 THEN 'DVD'
               WHEN 4 THEN 'HD' WHEN 5 THEN 'FULL_HD' WHEN 6 THEN 'QHD' WHEN 7 THEN 'UHD' ELSE 'UHD_8K' END,
           CASE TRUNC(DBMS_RANDOM.VALUE(0, 6))
               WHEN 0 THEN 'ROMANIAN' WHEN 1 THEN 'ENGLISH' WHEN 2 THEN 'GERMAN'
               WHEN 3 THEN 'RUSSIAN' WHEN 4 THEN 'SPANISH' ELSE 'OTHER' END,
           CASE TRUNC(DBMS_RANDOM.VALUE(0, 5))
               WHEN 0 THEN 'THEATRICAL_CUT' WHEN 1 THEN 'DIRECTORS_CUT' WHEN 2 THEN 'EXTENDED_EDITION'
               WHEN 3 THEN 'DUBBED_VERSION' ELSE 'SUBBED_VERSION' END
       );
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare versiuni_film... GATA');

  -- ACTORI
  DBMS_OUTPUT.PUT_LINE('Inserare actori...');
FOR i IN 1..20 LOOP
    INSERT INTO actori (id, nume_scena, nume, prenume, data_nastere)
    VALUES (
      seq_actori.NEXTVAL,
      -- 'NUME_SCENA i'
      l_nume_scena(TRUNC(DBMS_RANDOM.VALUE(1, l_nume_scena.COUNT + 1))) || ' ' || i,

      l_nume(TRUNC(DBMS_RANDOM.VALUE(1, l_nume.COUNT + 1))),

      l_prenume(TRUNC(DBMS_RANDOM.VALUE(1, l_prenume.COUNT + 1))),
         -- Random birthday between 1950 and 2000
      TO_DATE('1950-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 18000)
    );
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare actori... GATA');

  -- DISTRIBUTIE
  DBMS_OUTPUT.PUT_LINE('Inserare distributie...');
FOR f IN (SELECT id FROM filme) LOOP
    -- INSERT 3 RANDOM ACTORS FOR EACH FILM
    FOR i IN 1..3 LOOP
BEGIN
-- SELECT RANDOM ACTOR ID
SELECT id INTO v_id_1 FROM actori ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO distributie (id_film, id_actor, rol, comentariu)
-- TODO: 'COMENTARIU' IS FIXED, A NEW TABLE SHOULD BE CREATED IN THE FUTURE.
VALUES (f.id, v_id_1, l_roluri(TRUNC(DBMS_RANDOM.VALUE(1, l_roluri.COUNT + 1))), 'Interpretare deosebită');
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare distributie... GATA');

  -- CLIENTI
  DBMS_OUTPUT.PUT_LINE('Inserare clienti...');
FOR i IN 1..20 LOOP
    INSERT INTO clienti (id, nume, prenume, telefon_fix_cod, telefon_fix_nr, adresa, oras, email, telefon_mobil_cod, telefon_mobil_nr, data_nastere)
    VALUES (
      seq_clienti.NEXTVAL,
      l_nume(TRUNC(DBMS_RANDOM.VALUE(1, l_nume.COUNT + 1))),
      l_prenume(TRUNC(DBMS_RANDOM.VALUE(1, l_prenume.COUNT + 1))),
      '021',
      TRUNC(DBMS_RANDOM.VALUE(1000000, 9999999)),
      -- TODO: NEW LOCAL VARIABLE FOR STREET NAMES SHOULD BE CREATED IN THE FUTURE
      'Strada Florilor Nr. ' || i,
      l_orase(TRUNC(DBMS_RANDOM.VALUE(1, l_orase.COUNT + 1))),
      -- TODO: THIS EMAIL GENERATION IS VERY BASIC, IT CAN BE IMPROVED IN THE FUTURE
      -- EXEMPLE: 'prenume.nume' || DBMS_RANDOM.VALUE( ... ) || '@email.ro'
      'client' || i || '@email.ro',
      '07' || TRUNC(DBMS_RANDOM.VALUE(20, 99)),
      TRUNC(DBMS_RANDOM.VALUE(1000000, 9999999)),
      TO_DATE('1970-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 15000)
    );
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare clienti... GATA');

  -- ETICHETE
  DBMS_OUTPUT.PUT_LINE('Inserare etichete...');
FOR i IN 1 .. l_etichete.COUNT LOOP
      INSERT INTO etichete (id, denumire, sentiment)
      VALUES (seq_etichete.NEXTVAL, l_etichete(i).denumire, l_etichete(i).sentiment);
END LOOP;
DBMS_OUTPUT.PUT_LINE('Inserare etichete... GATA');

  -- VIZUALIZARI
  DBMS_OUTPUT.PUT_LINE('Inserare vizualizari...');
FOR i IN 1..30 LOOP
SELECT id INTO v_id_1 FROM clienti ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
SELECT id INTO v_id_2 FROM versiuni_film ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO vizualizari (id, id_client, id_versiune, data_vizualizare, durata, stare)
VALUES (
           seq_vizualizari.NEXTVAL,
           v_id_1,
           v_id_2,
           CURRENT_DATE - DBMS_RANDOM.VALUE(1, 100),
           DBMS_RANDOM.VALUE(1, 180),
           l_stari(TRUNC(DBMS_RANDOM.VALUE(1, l_stari.COUNT + 1)))
       );
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare vizualizari... GATA');

  -- RECENZII
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii...');
FOR i IN 1..30 LOOP
BEGIN
SELECT id INTO v_id_1 FROM clienti ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
SELECT id INTO v_id_2 FROM filme ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO recenzii (id, id_client, id_film, nota, sentiment, text_comentariu)
VALUES (
           seq_recenzii.NEXTVAL,
           v_id_1,
           v_id_2,
           TRUNC(DBMS_RANDOM.VALUE(1, 11)),
           CASE TRUNC(DBMS_RANDOM.VALUE(0, 3)) WHEN 0 THEN 'POZITIV' WHEN 1 THEN 'NEGATIV' ELSE 'NEUTRU' END,
            -- TODO: NEED TO MAKE LOCAL VARIABLE FOR COMMENT TEXTS
           'O experiență cinematografică interesantă.'
       );
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii... GATA');

  -- RECENZII_ETICHETE
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii_etichete...');
FOR r IN (SELECT id FROM recenzii) LOOP
    FOR i IN 1..2 LOOP
BEGIN
SELECT id INTO v_id_1 FROM etichete ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 1 ROW ONLY;
INSERT INTO recenzii_etichete (id_recenzie, id_eticheta) VALUES (r.id, v_id_1);
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii_etichete... GATA');

  -- RECENZII_ACTORI
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii_actori...');
FOR r IN (SELECT id, id_film FROM recenzii) LOOP
    FOR a IN (SELECT id_actor FROM distributie WHERE id_film = r.id_film FETCH FIRST 2 ROWS ONLY) LOOP
BEGIN
INSERT INTO recenzii_actori (id_recenzie, id_actor, comentariu)
VALUES (r.id, a.id_actor, 'Actorul s-a descurcat excelent în acest rol.');
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
END LOOP;
  DBMS_OUTPUT.PUT_LINE('Inserare recenzii_actori... GATA');

COMMIT;
END;
/