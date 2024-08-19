INSERT INTO actor (id, login, hash_password, name, email, role, version)
VALUES ('27105bda-ebde-4eed-b4ea-179e3baefc68', 'admin', 'admin', 'Admin', 'admin@admin.com', 'ADMIN', 0);


DELETE FROM actor WHERE login LIKE 'admin';


TRUNCATE TABLE actor;