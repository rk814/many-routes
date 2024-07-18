ALTER TABLE actor
    ADD started_at TIMESTAMP,
    ADD deleted_at TIMESTAMP;

ALTER TABLE actor
    DROP CONSTRAINT coords_constraint;
ALTER TABLE actor
    ADD CONSTRAINT coords_constraint
        CHECK (coords_latitude >= -180 AND coords_longitude <= 180);