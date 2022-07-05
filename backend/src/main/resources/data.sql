-- INSERT USER
INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (1, 'e1849a7d-ec51-46a7-8afc-ec2a77e00bd6', 'johnd', 'John', 'Doe', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'johnd@example.com', 'http://localhost:8080/users/image/e1849a7d-ec51-46a7-8afc-ec2a77e00bd6/dollar-gill-oKtRncpWNu0-unsplash.png', '2022-06-01', '2022-06-01', '1990-01-01', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (2, '00101a00-fc56-4d50-ae56-d5a623c37812', 'janed', 'Jane', 'Doe', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'janed@example.com', 'http://localhost:8080/users/image/00101a00-fc56-4d50-ae56-d5a623c37812/dollar-gill-oKtRncpWNu0-unsplash.png', '2027-09-25', '2022-05-10', '1996-07-15', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (3, '3da1a972-4c49-4388-a34a-64165e081fde', 'karen', 'Karen', 'Mngr', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'karen@example.com', 'http://localhost:8080/users/image/3da1a972-4c49-4388-a34a-64165e081fde/dollar-gill-oKtRncpWNu0-unsplash.png', '2010-01-01', '2022-06-08', '1999-07-15', true, true);

INSERT INTO user_entity (id, public_id, username, first_name, last_name, password, email, profile_image_url, join_date, last_login_date, date_of_birth, is_active, is_not_locked)
VALUES (4, 'fe79e302-8bfa-4eac-bc15-8ae448c019e8', 'admin', 'Admin', 'App', '$2a$12$qp.3PTIb3T/VMFSTsfxJkux2Qy8hYRbV92z1DNeKtjMAzBZn/mf7G', 'admin@example.com', 'http://localhost:8080/users/image/fe79e302-8bfa-4eac-bc15-8ae448c019e8/dollar-gill-oKtRncpWNu0-unsplash.png', '2009-01-01', '2022-06-14', '1994-02-14', true, true);

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
INSERT INTO role_authority (role_id, authority_id) VALUES (3, 3);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 1);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 2);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 3);
INSERT INTO role_authority (role_id, authority_id) VALUES (4, 4);

-- JOIN USER with ROLE
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO user_role (user_id, role_id) VALUES (4, 4);
