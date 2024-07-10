-- changeset YuriPetukhov:5

INSERT INTO roles (role_name) VALUES
('ROLE_USER'),
('ROLE_TEACHER'),
('ROLE_STUDENT'),
('ROLE_ADMIN')
ON CONFLICT (role_name) DO NOTHING;
