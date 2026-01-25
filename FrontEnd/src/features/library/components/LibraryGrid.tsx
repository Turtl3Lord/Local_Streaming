import { motion } from 'framer-motion';
import { VideoCard } from './VideoCard';
import { Film } from 'lucide-react';
import { LibraryItem, MovieDTO } from '@/shared/types';
import { apiService } from '@/shared/services/api';
import { useEffect, useState } from 'react';



export function LibraryGrid() {
  const [movies, setMovies] = useState<MovieDTO[]>([]);
  useEffect(() => {
    const fetchMovies = async () => {
      const movies = await getAllMovies();
      setMovies(movies);
    };
    fetchMovies();
  }, []);

  for (const movie of movies) {
    console.log(movie);
  }

  if (movies.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full text-gray-400">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Film className="w-8 h-8" />
        </div>
        <p className="text-lg font-medium text-gray-500">Library is empty</p>
        <p className="text-sm">Uploaded videos will appear here</p>
      </div>
    );
  }

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-2xl font-semibold text-gray-900">Your Library</h2>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {movies.map((item, index) => (
          <motion.div
            key={item.id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
          >
            <VideoCard
              title={item.title}
              coverUrl={item.coverUrl!}
              date={item.createdAt!}
            
            />
          </motion.div>
        ))}
      </div>
    </div>
  );
}


async function getAllMovies() {
  const response = await apiService.getMovies();
  return response;
}
