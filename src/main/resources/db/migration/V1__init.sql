create sequence event_participants_seq
    increment by 50;

create sequence events_seq
    increment by 50;

create sequence groups_seq
    increment by 50;

create sequence users_seq
    start with 100
    increment by 50;

create sequence user_emergency_contact_seq
    increment by 50;

create sequence user_files_seq
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

create table tribe
(
    id             bigint           not null
        primary key,
    name           varchar(255)     not null,
    intervall      integer,
    intervall_unit varchar(255)
        constraint tribe_intervall_unit_check
            check ((intervall_unit)::text = ANY
                   ((ARRAY ['NANOS'::character varying, 'MICROS'::character varying, 'MILLIS'::character varying, 'SECONDS'::character varying, 'MINUTES'::character varying, 'HOURS'::character varying, 'HALF_DAYS'::character varying, 'DAYS'::character varying, 'WEEKS'::character varying, 'MONTHS'::character varying, 'YEARS'::character varying, 'DECADES'::character varying, 'CENTURIES'::character varying, 'MILLENNIA'::character varying, 'ERAS'::character varying, 'FOREVER'::character varying])::text[])),
    price          double precision
);

create table items
(
    id           uuid not null
        primary key,
    created_at   date,
    description  varchar(255),
    name         varchar(255),
    picture      oid,
    tags         varchar(255)[],
    container_id uuid
        constraint fki073j4h2svnxg9s93hu1q4pd8
            references items,
    tribe_id     bigint
        constraint fkbqe36paxts288pk8w9q93ckb2
            references tribe
);

create table tribe_events
(
    tribe_id  bigint  not null
        constraint fkbrrl8ojsy0gb5a9jvi2s9ucwp
            references tribe,
    events_id integer not null
        constraint fksrm6f8pic21ummxuqhd7jxbys
            references events,
    primary key (tribe_id, events_id)
);

create table user_emergency_contact
(
    id      bigint not null
        primary key,
    address varchar(255),
    email   varchar(255),
    name    varchar(255),
    phone   varchar(255)
);

create table users
(
    id                        bigint       not null
        primary key,
    date_of_birth             date,
    eating_habits             varchar(255),
    email                     varchar(255),
    first_name                varchar(255) not null,
    gender                    varchar(255)
        constraint users_gender_check
            check ((gender)::text = ANY
                   (ARRAY [('MALE'::character varying)::text, ('FEMALE'::character varying)::text, ('OTHER'::character varying)::text])),
    hashed_password           varchar(255),
    intolerances              varchar(255),
    last_name                 varchar(255) not null,
    level                     varchar(255)
        constraint users_level_check
            check ((level)::text = ANY
                   (ARRAY [('WOELFLING'::character varying)::text, ('JUNGPFADFINDER'::character varying)::text, ('PFADFINDER'::character varying)::text, ('ROVER'::character varying)::text, ('ERWACHSEN'::character varying)::text])),
    membership_id             integer,
    phone                     varchar(255),
    pictures_allowed          boolean DEFAULT false,
    can_swim                   boolean DEFAULT false,
    profile_picture           oid,
    username                  varchar(255),
    version                   integer      not null,
    address                   varchar(255),
    info_update_mail_sent     date,
    join_date                 date,
    tags                      varchar(255)[],
    tribe_id                  bigint
        constraint fklu6sy4kf95rmulo1n59flgb9y
            references tribe,
    user_emergency_contact_id bigint
        constraint uk_apnmvucyik7m5qncxp3movtgv
            unique
        constraint fkiy8xhi7e43x6s2kp3kybh3734
            references user_emergency_contact
);

create table event_participants
(
    id         bigint       not null
        primary key,
    event_role varchar(255) not null
        constraint event_participants_event_role_check
            check ((event_role)::text = ANY
                   (ARRAY [('PARTICIPANT'::character varying)::text, ('COOK'::character varying)::text, ('ORGANISER'::character varying)::text])),
    status     varchar(255)
        constraint event_participants_status_check
            check ((status)::text = ANY
                   (ARRAY [('INVITED'::character varying)::text, ('DECLINED'::character varying)::text, ('IN_PAYMENT'::character varying)::text, ('IN_REPAYMENT'::character varying)::text, ('REGISTERED'::character varying)::text])),
    event_id   integer      not null
        constraint fk2x391urx4up03f4jp2y9mdt5x
            references events,
    user_id    bigint       not null
        constraint fkre6m0d4mgt4351tytlkac9jvf
            references users,
    constraint uk9adsffoeo3ivo3abi3qwke70i
        unique (user_id, event_id)
);

create table roles
(
    name    varchar(255) not null
        primary key
);

create table roles_permissions
(
    id         varchar(255) not null
        constraint fkjborrqw6nakj98opptncin4ki
            references roles,
    permission varchar(255) not null
        constraint roles_permissions_permission_check
            check ((permission)::text = ANY
                   (ARRAY [('MEMBER_READ'::character varying)::text, ('MEMBER_INSERT'::character varying)::text, ('MEMBER_DELETE'::character varying)::text, ('MEMBER_UPDATE'::character varying)::text, ('INVENTORY_READ'::character varying)::text, ('INVENTORY_INSERT'::character varying)::text, ('INVENTORY_DELETE'::character varying)::text, ('INVENTORY_UPDATE'::character varying)::text, ('EVENT_READ'::character varying)::text, ('EVENT_INSERT'::character varying)::text, ('EVENT_DELETE'::character varying)::text, ('EVENT_UPDATE'::character varying)::text])),
    primary key (id, permission)
);



create table user_files
(
    id            bigint not null
        primary key,
    filename      varchar(255),
    path          varchar(255),
    size          bigint,
    type          varchar(255)
        constraint user_files_type_check
            check ((type)::text = 'MEMBERSHIP_AGREEMENT'::text),
    upload_date   timestamp(6),
    uploader_id   bigint
        constraint uk_43r3x05xnmm4gk14v009pb59
            unique
        constraint fk4l86ggfatkcmhwhht89xuvvks
            references users,
    user_id       bigint not null
        constraint fkrelrpb2d8sq8bh91dob4t50vs
            references users,
    user_files_id bigint
        constraint fk3b8eyn45v4qk9r0sefvmngru1
            references users
);

create table users_roles
(
    user_id    bigint       not null
        constraint fklu6sy4kf95rmulo1n59flgb9y
            references users,
    roles_name varchar(255) not null
        constraint fkmi9sfx618v14gm89cyw408hqu
            references roles,
    primary key (user_id, roles_name)
);

