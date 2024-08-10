INSERT INTO actor(id,login,email, hash_password, name)
VALUES ('5c39c496-ff63-4c8a-bad4-47d6a97053e7',
        'adam',
        'adam@email.com',
        'xxx',
        'Adam');

INSERT INTO challenge(id, name)
VALUES ('4c39c496-ff63-4c8a-bad4-47d6a97053e7',
        'KGP');

INSERT INTO user_challenge(id, user_id, challenge_id, started_at, finished_at, score)
VALUES ('6c39c496-ff63-4c8a-bad4-47d6a97053e7',
        '5c39c496-ff63-4c8a-bad4-47d6a97053e7',
        '4c39c496-ff63-4c8a-bad4-47d6a97053e7',
        TIMESTAMP '2004-10-19 10:23:54',
        TIMESTAMP '2014-03-11 08:23:54',
        30);