INSERT INTO actor (id, login, hash_password, name, email, role, version)
VALUES ('27105bda-ebde-4eed-b4ea-179e3baefc68', 'admin', '$2a$10$uVM/1/OmZEt8awIlT.nVlu02fQz56IP/O8UlKXcpcTymICdJfKbHC', 'Admin', 'admin@admin.com', 'ADMIN', 0);


DELETE FROM actor WHERE login LIKE 'admin';


TRUNCATE TABLE actor;