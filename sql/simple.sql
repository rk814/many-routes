INSERT INTO actor (login, hash_password, name, email, role, version)
VALUES ('admin', 'admin', 'Admin', 'admin@admin.com', 'ADMIN', 0);


DELETE FROM actor WHERE login LIKE 'admin';


TRUNCATE TABLE actor;