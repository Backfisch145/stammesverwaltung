insert into tribe (id, name) VALUES (1, 'ADMIN');
insert into users (version, id, username, first_name, last_name, tribe_id, hashed_password) values (1, 1,'admin', 'admin', 'user', 1,'$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe');
insert into users_roles (user_id, roles_name) VALUES (1, 'ADMIN');