-- INSERT USER
INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (1, 'e1849a7d-ec51-46a7-8afc-ec2a77e00bd6', 'johnd', 'John', 'Doe', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'johnd@example.com', 'https://avatars.dicebear.com/api/adventurer/q.svg', '2022-06-01', '2022-06-01', '1990-01-01', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (2, 'e2d4b3af-595c-4462-ac3e-74ee0eb33dd4', 'janed', 'Jane', 'Doe', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'janed@example.com', 'https://avatars.dicebear.com/api/adventurer/qw.svg', '2027-09-25', '2022-05-10', '1996-07-15', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (3, 'e2d4b3af-595c-4462-ac3e-74ee0eb33dd4', 'karen', 'Karen', 'Mngr', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'karen@example.com', 'https://avatars.dicebear.com/api/adventurer/qwe.svg', '2010-01-01', '2022-06-08', '1980-07-15', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (4, 'e2d4b3af-595c-4462-ac3e-74ee0eb33dd4', 'admin', 'Admin', 'App', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'admin@example.com', 'https://avatars.dicebear.com/api/adventurer/qwer.svg', '2009-01-01', '2022-06-14', '1994-02-14', true, true);

-- UPDATE USER SEQUENCE
ALTER SEQUENCE user_sequence RESTART WITH 5;

-- ROLES, AUTHORITIES, ROLES WITH AUTHORITIES
INSERT INTO role (id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, role_name) VALUES (2, 'ROLE_HR');
INSERT INTO role (id, role_name) VALUES (3, 'ROLE_MANAGER');
INSERT INTO role (id, role_name) VALUES (4, 'ROLE_ADMIN');

INSERT INTO authority (id, permission) VALUES (1, 'user:read');
INSERT INTO authority (id, permission) VALUES (2, 'user:update');
INSERT INTO authority (id, permission) VALUES (3, 'user:create');
INSERT INTO authority (id, permission) VALUES (4, 'user:delete');

INSERT INTO role_authority (role_id, authority_id) VALUES (1, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (2, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (2, 2);
INSERT INTO role_authority (role_id, authority_id) VALUES (3, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (3, 2);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 2);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 3);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 4);

-- JOIN USER with ROLE
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO user_role (user_id, role_id) VALUES (4, 4);
