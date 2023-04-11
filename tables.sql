CREATE TABLE IF NOT EXISTS "user"
(
    login    varchar(100) PRIMARY KEY,
    password varchar(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_history"
(
    user_login      varchar(100),
    command_history text[7] default array []::text[],
    FOREIGN KEY ("user_login") REFERENCES "user" (login) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "collection"
(
    id                 integer primary key,
    movie              bytea not null,
    last_modified_date timestamp,
    editor             varchar(100),
    FOREIGN KEY (editor) REFERENCES "user" (login) ON DELETE SET NULL
);
