-- liquibase formatted sql

-- changeset AntonS:1
CREATE TABLE IF NOT EXISTS student (
    faculty_id  SERIAL,
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL
);

-- changeset AntonS:2
CREATE INDEX IF NOT EXISTS name_index ON student (name);

-- changeset AntonS:3
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty (name, color);


-- changeset AntonS:4
CREATE TABLE IF NOT EXISTS faculty (
    id      SERIAL PRIMARY KEY,
    color   TEXT NOT NULL,
    name    TEXT NOT NULL
);