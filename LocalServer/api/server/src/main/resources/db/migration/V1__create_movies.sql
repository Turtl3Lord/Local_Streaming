-- V1: Create movies table
-- This migration creates only the `movies` table.

CREATE TABLE IF NOT EXISTS movies (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  cover_url VARCHAR(500),
  video_url VARCHAR(500) NOT NULL,
  duration_minutes INTEGER,
  release_year INTEGER,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

