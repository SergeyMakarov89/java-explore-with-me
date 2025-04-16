DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT,
    lon FLOAT
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests BIGINT,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT,
    location_id BIGINT NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    participant_limit BIGINT,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state VARCHAR(255),
    title VARCHAR(120),
    views BIGINT DEFAULT 0,
    FOREIGN KEY (initiator_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY  (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requestor_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY  (requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    commentator_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    comment VARCHAR(1000) NOT NULL,
    FOREIGN KEY (commentator_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);