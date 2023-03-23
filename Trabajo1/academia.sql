DROP TABLE karateca;

CREATE TABLE karateca(
pasaporte NUMBER(20) PRIMARY KEY,
datoka XMLTYPE);

DROP TABLE evento;

CREATE TABLE evento(
code NUMBER(8) PRIMARY KEY,
datoev XMLTYPE);

-----------------------------------------------------------
CREATE OR REPLACE TRIGGER validar_evento
BEFORE INSERT ON evento
FOR EACH ROW
DECLARE
    peleas_count NUMBER := 0;
    pasaporte1 NUMBER;
    pasaporte2 NUMBER;
BEGIN
    FOR p IN (
        SELECT EXTRACTVALUE(VALUE(p), '/Pelea/Pas1') pas1,
               EXTRACTVALUE(VALUE(p), '/Pelea/Pas2') pas2,
               EXTRACTVALUE(VALUE(p), '/Pelea/Ganador') ganador,
               EXTRACTVALUE(VALUE(p), '/Pelea/Tecnica') tecnica
        FROM TABLE(XMLSEQUENCE(EXTRACT(:new.datoev, '/Evento/Peleas/Pelea'))) p
    ) LOOP
        -- Control de existencia de karatecas
        SELECT pasaporte INTO pasaporte1 FROM karateca WHERE pasaporte = p.pas1;
        IF pasaporte1 IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002, 'No existe el karateca con pasaporte ' || p.pas1 || '.');
        END IF;
        
        SELECT pasaporte INTO pasaporte2 FROM karateca WHERE pasaporte = p.pas2;
        IF pasaporte2 IS NULL THEN
            RAISE_APPLICATION_ERROR(-20002, 'No existe el karateca con pasaporte ' || p.pas2 || '.');
        END IF;

        -- Control de cantidad de peleas
        peleas_count := peleas_count + 1;
        IF peleas_count > 5 THEN
            RAISE_APPLICATION_ERROR(-20001, 'El evento no puede tener mas de 5 peleas.');
        END IF;

        -- Control de peleas validas
        IF p.ganador NOT IN ('0', '1', '2') THEN
            RAISE_APPLICATION_ERROR(-20006, 'El valor del campo ganador debe ser 0, 1 o 2.');
        END IF;

        IF p.pas1 = p.pas2 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Un karateca no puede pelear consigo mismo.');
        END IF;
        
        IF p.ganador = '0' THEN
            IF p.tecnica IS NOT NULL THEN
                RAISE_APPLICATION_ERROR(-20004, 'No puede haber técnica en una pelea empatada.');
            END IF;
        ELSE
            IF p.tecnica IS NULL THEN
                RAISE_APPLICATION_ERROR(-20005, 'Debe haber técnica en una pelea ganada.');
            END IF;
        END IF;
        
        IF p.ganador NOT IN ('0', '1', '2') THEN
            RAISE_APPLICATION_ERROR(-20006, 'El valor del campo ganador debe ser 0, 1 o 2.');
        END IF;
    END LOOP;
    
    -- Control de cantidad minima de peleas
    IF peleas_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'El evento debe tener al menos una pelea.');
    END IF;
END;
----------------------------------------------------------------------

CREATE TABLE peleador (
    pasaporte NUMBER(20) PRIMARY KEY,
    datope CLOB NOT NULL
);
-- Crear tabla KaratecaPeleador
CREATE TABLE KaratecaPeleador (
  pasaporte NUMBER(20) PRIMARY KEY,
  nom VARCHAR2(50),
  otronom VARCHAR2(50),
  nick VARCHAR2(50),
  otronick VARCHAR2(50)
);

-- Crear tabla Pelea
CREATE TABLE Pelea (
  consecutivo NUMBER(8) PRIMARY KEY,
  pas1 NUMBER(20),
  pas2 NUMBER(20),
  fecha DATE,
  ganador NUMBER(1),
  tecnica VARCHAR2(50),
  Evento VARCHAR2(50),
  CONSTRAINT fk_pas1 FOREIGN KEY (pas1) REFERENCES KaratecaPeleador(pasaporte),
  CONSTRAINT fk_pas2 FOREIGN KEY (pas2) REFERENCES KaratecaPeleador(pasaporte)
);

