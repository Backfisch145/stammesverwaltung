create sequence events_seq
    increment by 50;

alter sequence events_seq owner to postgres;

create sequence users_seq
    increment by 50;

alter sequence users_seq owner to postgres;

create table events
(
    price         real    not null,
    version       integer not null,
    ending_time   timestamp(6),
    id            bigint  not null
        primary key,
    starting_time timestamp(6),
    address       varchar(255),
    name          varchar(255)
);

alter table events
    owner to postgres;

create table intolerances
(
    name varchar(255) not null
        primary key
);

alter table intolerances
    owner to postgres;

create table users
(
    date_of_birth   date,
    version         integer not null,
    membership_id   integer,
    id              bigint  not null
        primary key,
    email           varchar(255),
    first_name      varchar(255),
    gender          varchar(255)
        constraint users_gender_check
            check ((gender)::text = ANY
                   (ARRAY [('MALE'::character varying)::text, ('FEMALE'::character varying)::text, ('OTHER'::character varying)::text])),
    hashed_password varchar(255),
    last_name       varchar(255),
    level           varchar(255)
        constraint users_level_check
            check ((level)::text = ANY
                   (ARRAY [('WOELFLING'::character varying)::text, ('JUNGPFADFINDER'::character varying)::text, ('PFADFINDER'::character varying)::text, ('ROVER'::character varying)::text, ('ERWACHSEN'::character varying)::text])),
    phone           varchar(255),
    username        varchar(255),
    profile_picture oid
);

alter table users
    owner to postgres;

create table user_roles
(
    user_id bigint not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references users,
    roles   varchar(255)
        constraint user_roles_roles_check
            check ((roles)::text = ANY (ARRAY [('USER'::character varying)::text, ('ADMIN'::character varying)::text]))
);

alter table user_roles
    owner to postgres;

create table users_intolerances
(
    user_id           bigint       not null
        constraint fkbyg7vm11k1y3230u3sfxn3mlx
            references users,
    intolerances_name varchar(255) not null
        constraint fk3n1ptlrmr4tv6l1cb6y543f1l
            references intolerances,
    primary key (user_id, intolerances_name)
);

alter table users_intolerances
    owner to postgres;

create table events_users
(
    event_id bigint not null
        constraint fkd46my3jbxrdmcd3m2dryyc295
            references events,
    users_id bigint not null
        constraint fk2xqmgftqei5c4s82ruxl74w9a
            references users,
    primary key (event_id, users_id)
);

alter table events_users
    owner to postgres;

