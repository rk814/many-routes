CREATE TABLE actor
(
    id               uuid PRIMARY KEY UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    login            VARCHAR(50) UNIQUE      NOT NULL,
    hash_password    VARCHAR                 NOT NULL,
    name             VARCHAR(50),
    email            VARCHAR(100) UNIQUE     NOT NULL,
    phone            VARCHAR(100),
    newsletter       bool,
    role             VARCHAR,
    coords_latitude  double precision,
    coords_longitude double precision,
    version          int
);

ALTER TABLE actor
    ADD CONSTRAINT coords_constraint
        CHECK (
            coords_latitude >= 0 AND coords_longitude >= 0
            );