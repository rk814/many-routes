CREATE TABLE actor
(
    id            UUID NOT NULL DEFAULT gen_random_uuid(),
    login         VARCHAR(50),
    hash_password VARCHAR(255),
    name          VARCHAR(50),
    email         VARCHAR(100),
    phone         VARCHAR(100),
    newsletter    BOOLEAN,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    role          VARCHAR(255),
    version       INTEGER,
    coords_latitude      DOUBLE PRECISION,
    coords_longitude     DOUBLE PRECISION,
    CONSTRAINT pk_actor PRIMARY KEY (id)
);

CREATE TABLE challenge_summit
(
    challenge_id UUID NOT NULL,
    summit_id    UUID NOT NULL,
    CONSTRAINT pk_challenge_summit PRIMARY KEY (challenge_id, summit_id)
);

CREATE TABLE challenge
(
    id          UUID NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(255),
    description VARCHAR(4000),
    status      VARCHAR(255),
    version     INTEGER,
    CONSTRAINT pk_challenge PRIMARY KEY (id)
);

CREATE TABLE summit
(
    id             UUID NOT NULL DEFAULT gen_random_uuid(),
    name           VARCHAR(255),
    mountain_range VARCHAR(255),
    mountain_chain VARCHAR(255),
    height         INTEGER,
    description    VARCHAR(4000),
    guide_notes    VARCHAR(4000),
    score          INTEGER      NOT NULL,
    status         VARCHAR(255) NOT NULL,
    version        INTEGER,
    coords_latitude       DOUBLE PRECISION,
    coords_longitude      DOUBLE PRECISION,
    CONSTRAINT pk_summit PRIMARY KEY (id)
);

CREATE TABLE user_challenge
(
    id           UUID NOT NULL DEFAULT gen_random_uuid(),
    started_at   TIMESTAMP WITHOUT TIME ZONE,
    finished_at  TIMESTAMP WITHOUT TIME ZONE,
    score        INTEGER NOT NULL,
    version      INTEGER,
    user_id      UUID NOT NULL,
    challenge_id UUID NOT NULL,
    CONSTRAINT pk_user_challenge PRIMARY KEY (id)
);

CREATE TABLE user_summit
(
    id                UUID NOT NULL DEFAULT gen_random_uuid(),
    conquered_at      TIMESTAMP WITHOUT TIME ZONE,
    score             INTEGER,
    version           INTEGER,
    user_challenge_id UUID NOT NULL,
    summit_id         UUID NOT NULL,
    CONSTRAINT pk_user_summit PRIMARY KEY (id)
);

ALTER TABLE actor
    ADD CONSTRAINT uc_actor_email UNIQUE (email);

ALTER TABLE actor
    ADD CONSTRAINT uc_actor_login UNIQUE (login);

ALTER TABLE challenge
    ADD CONSTRAINT uc_challenge_name UNIQUE (name);

ALTER TABLE summit
    ADD CONSTRAINT uc_summit_name UNIQUE (name);

ALTER TABLE user_challenge
    ADD CONSTRAINT FK_USER_CHALLENGE_ON_CHALLENGE FOREIGN KEY (challenge_id) REFERENCES challenge (id);

ALTER TABLE user_challenge
    ADD CONSTRAINT FK_USER_CHALLENGE_ON_USER FOREIGN KEY (user_id) REFERENCES actor (id);

ALTER TABLE user_summit
    ADD CONSTRAINT FK_USER_SUMMIT_ON_SUMMIT FOREIGN KEY (summit_id) REFERENCES summit (id);

ALTER TABLE user_summit
    ADD CONSTRAINT FK_USER_SUMMIT_ON_USER_CHALLENGE FOREIGN KEY (user_challenge_id) REFERENCES user_challenge (id);

ALTER TABLE challenge_summit
    ADD CONSTRAINT fk_chsu_on_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (id);

ALTER TABLE challenge_summit
    ADD CONSTRAINT fk_chsu_on_summit FOREIGN KEY (summit_id) REFERENCES summit (id);