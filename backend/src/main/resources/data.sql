-- INSERT USER
INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (1, 'e1849a7d-ec51-46a7-8afc-ec2a77e00bd6', 'johnny', 'John', 'Doe', 'encPass', 'john@example.com', 'http://', '2022-06-01', '2022-06-01', '1990-01-01', true, true);

-- UPDATE USER SEQUENCE
ALTER SEQUENCE user_sequence RESTART WITH 2;

-- INSERT ROLE
INSERT INTO role (id, role_name) VALUES (1, 'SUPER_ADMIN');

-- JOIN USER with ROLE
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

-- INSERT AUTHORITY
INSERT INTO authority (id, permission) VALUES (1, 'read');
INSERT INTO authority (id, permission) VALUES (2, 'write');
INSERT INTO authority (id, permission) VALUES (3, 'delete');

-- JOIN AUTHORITY with ROLE
INSERT INTO role_authority (role_id, authority_id) VALUES (1, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (1, 2);
INSERT INTO role_authority (role_id, authority_id) VALUES (1, 3);

-- INSERT INTO role (id, roleName) VALUES (1L, 'SUPER_ADMIN');
-- INSERT INTO role (id, roleName) VALUES (2L, 'ADMIN');
-- INSERT INTO role (id, roleName) VALUES (3L, 'MANAGER');
-- INSERT INTO role (id, roleName) VALUES (4L, 'USER');