-- changeset YuriPetukhov:5

CREATE TABLE metrics (
    id BIGSERIAL PRIMARY KEY,
    message TEXT,
    chat_id BIGINT,
    timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
