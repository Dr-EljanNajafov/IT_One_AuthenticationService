CREATE TABLE IF NOT EXISTS account
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mail   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL
);