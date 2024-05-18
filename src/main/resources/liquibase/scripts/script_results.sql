-- changeset YuriPetukhov:3

create TABLE results (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL,
    interval VARCHAR(20) NOT NULL,
    result BOOLEAN NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards(id)
);