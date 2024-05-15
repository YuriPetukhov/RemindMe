-- liquibase formatted sql

-- changeset YuriPetukhov:2
CREATE INDEX idx_word ON cards(word);