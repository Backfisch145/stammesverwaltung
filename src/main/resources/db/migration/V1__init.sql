create sequence event_participants_seq
    increment by 50;

create sequence events_seq
    increment by 50;

create sequence groups_seq
    increment by 50;

create sequence users_seq
    increment by 50;

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

create table groups
(
    id      bigint  not null
        primary key,
    name    varchar(255),
    version integer not null
);

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

create table event_participants
(
    id         bigint       not null
        primary key,
    event_role varchar(255) not null
        constraint event_participants_event_role_check
            check ((event_role)::text = ANY
        ((ARRAY ['PARTICIPANT'::character varying, 'COOK'::character varying, 'ORGANISER'::character varying])::text[])),
    status     varchar(255)
        constraint event_participants_status_check
            check ((status)::text = ANY
                   ((ARRAY ['INVITED'::character varying, 'DECLINED'::character varying, 'IN_PAYMENT'::character varying, 'IN_REPAYMENT'::character varying, 'REGISTERED'::character varying])::text[])),
    event_id   integer      not null
        constraint fk2x391urx4up03f4jp2y9mdt5x
            references events,
    user_id    bigint       not null
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references users
);

create table roles
(
    name    varchar(255) not null
        primary key,
    user_id bigint
        constraint fk97mxvrajhkq19dmvboprimeg1
            references users
);

create table roles_permissions
(
    id         varchar(255) not null
        constraint fkjborrqw6nakj98opptncin4ki
            references roles,
    permission varchar(255) not null
        constraint roles_permissions_permission_check
            check ((permission)::text = ANY
        ((ARRAY ['MEMBER_READ'::character varying, 'MEMBER_INSERT'::character varying, 'MEMBER_DELETE'::character varying, 'MEMBER_UPDATE'::character varying, 'INVENTORY_READ'::character varying, 'INVENTORY_INSERT'::character varying, 'INVENTORY_DELETE'::character varying, 'INVENTORY_UPDATE'::character varying, 'EVENT_READ'::character varying, 'EVENT_INSERT'::character varying, 'EVENT_DELETE'::character varying, 'EVENT_UPDATE'::character varying])::text[])),
    primary key (id, permission)
);