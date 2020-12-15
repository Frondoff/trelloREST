create table if not exists board_column
(
    id       serial  not null
        constraint board_column_pk
            primary key,
    name     varchar not null,
    position integer not null
);