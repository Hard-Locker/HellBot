DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS guild CASCADE;
DROP TABLE IF EXISTS play_history CASCADE;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  username VARCHAR(255)
);

CREATE TABLE guild (
  id SERIAL PRIMARY KEY,
  guild_id BIGINT NOT NULL UNIQUE,
  guild_name VARCHAR(255)
);

CREATE TABLE play_history (
  action_id SERIAL PRIMARY KEY,
  event_datetime TIMESTAMP,
  url VARCHAR(255) NOT NULL,
  song_name VARCHAR(255),
  duration BIGINT,
  user_id BIGINT NOT NULL,
  guild_id BIGINT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (guild_id) REFERENCES guild (guild_id),
  note VARCHAR(255)
);
