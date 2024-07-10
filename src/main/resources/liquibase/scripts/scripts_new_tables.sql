-- changeset YuriPetukhov:4

ALTER TABLE users DROP COLUMN user_role;

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE card_sets (
    id BIGSERIAL PRIMARY KEY,
    set_name VARCHAR(100),
    set_description VARCHAR(255)
);

CREATE TABLE card_set_cards (
    card_set_id BIGINT,
    card_id BIGINT,
    PRIMARY KEY (card_set_id, card_id),
    FOREIGN KEY (card_set_id) REFERENCES card_sets(id),
    FOREIGN KEY (card_id) REFERENCES cards(id)
);

CREATE TABLE study_groups (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(100),
    description VARCHAR(255)
);

CREATE TABLE study_group_users (
    study_group_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (study_group_id, user_id),
    FOREIGN KEY (study_group_id) REFERENCES study_groups(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE study_group_card_sets (
    study_group_id BIGINT,
    card_set_id BIGINT,
    PRIMARY KEY (study_group_id, card_set_id),
    FOREIGN KEY (study_group_id) REFERENCES study_groups(id),
    FOREIGN KEY (card_set_id) REFERENCES card_sets(id)
);