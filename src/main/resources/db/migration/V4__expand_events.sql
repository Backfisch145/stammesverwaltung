alter table events
    add tribe_id bigint not null
        constraint tribe_id_fk
            references tribe;

