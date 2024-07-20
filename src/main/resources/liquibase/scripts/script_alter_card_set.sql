-- changeset YuriPetukhov:6

ALTER TABLE card_sets
ADD COLUMN user_id BIGINT;

ALTER TABLE card_sets
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES users(id);
