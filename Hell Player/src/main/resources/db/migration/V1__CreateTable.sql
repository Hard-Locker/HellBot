DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS guild CASCADE;
DROP TABLE IF EXISTS play_history CASCADE;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  user_id BIGINT,
  user_name VARCHAR(255),
);

CREATE TABLE guild (
  id SERIAL PRIMARY KEY,
  guild_id BIGINT,
  guild_name VARCHAR(255),
);

CREATE TABLE play_history (
  action_id SERIAL PRIMARY KEY,
  event_datetime TIMESTAMP NOT NULL,
  url VARCHAR(255) NOT NULL,
  song_name VARCHAR(255),
  duration INTERVAL,
  user_id BIGINT,
  username VARCHAR(255),
  note VARCHAR(255)
);
