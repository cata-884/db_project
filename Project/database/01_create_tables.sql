/*
sudo podman run --replace -d --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=parola \
  -e APP_USER=movie_user \
  -e APP_USER_PASSWORD=parola \
  docker.io/gvenzl/oracle-xe:21-slim

 sudo podman logs -f oracle-xe
 */

-- Secvente
CREATE SEQUENCE seq_categorii    START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_filme        START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_versiuni     START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_actori       START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_clienti      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_vizualizari  START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_etichete     START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_recenzii     START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Tabele

CREATE TABLE categorii (
    id      NUMBER DEFAULT seq_categorii.NEXTVAL PRIMARY KEY,
    nume    VARCHAR2(100) NOT NULL
);

CREATE TABLE filme (
    id              NUMBER DEFAULT seq_filme.NEXTVAL PRIMARY KEY,
    titlu           VARCHAR2(200) NOT NULL,
    descriere       VARCHAR2(4000),
    data_lansare    DATE,
    id_categorie    NUMBER REFERENCES categorii(id),
    rating          NUMBER(3, 1) DEFAULT 0 CHECK (rating BETWEEN 0 AND 10)
);

CREATE TABLE versiuni_film (
    id                  NUMBER DEFAULT seq_versiuni.NEXTVAL PRIMARY KEY,
    id_film             NUMBER NOT NULL REFERENCES filme(id),
    rezolutie           VARCHAR2(10)  CHECK (rezolutie IN ('TINY','LOW','SD','DVD','HD','FULL_HD','QHD','UHD','UHD_8K')),
    limbi               VARCHAR2(15)  CHECK (limbi     IN ('ROMANIAN','ENGLISH','GERMAN','RUSSIAN','SPANISH','OTHER')),
    format              VARCHAR2(30)  CHECK (format    IN ('THEATRICAL_CUT','DIRECTORS_CUT','EXTENDED_EDITION','DUBBED_VERSION','SUBBED_VERSION'))
);

CREATE TABLE actori (
    id              NUMBER DEFAULT seq_actori.NEXTVAL PRIMARY KEY,
    nume_scena      VARCHAR2(100),
    nume            VARCHAR2(100) NOT NULL,
    prenume         VARCHAR2(100) NOT NULL,
    data_nastere    DATE
);

-- many-to-many filme <-> actori
CREATE TABLE distributie (
    id_film         NUMBER NOT NULL REFERENCES filme(id),
    id_actor        NUMBER NOT NULL REFERENCES actori(id),
    rol             VARCHAR2(20) NOT NULL CHECK (rol IN ('PROTAGONIST','ANTAGONIST','SECUNDAR','EPISODIC','DUBLURA')),
    CONSTRAINT pk_distributie PRIMARY KEY (id_film, id_actor)
);

CREATE TABLE clienti (
    id                  NUMBER DEFAULT seq_clienti.NEXTVAL PRIMARY KEY,
    nume                VARCHAR2(100) NOT NULL,
    prenume             VARCHAR2(100) NOT NULL,
    telefon_fix_cod     VARCHAR2(10),
    telefon_fix_nr      VARCHAR2(20),
    adresa              VARCHAR2(300),
    oras                VARCHAR2(100),
    email               VARCHAR2(200),
    telefon_mobil_cod   VARCHAR2(10),
    telefon_mobil_nr    VARCHAR2(20),
    data_nastere        DATE
);

CREATE TABLE vizualizari (
    id                  NUMBER DEFAULT seq_vizualizari.NEXTVAL PRIMARY KEY,
    id_client           NUMBER NOT NULL REFERENCES clienti(id),
    id_versiune         NUMBER NOT NULL REFERENCES versiuni_film(id),
    data_vizualizare    DATE,
    durata              NUMBER(6, 2),
    stare               VARCHAR2(20) CHECK (stare IN ('IN_PROGRESS','COMPLETED','PAUSED','ABANDONED'))
);

CREATE TABLE etichete (
    id          NUMBER DEFAULT seq_etichete.NEXTVAL PRIMARY KEY,
    denumire    VARCHAR2(50)  NOT NULL UNIQUE,
    sentiment   VARCHAR2(10)  NOT NULL CHECK (sentiment IN ('POZITIV','NEGATIV','NEUTRU'))
);

CREATE TABLE recenzii (
    id                  NUMBER DEFAULT seq_recenzii.NEXTVAL PRIMARY KEY,
    id_client           NUMBER NOT NULL REFERENCES clienti(id),
    id_film             NUMBER NOT NULL REFERENCES filme(id),
    nota                NUMBER(2)     CHECK (nota BETWEEN 1 AND 10),
    sentiment           VARCHAR2(10)  CHECK (sentiment IN ('POZITIV','NEGATIV','NEUTRU')),
    text_comentariu     VARCHAR2(4000),
    data_postare        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_recenzie_client_film UNIQUE (id_client, id_film)
);

-- many-to-many recenzii <-> etichete
CREATE TABLE recenzii_etichete (
    id_recenzie     NUMBER NOT NULL REFERENCES recenzii(id),
    id_eticheta     NUMBER NOT NULL REFERENCES etichete(id),
    CONSTRAINT pk_recenzii_etichete PRIMARY KEY (id_recenzie, id_eticheta)
);

-- comentariu per actor per recenzie
CREATE TABLE recenzii_actori (
    id_recenzie     NUMBER NOT NULL REFERENCES recenzii(id),
    id_actor        NUMBER NOT NULL REFERENCES actori(id),
    comentariu      VARCHAR2(1000),
    CONSTRAINT pk_recenzii_actori PRIMARY KEY (id_recenzie, id_actor)
);

-- Tabela temporara folosita de p_grupare_clienti pentru a returna rezultatele
-- prin refcursor. ON COMMIT PRESERVE ROWS pastreaza datele pana la sfarsitul sesiunii.
CREATE GLOBAL TEMPORARY TABLE tmp_grupare_clienti (
    id_client NUMBER NOT NULL,
    id_grupa  NUMBER NOT NULL
) ON COMMIT PRESERVE ROWS;

CREATE TABLE sesiuni (
    token       VARCHAR2(64)  PRIMARY KEY,
    id_client   NUMBER        NOT NULL,
    creata_la   TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    expira_la   TIMESTAMP     NOT NULL,
    CONSTRAINT fk_sesiuni_client FOREIGN KEY (id_client)
        REFERENCES clienti(id) ON DELETE CASCADE
);

CREATE INDEX idx_sesiuni_expira ON sesiuni(expira_la);
CREATE INDEX idx_sesiuni_client ON sesiuni(id_client);
