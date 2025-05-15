create table one_time_tokens
(
    id          IDENTITY primary key,
    token_value varchar(255) not null,
    username    varchar(255) not null,
    created_at  timestamp    not null default now(),
    expires_at  timestamp    not null
);