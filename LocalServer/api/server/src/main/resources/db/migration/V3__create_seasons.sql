-- V3: Create seasons table (depends on series)

CREATE TABLE IF NOT EXISTS seasons (
  id BIGSERIAL PRIMARY KEY,
  series_id BIGINT NOT NULL,
  season_number INTEGER NOT NULL,
  title VARCHAR(255),
  description TEXT,
  release_year INTEGER,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT uq_seasons_series_season UNIQUE (series_id, season_number),
  CONSTRAINT fk_seasons_series FOREIGN KEY (series_id) REFERENCES series (id) ON DELETE CASCADE
);

