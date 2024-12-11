INSERT INTO challenge (id, name, description, status, version)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458',
        'Korona Gór Polski',
        'Korona Gór Polski obejmuje zdobycie najwyższych szczytów 28 pasm górskich w Polsce. Każdy z tych szczytów, takich jak Rysy w Tatrach czy Śnieżka w Karkonoszach, ma swoje unikalne wyzwania i widoki. Zdobycie Korony Gór Polski nie wymaga specjalnych umiejętności wspinaczkowych, ale przede wszystkim determinacji.',
        'ACTIVE',
        0);

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('2e002769-13d9-409c-8cbe-c4d6cf83f427', 'Rysy', 'Tatry', 'Karpaty', 2499, 28, 'ACTIVE', 0, 49.179306,
        20.088444);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '2e002769-13d9-409c-8cbe-c4d6cf83f427');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('a1b2c3d4-e5f6-1234-5678-9abcdef01234', 'Babia Góra', 'Beskid Żywiecki', 'Karpaty', 1725, 27, 'ACTIVE', 0,
        49.573333, 19.529444);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'a1b2c3d4-e5f6-1234-5678-9abcdef01234');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('1234abcd-5678-9ef0-1234-56789abcdef0', 'Śnieżka', 'Karkonosze', 'Sudety', 1603, 26, 'ACTIVE', 0, 50.736028,
        15.739611);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '1234abcd-5678-9ef0-1234-56789abcdef0');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('9abcdef0-1234-5678-9abc-def012345678', 'Śnieżnik', 'Masyw Śnieżnika', 'Sudety', 1423, 25, 'ACTIVE', 0,
        50.207004, 16.849226);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '9abcdef0-1234-5678-9abc-def012345678');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('56789abc-def0-1234-5678-9abcdef01234', 'Tarnica', 'Bieszczady Zachodnie', 'Karpaty', 1346, 24, 'ACTIVE', 0,
        49.074778, 22.72675);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '56789abc-def0-1234-5678-9abcdef01234');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('def01234-5678-9abc-def0-123456789abc', 'Turbacz', 'Gorce', 'Karpaty', 1310, 23, 'ACTIVE', 0, 49.542944,
        20.111556);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'def01234-5678-9abc-def0-123456789abc');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('abcd1234-5678-9abc-def0-123456789abc', 'Radziejowa', 'Beskid Sądecki', 'Karpaty', 1266, 22, 'ACTIVE', 0,
        49.449444, 20.604444);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'abcd1234-5678-9abc-def0-123456789abc');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('789abcde-f012-3456-789a-bcdef0123456', 'Skrzyczne', 'Beskid Śląski', 'Karpaty', 1257, 21, 'ACTIVE', 0,
        49.684444, 19.030278);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '789abcde-f012-3456-789a-bcdef0123456');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('01234567-89ab-cdef-0123-456789abcdef', 'Mogielica', 'Beskid Wyspowy', 'Karpaty', 1171, 20, 'ACTIVE', 0,
        49.655194, 20.276694);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '01234567-89ab-cdef-0123-456789abcdef');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('25451076-fa01-4982-85ab-1378617157f8', 'Wysoka Kopa', 'Góry Izerskie', 'Sudety', 1126, 19, 'ACTIVE', 0,
        50.850278, 15.42);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '25451076-fa01-4982-85ab-1378617157f8');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('b08e11b6-ecd5-41a2-a175-048d7e6e499d', 'Rudawiec', 'Góry Bialskie', 'Sudety', 1106, 18, 'ACTIVE', 0, 50.244056,
        16.975889);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'b08e11b6-ecd5-41a2-a175-048d7e6e499d');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('339ced7a-5dcf-404d-9e52-ffb6b003ca0d', 'Orlica', 'Góry Orlickie', 'Sudety', 1084, 17, 'ACTIVE', 0, 50.353186,
        16.360719);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '339ced7a-5dcf-404d-9e52-ffb6b003ca0d');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('3363793a-8416-46a8-a70a-a5a171cc70a4', 'Wysoka', 'Pieniny', 'Karpaty', 1050, 16, 'ACTIVE', 0, 49.380278,
        20.555556);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '3363793a-8416-46a8-a70a-a5a171cc70a4');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('e420af27-92f2-4894-b80e-b3f7073434c6', 'Wielka Sowa', 'Góry Sowie', 'Sudety', 1015, 15, 'ACTIVE', 0, 50.680158,
        16.485497);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'e420af27-92f2-4894-b80e-b3f7073434c6');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('9a2428f2-3e9c-46c1-ada0-3a3a2a7d7e3f', 'Lackowa', 'Beskid Niski', 'Karpaty', 997, 14, 'ACTIVE', 0, 49.428333,
        21.096111);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '9a2428f2-3e9c-46c1-ada0-3a3a2a7d7e3f');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('4671758b-5183-4e37-b15c-1b427e9b654a', 'Kowadło', 'Góry Złote', 'Sudety', 989, 13, 'ACTIVE', 0, 50.264433,
        17.013219);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '4671758b-5183-4e37-b15c-1b427e9b654a');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('b0405b3a-d4f5-4a7a-af26-fa464aead310', 'Jagodna', 'Góry Bystrzyckie', 'Sudety', 977, 12, 'ACTIVE', 0,
        50.252472, 16.564722);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'b0405b3a-d4f5-4a7a-af26-fa464aead310');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('c17cbbe6-f38c-47a7-9f57-e22182225912', 'Skalnik', 'Rudawy Janowickie', 'Sudety', 945, 11, 'ACTIVE', 0, 50.8085,
        15.90025);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'c17cbbe6-f38c-47a7-9f57-e22182225912');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('11a28078-1645-4910-ace5-3d31ff324d32', 'Waligóra', 'Góry Kamienne', 'Sudety', 936, 10, 'ACTIVE', 0, 50.680833,
        16.278056);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '11a28078-1645-4910-ace5-3d31ff324d32');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('eb2b926a-bbfd-4834-99a9-f4fae83028c0', 'Czupel', 'Beskid Mały', 'Karpaty', 931, 9, 'ACTIVE', 0, 49.767944,
        19.160639);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'eb2b926a-bbfd-4834-99a9-f4fae83028c0');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('960776a4-9844-48d7-b971-029ff9cebb49', 'Szczeliniec Wielki', 'Góry Stołowe', 'Sudety', 919, 8, 'ACTIVE', 0,
        50.485833, 16.339167);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '960776a4-9844-48d7-b971-029ff9cebb49');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('1e658074-9482-4289-9685-1963fa63f6bf', 'Lubomir', 'Beskid Makowski', 'Karpaty', 904, 7, 'ACTIVE', 0, 49.766944,
        20.059722);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '1e658074-9482-4289-9685-1963fa63f6bf');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('445dc210-ac6a-4452-8185-2713533083c7', 'Biskupia Kopa', 'Góry Opawskie', 'Sudety', 889, 6, 'ACTIVE', 0,
        50.256667, 17.428611);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '445dc210-ac6a-4452-8185-2713533083c7');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('400561ee-146e-4d95-aedc-c67597c3df33', 'Chełmiec', 'Góry Wałbrzyskie', 'Sudety', 850, 5, 'ACTIVE', 0,
        50.779167, 16.210278);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '400561ee-146e-4d95-aedc-c67597c3df33');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('5e41bf98-3248-450d-b8dd-16daccd8c46c', 'Kłodzka Góra', 'Góry Bardzkie', 'Sudety', 765, 4, 'ACTIVE', 0,
        50.387222, 16.817222);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '5e41bf98-3248-450d-b8dd-16daccd8c46c');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('9e37a078-e195-4b89-80f0-f8f68e318ce1', 'Skopiec', 'Góry Kaczawskie', 'Sudety', 724, 3, 'ACTIVE', 0, 50.943971,
        15.884667);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '9e37a078-e195-4b89-80f0-f8f68e318ce1');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('77de0c2e-e9f5-4c56-9803-49056fc0ed62', 'Ślęża', 'Masyw Ślęży', 'Sudety', 718, 2, 'ACTIVE', 0, 50.865,
        16.708611);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', '77de0c2e-e9f5-4c56-9803-49056fc0ed62');

INSERT INTO summit (id, name, mountain_range, mountain_chain, height, score, status, version, coords_latitude,
                    coords_longitude)
VALUES ('f520ec6e-b3fe-4a72-90f7-246f13c7a1c6', 'Łysica', 'Góry Świętokrzyskie', 'Góry Świętokrzyskie', 614, 1, 'ACTIVE', 0, 50.890833,
        20.900833);

INSERT INTO challenge_summit (challenge_id, summit_id)
VALUES ('c7a62186-05bf-4563-a460-7c8c50059458', 'f520ec6e-b3fe-4a72-90f7-246f13c7a1c6');