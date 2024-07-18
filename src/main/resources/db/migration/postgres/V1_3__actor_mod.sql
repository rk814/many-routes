ALTER TABLE actor
    DROP CONSTRAINT coords_constraint;
ALTER TABLE actor
    ADD CONSTRAINT coords_constraint
        CHECK (coords_latitude >= -180 AND coords_latitude <= 180 AND
        coords_longitude >= -180 AND coords_longitude <= 180);
