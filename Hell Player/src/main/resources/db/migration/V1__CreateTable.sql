DROP TABLE IF EXISTS play_history CASCADE;

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
