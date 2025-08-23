insert into roles (name)
values ('USER');
insert into roles (name)
values ('MODERATOR');
insert into roles (name)
values ('ADMIN');

insert into roles_permissions (id, permission)
values ('ADMIN', 'MEMBER_READ');
insert into roles_permissions (id, permission)
values ('ADMIN', 'MEMBER_INSERT');
insert into roles_permissions (id, permission)
values ('ADMIN', 'MEMBER_DELETE');
insert into roles_permissions (id, permission)
values ('ADMIN', 'MEMBER_UPDATE');
insert into roles_permissions (id, permission)
values ('ADMIN', 'INVENTORY_READ');
insert into roles_permissions (id, permission)
values ('ADMIN', 'INVENTORY_INSERT');
insert into roles_permissions (id, permission)
values ('ADMIN', 'INVENTORY_DELETE');
insert into roles_permissions (id, permission)
values ('ADMIN', 'INVENTORY_UPDATE');
insert into roles_permissions (id, permission)
values ('ADMIN', 'EVENT_READ');
insert into roles_permissions (id, permission)
values ('ADMIN', 'EVENT_INSERT');
insert into roles_permissions (id, permission)
values ('ADMIN', 'EVENT_DELETE');
insert into roles_permissions (id, permission)
values ('ADMIN', 'EVENT_UPDATE');