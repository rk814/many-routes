INSERT INTO actor(id, login, email, hash_password, name, role)
VALUES ('5c39c496-ff63-4c8a-bad4-47d6a97053e7',
        'adam_wanderlust',
        'adam@adventureworld.com',
        '$2a$10$X.Y0Peu/aIl7rDOFOANth.b/Prgy2ZVNNOHPK8lXw9XnF2B3ao.am', -- Bcrypt hash of 'safe-password123'
        'Adam',
        'ADMIN');

INSERT INTO actor(id, login, email, hash_password, name, role)
VALUES ('7b92d376-cc0d-4a1a-bc2e-d8f7c9d5e5a7',
        'bella_mystique',
        'bella@artisticvisions.com',
        'colorful2024',
        'Bella',
        'USER');

INSERT INTO actor(id, login, email, hash_password, name, role)
VALUES ('c82f1e44-12a2-4e9b-acd9-5b7a019c6d8c',
        'chris_crescendo',
        'chris@musicworld.com',
        'crescendo2024',
        'Chris',
        'USER');

INSERT INTO challenge(id, name, description, status)
VALUES ('4c39c496-ff63-4c8a-bad4-47d6a97053e7',
        'Conqueror',
        'Climb the highest peak of the mountain range and earn points',
        'ACTIVE');

INSERT INTO challenge(id, name, description, status)
VALUES ('8b7935ab-5e22-485b-ae18-7e5ad88b005e', 'test1-challenge', 'first challenge', 'ACTIVE'),
       ('af7c19c8-2b81-4bac-b91e-1cd56cc12551', 'test2-challenge', 'second challenge', 'ACTIVE'),
       (gen_random_uuid(), 'test3-challenge', 'third challenge', 'ACTIVE'),
       (gen_random_uuid(), 'test4-challenge', 'fourth challenge', 'REMOVED');

INSERT INTO summit(id, name, height, score, status)
VALUES ('6a20c34e-7de5-4216-8238-2e0a1ef45fcc', 'summit1', 1, 11, 'ACTIVE'),
       ('a4d928ab-d449-4064-904e-c64f19dd00d4', 'summit2', 2, 22, 'ACTIVE');

INSERT INTO challenge_summits_list(challenges_list_id, summits_list_id)
VALUES ('8b7935ab-5e22-485b-ae18-7e5ad88b005e', '6a20c34e-7de5-4216-8238-2e0a1ef45fcc'),
       ('8b7935ab-5e22-485b-ae18-7e5ad88b005e', 'a4d928ab-d449-4064-904e-c64f19dd00d4');

INSERT INTO user_challenge(id, user_id, challenge_id, started_at, finished_at, score)
VALUES ('cfe64228-2975-41af-bf4c-04bf48bc4523',
        '5c39c496-ff63-4c8a-bad4-47d6a97053e7',
        '4c39c496-ff63-4c8a-bad4-47d6a97053e7',
        TIMESTAMP '2004-10-19 10:23:54',
        TIMESTAMP '2014-03-11 08:23:54',
        30);


--Chris user challenge
INSERT INTO user_challenge(id, user_id, challenge_id, started_at, score)
VALUES ('c9ee3a19-0464-49d8-aba0-5b07a1b6fc65',
        'c82f1e44-12a2-4e9b-acd9-5b7a019c6d8c',
        'af7c19c8-2b81-4bac-b91e-1cd56cc12551',
        TIMESTAMP '2004-10-19 10:23:54',
        0);