-- Generated by Oracle SQL Developer Data Modeler 3.3.0.747
--   at:        2013-12-05 16:49:39 WET
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g




CREATE TABLE Compra
  (
    compra_id        NUMBER (10) NOT NULL ,
    userid           NUMBER (7) NOT NULL ,
    iid              NUMBER (7) NOT NULL ,
    num              NUMBER (6) NOT NULL ,
    maxpricepershare NUMBER (10,2) NOT NULL ,
    targetSellPrice  NUMBER (10,2) NOT NULL
  ) ;
ALTER TABLE Compra ADD CONSTRAINT Compra_PK PRIMARY KEY
(
  compra_id
)
;

CREATE TABLE HallFame
  ( hfid NUMBER (7) NOT NULL , iid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE HallFame ADD CONSTRAINT HallFame_PK PRIMARY KEY
(
  hfid
)
;

CREATE TABLE Ideia
  (
    iid             NUMBER (7) NOT NULL ,
    titulo          VARCHAR2 (80) NOT NULL ,
    descricao       VARCHAR2 (1000) NOT NULL ,
    userid          NUMBER (7) NOT NULL ,
    activa          NUMBER (1) NOT NULL ,
    path            VARCHAR2 (70) ,
    originalfile    VARCHAR2 (100) ,
    ultimatransacao NUMBER (10) ,
    facebook_id     VARCHAR2 (100)
  ) ;
ALTER TABLE Ideia ADD CONSTRAINT Ideia_PK PRIMARY KEY
(
  iid
)
;

CREATE TABLE IdeiaWatchList
  (
    userid NUMBER (7) NOT NULL ,
    iid    NUMBER (7) NOT NULL
  ) ;
ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_PK PRIMARY KEY
(
  userid, iid
)
;

CREATE TABLE "Share"
  (
    iid       NUMBER (7) NOT NULL ,
    userid    NUMBER (7) NOT NULL ,
    numshares NUMBER (6) NOT NULL ,
    valor     NUMBER (10,2) NOT NULL
  ) ;
ALTER TABLE "Share" ADD CONSTRAINT Share_PK PRIMARY KEY
(
  iid, userid, valor, numshares
)
;

CREATE TABLE Topico
  (
    tid    NUMBER (7) NOT NULL ,
    nome   VARCHAR2 (100) NOT NULL ,
    userid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE Topico ADD CONSTRAINT Topico_PK PRIMARY KEY
(
  tid
)
;

CREATE TABLE TopicoIdeia
  (
    tid NUMBER (7) NOT NULL ,
    iid NUMBER (7) NOT NULL
  ) ;
ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_PK PRIMARY KEY
(
  tid, iid
)
;

CREATE TABLE Transacao
  (
    transactionid NUMBER (7) NOT NULL ,
    comprador     NUMBER (7) NOT NULL ,
    vendedor      NUMBER (7) NOT NULL ,
    valor         NUMBER (10,2) NOT NULL ,
    numshares     NUMBER (6) NOT NULL ,
    iid           NUMBER (7) NOT NULL ,
    data          DATE NOT NULL
  ) ;
ALTER TABLE Transacao ADD CONSTRAINT Transacao_PK PRIMARY KEY
(
  transactionid
)
;

CREATE TABLE Utilizador
  (
    userid          NUMBER (7) NOT NULL ,
    email           VARCHAR2 (50) ,
    username        VARCHAR2 (20) ,
    pass            VARCHAR2 (100) ,
    dinheiro        NUMBER (10,2) NOT NULL ,
    dataregisto     DATE NOT NULL ,
    dataultimologin DATE ,
    funcao          NUMBER (1) NOT NULL ,
    id_facebook     VARCHAR2 (100)
  ) ;
ALTER TABLE Utilizador ADD CONSTRAINT Utilizador_PK PRIMARY KEY
(
  userid
)
;

ALTER TABLE Compra ADD CONSTRAINT Compra_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Compra ADD CONSTRAINT Compra_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE IdeiaWatchList ADD CONSTRAINT IdeiaWatchList_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE "Share" ADD CONSTRAINT Share_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE "Share" ADD CONSTRAINT Share_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE HallFame ADD CONSTRAINT TABLE_10_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Topico ADD CONSTRAINT TABLE_12_Utilizador_FK FOREIGN KEY ( userid ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE TopicoIdeia ADD CONSTRAINT TopicoIdeia_Topico_FK FOREIGN KEY ( tid ) REFERENCES Topico ( tid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Ideia_FK FOREIGN KEY ( iid ) REFERENCES Ideia ( iid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Utilizador_FK FOREIGN KEY ( comprador ) REFERENCES Utilizador ( userid ) ;

ALTER TABLE Transacao ADD CONSTRAINT Transacao_Utilizador_FKv2 FOREIGN KEY ( vendedor ) REFERENCES Utilizador ( userid ) ;


-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             9
-- CREATE INDEX                             0
-- ALTER TABLE                             22
-- CREATE VIEW                              0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
