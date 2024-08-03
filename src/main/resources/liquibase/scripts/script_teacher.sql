-- changeset YuriPetukhov:9

ALTER TABLE study_groups
ADD COLUMN teacher_id BIGINT,
ADD CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES users(id);

