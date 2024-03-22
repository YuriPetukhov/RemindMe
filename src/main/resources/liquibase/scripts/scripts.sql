-- liquibase formatted sql

-- changeset YuriPetukhov:1

create TABLE users (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT,
    user_name VARCHAR(20),
    user_role VARCHAR(20),
    card_input_state VARCHAR(20)
);

create TABLE cards(
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(100),
    meaning VARCHAR(250),
    card_activity VARCHAR(20),
    recall_mode VARCHAR(20),
    next_date_time TIMESTAMP,
    interval VARCHAR(20),
    user_id BIGSERIAL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

