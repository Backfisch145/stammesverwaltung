create sequence user_tags_seq
    increment by 50;

create table user_tags (
    id      bigint not null primary key,
    name    varchar(255),
    color   varchar(255),
    icon    varchar(255),
    tribe_id  bigint  not null
        constraint fk_tribe
            references tribe
);

create table users_to_tags
(
    user_id      bigint not null
        constraint fk_user
            references users,
    user_tags_id bigint not null
        constraint fk_user_tag
            references user_tags,
    primary key (user_id, user_tags_id)
);

