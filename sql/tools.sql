INSERT INTO actor (id, login, hash_password,created_at, name, email, role, version)
VALUES ('27105bda-ebde-4eed-b4ea-179e3baefc68', 'admin', '$2a$10$uVM/1/OmZEt8awIlT.nVlu02fQz56IP/O8UlKXcpcTymICdJfKbHC', CURRENT_TIMESTAMP , 'Admin', 'admin@admin.com', 'ADMIN', 0);


DELETE FROM actor WHERE login LIKE 'admin';


TRUNCATE TABLE user_summit;
TRUNCATE TABLE user_challenge;
TRUNCATE TABLE challenge_summit;
TRUNCATE TABLE summit;
TRUNCATE TABLE challenge;
TRUNCATE TABLE actor;


DROP TABLE IF EXISTS flyway_schema_history CASCADE;
DROP TABLE IF EXISTS user_summit CASCADE;
DROP TABLE IF EXISTS user_challenge CASCADE;
DROP TABLE IF EXISTS summit CASCADE;
DROP TABLE IF EXISTS challenge CASCADE;
DROP TABLE IF EXISTS challenge_summit CASCADE;
DROP TABLE IF EXISTS actor CASCADE;