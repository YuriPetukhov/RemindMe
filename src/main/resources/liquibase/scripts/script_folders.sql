-- changeset YuriPetukhov:7

CREATE TABLE folders (
    id BIGSERIAL PRIMARY KEY,
    folder_name VARCHAR(255) NOT NULL,
    parent_folder_id BIGINT,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (parent_folder_id) REFERENCES folders(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE card_sets
ADD COLUMN folder_id BIGINT;

ALTER TABLE card_sets
ADD CONSTRAINT fk_card_sets_folders
FOREIGN KEY (folder_id) REFERENCES folders(id);
