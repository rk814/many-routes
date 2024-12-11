INSERT INTO challenge (id, name, description, status, version)
VALUES ('387a6729-8222-4d2d-92f8-2ac88552c25a',
        'Krakowskie Kopce',
        'Kraków jest miastem słynącym z czterech kopców, które pełnią zarówno funkcje historyczne, jak i symboliczne. Najstarszy z nich to Kopiec Krakusa, legendarny grób założyciela miasta, sięgający czasów przedchrześcijańskich. Kopiec Wandy upamiętnia mityczną córkę Krakusa, która według legendy popełniła samobójstwo, by uniknąć małżeństwa z niechcianym mężem. Dwa pozostałe kopce, Kościuszki i Piłsudskiego, zostały usypane na cześć wielkich polskich bohaterów narodowych, Tadeusza Kościuszki i Józefa Piłsudskiego, stanowiąc wyraz patriotyzmu i hołd dla ich dokonań.',
        'ACTIVE',
        0);

INSERT INTO summit (id, name, height, score, status, version, coords_latitude, coords_longitude)
VALUES ('689b2e7f-0593-4f8f-aaba-f26d0029a0cf',
        'Kopiec Kościuszki',
        330,
        1,
        'ACTIVE',
        0,
        50.054922,
        19.893358);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('387a6729-8222-4d2d-92f8-2ac88552c25a', '689b2e7f-0593-4f8f-aaba-f26d0029a0cf');

INSERT INTO summit (id, name, height, score, status, version, coords_latitude, coords_longitude)
VALUES ('12979284-9479-48e4-977b-5d6754fd95d4',
        'Kopiec Krakusa',
        271,
        2,
        'ACTIVE',
        0,
        50.038056,
        19.958333);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('387a6729-8222-4d2d-92f8-2ac88552c25a', '12979284-9479-48e4-977b-5d6754fd95d4');

INSERT INTO summit (id, name, height, score, status, version,
                    coords_latitude, coords_longitude)
VALUES ('850a4789-d27b-4e8f-b814-3a54408c5581',
        'Kopiec Piłsudskiego',
        393,
        3,
        'ACTIVE',
        0,
        50.06,
        19.847222);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('387a6729-8222-4d2d-92f8-2ac88552c25a', '850a4789-d27b-4e8f-b814-3a54408c5581');

INSERT INTO summit (id, name, height, score, status, version,
                    coords_latitude, coords_longitude)
VALUES ('cf771534-e5e2-4e58-800b-f2fa965ef5a2',
        'Kopiec Wandy',
        238,
        4,
        'ACTIVE',
        0,
        50.070278,
        20.068056);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('387a6729-8222-4d2d-92f8-2ac88552c25a', 'cf771534-e5e2-4e58-800b-f2fa965ef5a2');
