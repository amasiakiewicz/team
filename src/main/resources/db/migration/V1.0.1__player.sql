create table player
(
    id                uuid primary key,
    version           int                      not null,
    created_date_time timestamp with time zone not null,
    team_id           uuid                     not null
);
