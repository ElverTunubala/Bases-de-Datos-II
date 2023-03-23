INSERT INTO karateca VALUES
(55, XMLTYPE('<Karateca>
                <Nombre>Paula Abdul</Nombre>
                <Nickname>Knocked Out</Nickname>
             </Karateca>'));
             
INSERT INTO karateca VALUES
(66, XMLTYPE('<Karateca>
                <Nombre>Rhian Teasdale</Nombre>
                <Nickname>Wet Leg</Nickname>
             </Karateca>'));
INSERT INTO karateca VALUES
(29, XMLTYPE('<Karateca>
                <Nombre>Adriana Grande</Nombre>
                <Nickname>Mor</Nickname>
             </Karateca>'));
             
INSERT INTO karateca VALUES
(10, XMLTYPE('<Karateca>
                <Nombre>Adriana Pequeña</Nombre>
                <Nickname>More</Nickname>
             </Karateca>'));
INSERT INTO karateca VALUES
(11, XMLTYPE('<Karateca>
                <Nombre>Laura Pequeña</Nombre>
                <Nickname>Lau</Nickname>
             </Karateca>'));
--------------------------------------------------------------------
INSERT INTO evento VALUES
(990, XMLTYPE('<Evento>
               <Fecha>30/01/2023</Fecha>
               <Nombre>Kick that B88ch</Nombre>
               <Peleas>
                   <Pelea>
                      <Pas1>55</Pas1>
                      <Pas2>66</Pas2>
                      <Ganador>0</Ganador>
                   </Pelea>
                   <Pelea>
                      <Pas1>55</Pas1>
                      <Pas2>29</Pas2>
                      <Ganador>1</Ganador>
                      <Tecnica>Mataleon Fulminante</Tecnica>
                   </Pelea>
               </Peleas>
              </Evento>'));
-------------------------------------------------------------------
INSERT INTO peleador VALUES(55,
'{
    "nombre":"Paula Adul",
    "nickname":"Knocked out",
    "peleas":[
        {
            "fecha":"25/01/2023",
            "pasrival":500,
            "ganador":0
        },
        {
            "fecha":"26/01/2023",
            "pasrival":666,
            "ganador":1,
            "tecnica":"Llave del dragon"
        }
    ]
}'
);

INSERT INTO peleador VALUES(500,
'{
    "nombre":"Cathy Dennis",
    "nickname":"Touch me",
    "peleas":[
        {
            "fecha":"25/01/2023",
            "pasrival":55,
            "ganador":0
        }
    ]
}'
);
INSERT INTO peleador VALUES(666,
'{
    "nombre":"Charli XCX",
    "nickname":"Boom Clap",
    "peleas":[
        {
            "fecha":"26/01/2023",
            "pasrival":55,
            "ganador":2,
            "tecnica":"Llave del dragon"
        }
    ]
}'
);
