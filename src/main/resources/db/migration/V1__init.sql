create table public.events
(
    id                     integer not null
        primary key,
    price                  double precision,
    version                integer not null,
    ending_time            timestamp(6),
    participation_deadline timestamp(6),
    payment_deadline       timestamp(6),
    starting_time          timestamp(6),
    address                varchar(255),
    name                   varchar(255)
);

alter table public.events
    owner to postgres;

create table public.groups
(
    version integer not null,
    id      bigint  not null
        primary key,
    name    varchar(255)
);

alter table public.groups
    owner to postgres;

create table public.users
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

alter table public.users
    owner to postgres;

create table public.event_participants
(
    event_id   integer
        constraint fk2x391urx4up03f4jp2y9mdt5x
            references public.events,
    id         bigint       not null
        primary key,
    user_id    bigint       not null
        unique
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references public.users,
    event_role varchar(255) not null
        constraint event_participants_event_role_check
            check ((event_role)::text = ANY
                   ((ARRAY ['PARTICIPANT'::character varying, 'COOK'::character varying, 'ORGANISER'::character varying])::text[])),
    status     varchar(255)
        constraint event_participants_status_check
            check ((status)::text = ANY
                   ((ARRAY ['INVITED'::character varying, 'DECLINED'::character varying, 'IN_PAYMENT'::character varying, 'IN_REPAYMENT'::character varying, 'REGISTERED'::character varying])::text[]))
);

alter table public.event_participants
    owner to postgres;

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

alter table public.user_roles
    owner to postgres;

