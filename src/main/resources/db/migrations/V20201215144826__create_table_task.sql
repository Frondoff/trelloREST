create table if not exists task
(
    id              serial  not null
        constraint task_pk primary key,
    name            varchar not null,
    description     varchar,
    date            varchar,
    position        integer not null,
    board_column_id integer not null
        constraint board_column_id_fk
            references board_column
            on update cascade on delete cascade
);