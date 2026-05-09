SET SERVEROUTPUT ON;

DECLARE

    TYPE varr IS VARRAY(1000) OF VARCHAR2(500);

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

    l_f_titlu varr := varr(
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
            'Taximetriști',
            'Hârtia va fi albastră',
            'Portretul luptătorului la tinerețe',
            'Unde dai și unde crapă',
            'Marfa și banii',
            'Amintiri din epoca de aur'
                      );

    l_f_desc varr := varr(
            'Povestea tragică a unui bătrân lăsat fără ajutor într-un sistem medical indiferent.',
            'O comedie neagră despre un profesor sărac care pune la cale o combinație ingenioasă.',
            'Un tânăr provincial se confruntă cu absurdul vieții urbane.',
            'Satira vieții corporatiste și a team-building-urilor forțate.',
            'Doi prieteni pornesc într-o aventură după ce câștigă la loterie.',
            'O incursiune în trecutul sângeros al Țării Românești prin ochii unui zapciu.',
            'Un tată confruntat cu alegeri morale dificile pentru viitorul fiicei sale.',
            'O mamă bogată încearcă să-și salveze fiul după un accident tragic.',
            'O nuntă celebrată în secret în timpul regimului comunist.',
            'În spatele realizării primului film românesc de mare succes se ascund intrigi pasionale.',
            'Trei personaje cu destine diferite se intersectează într-o suburbie bucureșteană.',
            'O poveste cutremurătoare despre prietenie și sacrificiu în comunism.',
            'O reuniune de familie după o înmormântare dezvăluie tensiuni și secrete.',
            'Un thriller psihologic despre violența din mediul rural.',
            'Doi taximetriști discută despre viață și probleme sociale.',
            'O dramă istorică despre Revoluția din 1989 văzută prin ochii unui soldat.',
            'Un regizor își petrece tinerețea în clandestinitate, luptând împotriva regimului.',
            'Un spectacol de comedie cu un scenariu de film absurd.',
            'Povestea unui tânăr care se confruntă cu lumea interlopă a traficului de marfă.',
            'O serie de legende urbane amuzante din perioada comunistă.'
                     );

    l_f_cat varr := varr(
            'Dramă',
            'Comedie',
            'Comedie',
            'Comedie',
            'Comedie',
            'Istoric',
            'Dramă',
            'Dramă',
            'Comedie',
            'Istoric',
            'Dramă',
            'Dramă',
            'Dramă',
            'Thriller',
            'Dramă',
            'Dramă',
            'Dramă',
            'Comedie',
            'Thriller',
            'Comedie'
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
    l_orase  varr := varr('București', 'Cluj-Napoca', 'Iași', 'Timișoara', 'Constanța', 'Brașov', 'Craiova', 'Galați');
    l_stari  varr := varr('IN_PROGRESS', 'COMPLETED', 'PAUSED', 'ABANDONED');

    l_et_den varr := varr(
            'MI_A_PLACUT',
            'NU_MI_A_PLACUT',
            'EMOTIONANT',
            'AMUZANT',
            'INSPAIMANTATOR',
            'RELAXANT',
            'PLICTISITOR',
            'SURPRINZATOR',
            'AS_RECOMANDA',
            'NU_AS_RECOMANDA',
            'AS_MAI_VIZIONA',
            'SCENARIU_BUN',
            'SCENARIU_SLAB',
            'ACTOR_PRINCIPAL_APRECIAT',
            'REGIE_EXCELENTA',
            'COLOANA_SONORA_BUNA',
            'EFECTE_VIZUALE_IMPRESIONANTE',
            'BUN_PENTRU_FAMILIE',
            'BUN_PENTRU_COPII',
            'PENTRU_ADULTI'
                     );

    l_et_sent varr := varr(
            'POZITIV',
            'NEGATIV',
            'POZITIV',
            'POZITIV',
            'NEUTRU',
            'POZITIV',
            'NEGATIV',
            'NEUTRU',
            'POZITIV',
            'NEGATIV',
            'POZITIV',
            'POZITIV',
            'NEGATIV',
            'POZITIV',
            'POZITIV',
            'POZITIV',
            'POZITIV',
            'NEUTRU',
            'NEUTRU',
            'NEUTRU'
                      );

    l_com_poz varr := varr(
            'Un film deosebit, m-a impresionat profund!',
            'Excelent! Recomand cu căldură.',
            'O capodoperă cinematografică.',
            'Mi-a plăcut foarte mult, actoria a fost superbă.',
            'Un film memorabil, cu un mesaj puternic.'
                      );
    l_com_neg varr := varr(
            'Nu mi-a plăcut deloc, a fost o pierdere de timp.',
            'Dezamăgitor, mă așteptam la mai mult.',
            'Scenariul slab și interpretările mediocre.',
            'Plictisitor și fără sens.',
            'Nu recomand, este sub orice critică.'
                      );
    l_com_neutru varr := varr(
            'Un film mediocru, nici prea-prea, nici foarte-foarte.',
            'Acceptabil, dar nu m-a dat pe spate.',
            'Un film obișnuit, care se uită și se uită.',
            'A fost ok, dar nu m-a impresionat.',
            'Un film de duzină, nimic special.'
                         );

    l_com_actor varr := varr(
            'Actorul a dat o interpretare remarcabilă.',
            'Prestație actoricească de excepție.',
            'S-a achitat onorabil de rol.',
            'Un rol bine jucat, cu emoție.',
            'A adus profunzime personajului.',
            'Performanța actoricească a fost convingătoare.',
            'Actorul a strălucit în acest rol.',
            'O interpretare solidă și credibilă.'
                        );

    l_strazi varr := varr(
            'Strada Unirii',
            'Bulevardul Carol I',
            'Strada Lalelelor',
            'Calea Victoriei',
            'Strada Mihai Eminescu',
            'Șoseaua Panduri',
            'Strada Știrbei Vodă',
            'Aleea Parcului'
                     );

    v_id_1       NUMBER;
    v_id_2       NUMBER;
    v_temp_id    NUMBER;
    v_film_id    NUMBER;
    v_idx        NUMBER;
    v_titlu      VARCHAR2(200);
    v_desc_val   VARCHAR2(500);
    v_cat_nume   VARCHAR2(100);
    v_scena      VARCHAR2(100);
    v_nume_val   VARCHAR2(100);
    v_prenume_val VARCHAR2(100);
    v_rol        VARCHAR2(20);
    v_oras_val   VARCHAR2(100);
    v_stare_val  VARCHAR2(20);
    v_et_den_val VARCHAR2(50);
    v_et_sent_val VARCHAR2(10);
    v_sentiment  VARCHAR2(10);
    v_comentariu VARCHAR2(500);
    v_strada     VARCHAR2(200);

BEGIN

    -- CATEGORII
    FOR i IN 1..l_cat.COUNT LOOP
            v_titlu := l_cat(i);
            INSERT INTO categorii (id, nume)
            VALUES (seq_categorii.NEXTVAL, v_titlu);
        END LOOP;
    FOR i IN 1..20 LOOP
            v_cat_nume := l_f_cat(i);
            SELECT id INTO v_id_1 FROM (SELECT id FROM categorii WHERE nume = v_cat_nume) FETCH FIRST 1 ROW ONLY;

            v_titlu    := l_f_titlu(i);
            v_desc_val := l_f_desc(i);

            INSERT INTO filme (id, titlu, descriere, data_lansare, id_categorie, rating)
            VALUES (
                       seq_filme.NEXTVAL,
                       v_titlu,
                       v_desc_val,
                       TO_DATE('2000-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 8000),
                       v_id_1,
                       NULL
                   );
        END LOOP;

    FOR i IN 1..25 LOOP
            SELECT id INTO v_id_1 FROM (SELECT id FROM filme ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
            INSERT INTO versiuni_film (id, id_film, rezolutie, limbi, format)
            VALUES (
                       seq_versiuni.NEXTVAL,
                       v_id_1,
                       DECODE(TRUNC(DBMS_RANDOM.VALUE(0, 9)), 0, 'TINY', 1, 'LOW', 2, 'SD', 3, 'DVD', 4, 'HD', 5,
                              'FULL_HD', 6, 'QHD', 7, 'UHD', 'UHD_8K'),
                       DECODE(TRUNC(DBMS_RANDOM.VALUE(0, 6)), 0, 'ROMANIAN', 1, 'ENGLISH', 2, 'GERMAN', 3, 'RUSSIAN', 4,
                              'SPANISH', 'OTHER'),
                       DECODE(TRUNC(DBMS_RANDOM.VALUE(0, 5)), 0, 'THEATRICAL_CUT', 1, 'DIRECTORS_CUT', 2,
                              'EXTENDED_EDITION', 3, 'DUBBED_VERSION', 'SUBBED_VERSION')
                   );
        END LOOP;

    -- ACTORI
    FOR i IN 1..20 LOOP
            v_idx         := TRUNC(DBMS_RANDOM.VALUE(1, l_nume_scena.COUNT + 1));
            v_temp_id     := TRUNC(DBMS_RANDOM.VALUE(1, l_nume.COUNT + 1));
            v_film_id     := TRUNC(DBMS_RANDOM.VALUE(1, l_prenume.COUNT + 1));
            v_scena       := l_nume_scena(v_idx) || ' ' || i;
            v_nume_val    := l_nume(v_temp_id);
            v_prenume_val := l_prenume(v_film_id);
            INSERT INTO actori (id, nume_scena, nume, prenume, data_nastere)
            VALUES (
                       seq_actori.NEXTVAL,
                       v_scena,
                       v_nume_val,
                       v_prenume_val,
                       TO_DATE('1950-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 18000)
                   );
        END LOOP;

    -- DISTRIBUTIE
    FOR f IN (SELECT id FROM filme) LOOP
            FOR i IN 1..3 LOOP
                    BEGIN
                        SELECT id INTO v_id_1 FROM (SELECT id FROM actori ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
                        v_idx := TRUNC(DBMS_RANDOM.VALUE(1, l_roluri.COUNT + 1));
                        v_rol := l_roluri(v_idx);
                        INSERT INTO distributie (id_film, id_actor, rol)
                        VALUES (f.id, v_id_1, v_rol);
                    EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
                    END;
                END LOOP;
        END LOOP;

    -- CLIENTI
    FOR i IN 1..20 LOOP
            v_idx         := TRUNC(DBMS_RANDOM.VALUE(1, l_nume.COUNT + 1));
            v_temp_id     := TRUNC(DBMS_RANDOM.VALUE(1, l_prenume.COUNT + 1));
            v_film_id     := TRUNC(DBMS_RANDOM.VALUE(1, l_orase.COUNT + 1));
            v_nume_val    := l_nume(v_idx);
            v_prenume_val := l_prenume(v_temp_id);
            v_oras_val    := l_orase(v_film_id);

            v_idx     := TRUNC(DBMS_RANDOM.VALUE(1, l_strazi.COUNT + 1));
            v_strada  := l_strazi(v_idx);

            INSERT INTO clienti (id, nume, prenume, telefon_fix_cod, telefon_fix_nr, adresa, oras, email, telefon_mobil_cod, telefon_mobil_nr, data_nastere)
            VALUES (
                       seq_clienti.NEXTVAL,
                       v_nume_val,
                       v_prenume_val,
                       '021',
                       TRUNC(DBMS_RANDOM.VALUE(1000000, 9999999)),
                       v_strada || ' Nr. ' || TRUNC(DBMS_RANDOM.VALUE(1, 99)),
                       v_oras_val,
                       LOWER(v_prenume_val || '.' || v_nume_val || ROUND(DBMS_RANDOM.VALUE(10,99))) || '@email.ro',
                       '07' || TRUNC(DBMS_RANDOM.VALUE(20, 99)),
                       TRUNC(DBMS_RANDOM.VALUE(1000000, 9999999)),
                       TO_DATE('1970-01-01', 'YYYY-MM-DD') + DBMS_RANDOM.VALUE(0, 15000)
                   );
        END LOOP;

    -- ETICHETE
    FOR i IN 1..l_et_den.COUNT LOOP
            v_et_den_val  := l_et_den(i);
            v_et_sent_val := l_et_sent(i);
            INSERT INTO etichete (id, denumire, sentiment)
            VALUES (seq_etichete.NEXTVAL, v_et_den_val, v_et_sent_val);
        END LOOP;

    -- VIZUALIZARI
    FOR i IN 1..30 LOOP
            SELECT id INTO v_id_1 FROM (SELECT id FROM clienti      ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
            SELECT id INTO v_id_2 FROM (SELECT id FROM versiuni_film ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
            v_idx       := TRUNC(DBMS_RANDOM.VALUE(1, l_stari.COUNT + 1));
            v_stare_val := l_stari(v_idx);
            INSERT INTO vizualizari (id, id_client, id_versiune, data_vizualizare, durata, stare)
            VALUES (
                       seq_vizualizari.NEXTVAL,
                       v_id_1,
                       v_id_2,
                       CURRENT_DATE - DBMS_RANDOM.VALUE(1, 100),
                       DBMS_RANDOM.VALUE(1, 180),
                       v_stare_val
                   );
        END LOOP;

    -- RECENZII
    FOR i IN 1..30 LOOP
            BEGIN
                SELECT id INTO v_id_1 FROM (SELECT id FROM clienti ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
                SELECT id INTO v_id_2 FROM (SELECT id FROM filme    ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;

                v_idx := TRUNC(DBMS_RANDOM.VALUE(0, 3));
                IF v_idx = 0 THEN
                    v_sentiment := 'POZITIV';
                    v_idx := TRUNC(DBMS_RANDOM.VALUE(1, l_com_poz.COUNT + 1));
                    v_comentariu := l_com_poz(v_idx);
                ELSIF v_idx = 1 THEN
                    v_sentiment := 'NEGATIV';
                    v_idx := TRUNC(DBMS_RANDOM.VALUE(1, l_com_neg.COUNT + 1));
                    v_comentariu := l_com_neg(v_idx);
                ELSE
                    v_sentiment := 'NEUTRU';
                    v_idx := TRUNC(DBMS_RANDOM.VALUE(1, l_com_neutru.COUNT + 1));
                    v_comentariu := l_com_neutru(v_idx);
                END IF;

                INSERT INTO recenzii (id, id_client, id_film, nota, sentiment, text_comentariu)
                VALUES (
                           seq_recenzii.NEXTVAL,
                           v_id_1,
                           v_id_2,
                           TRUNC(DBMS_RANDOM.VALUE(1, 11)),
                           v_sentiment,
                           v_comentariu
                       );
            EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
            END;
        END LOOP;

    -- RECENZII_ETICHETE
    FOR r IN (SELECT id FROM recenzii) LOOP
            FOR i IN 1..2 LOOP
                    BEGIN
                        SELECT id INTO v_id_1 FROM (SELECT id FROM etichete ORDER BY DBMS_RANDOM.VALUE()) FETCH FIRST 1 ROW ONLY;
                        INSERT INTO recenzii_etichete (id_recenzie, id_eticheta) VALUES (r.id, v_id_1);
                    EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
                    END;
                END LOOP;
        END LOOP;
    -- RECENZII_ACTORI
    FOR r IN (SELECT id, id_film FROM recenzii) LOOP
            FOR a IN (SELECT id_actor FROM distributie WHERE id_film = r.id_film FETCH FIRST 2 ROWS ONLY) LOOP
                    BEGIN
                        v_idx := TRUNC(DBMS_RANDOM.VALUE(1, l_com_actor.COUNT + 1));
                        v_comentariu := l_com_actor(v_idx);
                        INSERT INTO recenzii_actori (id_recenzie, id_actor, comentariu)
                        VALUES (r.id, a.id_actor, v_comentariu);
                    EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
                    END;
                END LOOP;
        END LOOP;

    COMMIT;
END;
/