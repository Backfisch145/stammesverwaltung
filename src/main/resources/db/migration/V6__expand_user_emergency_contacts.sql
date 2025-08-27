alter table user_emergency_contact
    add user_id bigint not null
        constraint user_fk
            references users


