create sequence event_participants_seq
    increment by 50;

alter sequence event_participants_seq owner to postgres;

create sequence events_seq
    increment by 50;

alter sequence events_seq owner to postgres;

create sequence groups_seq
    increment by 50;

alter sequence groups_seq owner to postgres;

create sequence users_seq
    increment by 50;

alter sequence users_seq owner to postgres;


create table intolerances
(
    name varchar(255) not null
        primary key
);

alter table intolerances
    owner to postgres;

create table users_intolerances
(
    user_id           bigint       not null,
    intolerances_name varchar(255) not null
        constraint fk3n1ptlrmr4tv6l1cb6y543f1l
            references intolerances,
    primary key (user_id, intolerances_name)
);

alter table users_intolerances
    owner to postgres;

create table events_users
(
    event_id bigint not null,
    users_id bigint not null,
    primary key (event_id, users_id)
);

alter table events_users
    owner to postgres;

create table events
(
    price                  real    not null,
    version                integer not null,
    ending_time            timestamp(6),
    id                     bigint  not null
        primary key,
    participation_deadline timestamp(6),
    starting_time          timestamp(6),
    address                varchar(255),
    name                   varchar(255)
);

alter table events
    owner to postgres;

create table groups
(
    version integer not null,
    id      bigint  not null
        primary key,
    name    varchar(255)
);

alter table groups
    owner to postgres;

create table users
(
    date_of_birth    date,
    membership_id    integer,
    pictures_allowed boolean,
    version          integer      not null,
    id               bigint       not null
        primary key,
    eating_habits    varchar(255),
    email            varchar(255),
    first_name       varchar(255) not null,
    gender           varchar(255)
        constraint users_gender_check
            check ((gender)::text = ANY
                   ((ARRAY ['MALE'::character varying, 'FEMALE'::character varying, 'OTHER'::character varying])::text[])),
    hashed_password  varchar(255),
    intolerances     varchar(255),
    last_name        varchar(255) not null,
    level            varchar(255)
        constraint users_level_check
            check ((level)::text = ANY
                   ((ARRAY ['WOELFLING'::character varying, 'JUNGPFADFINDER'::character varying, 'PFADFINDER'::character varying, 'ROVER'::character varying, 'ERWACHSEN'::character varying])::text[])),
    phone            varchar(255),
    username         varchar(255),
    profile_picture  oid
);

alter table users
    owner to postgres;

create table event_participants
(
    version    integer not null,
    event_id   bigint
        constraint fk2x391urx4up03f4jp2y9mdt5x
            references events,
    id         bigint  not null
        primary key,
    user_id    bigint
        unique
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references users,
    event_role varchar(255)
        constraint event_participants_event_role_check
            check ((event_role)::text = ANY
                   ((ARRAY ['PARTICIPANT'::character varying, 'COOK'::character varying, 'ORGANISER'::character varying])::text[])),
    status     varchar(255)
        constraint event_participants_status_check
            check ((status)::text = ANY
                   ((ARRAY ['INVITED'::character varying, 'DECLINED'::character varying, 'IN_PAYMENT'::character varying, 'IN_REPAYMENT'::character varying, 'REGISTERED'::character varying])::text[]))
);

alter table event_participants
    owner to postgres;

create table user_roles
(
    user_id bigint not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references users,
    roles   varchar(255)
        constraint user_roles_roles_check
            check ((roles)::text = ANY ((ARRAY ['USER'::character varying, 'ADMIN'::character varying])::text[]))
);

alter table user_roles
    owner to postgres;

