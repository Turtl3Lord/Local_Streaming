-- V4: Create episodes table (depends on seasons)

CREATE TABLE IF NOT EXISTS episodes (
  id BIGSERIAL PRIMARY KEY,
  season_id BIGINT NOT NULL,
  episode_number INTEGER NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  cover_url VARCHAR(500),
  video_url VARCHAR(500) NOT NULL,
  duration_minutes INTEGER,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT uq_episodes_season_episode UNIQUE (season_id, episode_number),
  CONSTRAINT fk_episodes_seasons FOREIGN KEY (season_id) REFERENCES seasons (id) ON DELETE CASCADE
);

