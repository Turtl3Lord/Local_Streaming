import { useState } from 'react';
import { apiService } from '../services/api';
import { MovieDTO } from '../types';
import { useQueueStore } from '../store/queueStore';

export function useUpload() {
  const [isUploading, setIsUploading] = useState(false);
  const { addToQueue, updateItem } = useQueueStore();

  const uploadMovie = async (movieDTO: MovieDTO, file: File) => {
    const itemId = `upload-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    const fileSize = `${(file.size / (1024 * 1024)).toFixed(1)} MB`;

    // Adicionar à fila com status uploading
    addToQueue({
      id: itemId,
      name: file.name,
      size: fileSize,
      progress: 0,
      status: 'uploading',
      movieData: movieDTO,
    });

    setIsUploading(true);

    try {
      // Simular progresso de upload (0-50%)
      let uploadProgress = 0;
      const uploadProgressInterval = setInterval(() => {
        uploadProgress = Math.min(uploadProgress + 10, 50);
        updateItem(itemId, {
          progress: uploadProgress,
        });
      }, 200);

      // Enviar para o backend com arquivo e metadados
      await apiService.createMovie(file, movieDTO);

      clearInterval(uploadProgressInterval);

      // Atualizar status para processing (backend está processando)
      updateItem(itemId, {
        status: 'processing',
        progress: 60,
      });

      // Simular progresso de processamento (60-100%)
      // Em produção, isso poderia ser feito via polling ou WebSocket
      let progressValue = 60;
      const processingInterval = setInterval(() => {
        progressValue = Math.min(progressValue + 5, 100);
        updateItem(itemId, {
          progress: progressValue,
        });

        if (progressValue >= 100) {
          clearInterval(processingInterval);
          updateItem(itemId, {
            status: 'completed',
            progress: 100,
          });
        }
      }, 500);

    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro desconhecido';
      updateItem(itemId, {
        status: 'error',
        errorMessage,
        progress: 0,
      });
    } finally {
      setIsUploading(false);
    }
  };

  return {
    uploadMovie,
    isUploading,
  };
}
