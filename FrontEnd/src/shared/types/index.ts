export type View = 'upload' | 'queue' | 'library';

export interface MovieDTO {
  title: string;
  description?: string;
  coverUrl?: string;
  durationMinutes?: number;
  releaseYear?: number;
}

export interface QueueItem {
  id: string;
  name: string;
  size: string;
  progress: number;
  status: 'uploading' | 'processing' | 'completed' | 'error';
  errorMessage?: string;
  movieData?: MovieDTO;
}

export interface LibraryItem {
  id: string;
  name: string;
  size: string;
  date: string;
  color: string;
}
