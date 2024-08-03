-- changeset YuriPetukhov:8

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

DROP TABLE study_group_users;

CREATE TABLE study_group_students (
    study_group_id BIGINT,
    student_id BIGINT,
    PRIMARY KEY (study_group_id, student_id),
    FOREIGN KEY (study_group_id) REFERENCES study_groups(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

