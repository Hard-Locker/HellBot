DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS guild CASCADE;
DROP TABLE IF EXISTS event_history CASCADE;
DROP TABLE IF EXISTS song_history CASCADE;

CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  username VARCHAR(255)
);

CREATE TABLE guild (
  id BIGSERIAL PRIMARY KEY,
  guild_id BIGINT NOT NULL UNIQUE,
  guild_name VARCHAR(255)
);

CREATE TABLE event_history (
  id BIGSERIAL PRIMARY KEY,
  event_datetime TIMESTAMP,
  event_type VARCHAR(255) NOT NULL,
  user_id BIGINT NOT NULL,
  guild_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (guild_id) REFERENCES guild (guild_id),
  note VARCHAR(255)
);

CREATE TABLE song_history (
  id BIGSERIAL PRIMARY KEY,
  event_datetime TIMESTAMP,
  song_url VARCHAR(255) NOT NULL,
  song_artist VARCHAR(255),
  song_name VARCHAR(255),
  song_duration BIGINT,
  user_id BIGINT NOT NULL,
  guild_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (guild_id) REFERENCES guild (guild_id),
  note VARCHAR(255)
);