-- Insertar datos en la tabla KaratecaPeleador
DECLARE
  v_pasaporte NUMBER(20);
  v_nom VARCHAR2(50);
  v_otronom VARCHAR2(50);
  v_nick VARCHAR2(50);
  v_otronick VARCHAR2(50);
BEGIN
  FOR cur IN (
    SELECT pasaporte, datoka, datope FROM (
    SELECT pasaporte, TO_CHAR(datoka) as datoka, TO_CHAR(datope) as datope FROM karateca
    UNION
    SELECT pasaporte, TO_CHAR(datoev) as datoka, NULL as datope FROM evento
    UNION
    SELECT pasaporte, NULL as datoka, datope FROM peleador
    )
  ) LOOP
    v_pasaporte := cur.pasaporte;
    
    -- Obtener nombre y nickname de la persona de la tabla XML
    IF cur.datoka IS NOT NULL THEN
      SELECT EXTRACTVALUE(cur.datoka, '/Karateca/Nombre') INTO v_nom FROM DUAL;
      SELECT EXTRACTVALUE(cur.datoka, '/Karateca/Nickname') INTO v_nick FROM DUAL;
    END IF;
    
    -- Obtener nombre y nickname de la persona de la tabla JSON
    IF cur.datope IS NOT NULL THEN
      SELECT JSON_VALUE(cur.datope, '$.nombre') INTO v_otronom FROM DUAL;
      SELECT JSON_VALUE(cur.datope, '$.nickname') INTO v_otronick FROM DUAL;
    END IF;
    
    -- Insertar en la tabla KaratecaPeleador
    INSERT INTO KaratecaPeleador (pasaporte, nom, otronom, nick, otronick)
    VALUES (v_pasaporte, COALESCE(v_nom, v_otronom), CASE WHEN v_nom <> v_otronom THEN v_nom END, COALESCE(v_nick, v_otronick), CASE WHEN v_nick <> v_otronick THEN v_nick END);
  END LOOP;
END;

-- Insertar datos en la tabla Pelea
DECLARE
  v_consecutivo NUMBER(8);
  v_pas1 NUMBER(20);
  v_pas2 NUMBER(20);
  v_fecha DATE;
  v_ganador NUMBER(1);
  v_tecnica VARCHAR2(50);
  v_Evento VARCHAR2(50);
BEGIN
FOR cur IN ( 
    SELECT ROW_NUMBER() OVER (ORDER BY EXTRACTVALUE(value(t), 'Pas1')) AS consecutivo,
    EXTRACTVALUE(value(t), 'Pas1') AS pas1,
    EXTRACTVALUE(value(t), 'Pas2') AS pas2,
    TO_DATE(EXTRACTVALUE(value(t), '../../Fecha'), 'DD/MM/YYYY') AS fecha,
    TO_NUMBER(EXTRACTVALUE(value(t), 'Ganador')) AS ganador,
    EXTRACTVALUE(value(t), 'Tecnica') AS tecnica,
    'Kick that B88ch' AS Evento
    FROM evento e, TABLE(XMLSequence(EXTRACT(e.datoev, '/Evento/Peleas/Pelea'))) t
) LOOP
  v_consecutivo := cur.consecutivo;
  v_pas1 := cur.pas1;
  v_pas2 := cur.pas2;
  v_fecha := cur.fecha;
  v_ganador := cur.ganador;
  v_tecnica := cur.tecnica;
  v_Evento := cur.Evento;
  -- Insertar datos en la tabla Pelea
  INSERT INTO Pelea (consecutivo, pas1, pas2, fecha, ganador, tecnica, Evento)
  VALUES (v_consecutivo, v_pas1, v_pas2, v_fecha, v_ganador, v_tecnica, v_Evento);
END LOOP;
END;