export type View = 'upload' | 'queue' | 'library';

export interface MovieDTO {
  id: string;
  title: string;
  description?: string;
  coverUrl?: string;
  videoUrl?: string;
  durationMinutes?: number;
  releaseYear?: number;
  createdAt?: string;
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
