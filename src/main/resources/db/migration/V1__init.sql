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

create table events
(
    id                     integer not null
        primary key,
    address                varchar(255),
    ending_time            timestamp(6),
    name                   varchar(255),
    participation_deadline timestamp(6),
    payment_deadline       timestamp(6),
    price                  double precision,
    starting_time          timestamp(6),
    version                integer not null
);

alter table events
    owner to postgres;

create table groups
(
    id      bigint  not null
        primary key,
    name    varchar(255),
    version integer not null
);

alter table groups
    owner to postgres;

create table users
(
    id               bigint       not null
        primary key,
    date_of_birth    date,
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
    membership_id    integer,
    phone            varchar(255),
    pictures_allowed boolean,
    profile_picture  oid,
    username         varchar(255),
    version          integer      not null
);

alter table users
    owner to postgres;

create table event_participants
(
    id         bigint  not null
        primary key,
    event_role varchar(255)
        constraint event_participants_event_role_check
            check ((event_role)::text = ANY
                   ((ARRAY ['PARTICIPANT'::character varying, 'COOK'::character varying, 'ORGANISER'::character varying])::text[])),
    status     varchar(255)
        constraint event_participants_status_check
            check ((status)::text = ANY
                   ((ARRAY ['INVITED'::character varying, 'DECLINED'::character varying, 'IN_PAYMENT'::character varying, 'IN_REPAYMENT'::character varying, 'REGISTERED'::character varying])::text[])),
    version    integer not null,
    event_id   integer
        constraint fk2x391urx4up03f4jp2y9mdt5x
            references events,
    user_id    bigint
        constraint uk_dilgs22qiib3pm7226q2exo6p
            unique
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references users
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
            check ((roles)::text = ANY
                   ((ARRAY ['USER'::character varying, 'MODERATOR'::character varying, 'ADMIN'::character varying])::text[]))
);

alter table user_roles
    owner to postgres;

