create sequence public.event_participants_seq
    increment by 50;

create sequence public.events_seq
    increment by 50;

create sequence public.groups_seq
    increment by 50;

create sequence public.users_seq
    increment by 50;

create table public.events
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

create table public.groups
(
    id      bigint  not null
        primary key,
    name    varchar(255),
    version integer not null
);

create table public.users
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

create table public.event_participants
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
            references public.events,
    user_id    bigint       not null
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references public.users
);

create table public.user_roles
(
    user_id bigint not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references public.users,
    roles   varchar(255)
        constraint user_roles_roles_check
            check ((roles)::text = ANY
        ((ARRAY ['USER'::character varying, 'MODERATOR'::character varying, 'ADMIN'::character varying])::text[]))
    );

