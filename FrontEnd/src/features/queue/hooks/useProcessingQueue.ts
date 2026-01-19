import { useEffect } from 'react';
import { useQueueStore } from '../store/queueStore';

export function useProcessingQueue() {
  const { queue, updateItem, removeItem } = useQueueStore();

  // Polling para verificar status dos itens em processamento
  useEffect(() => {
    const itemsInProcessing = queue.filter(
      (item) => item.status === 'processing' || item.status === 'uploading'
    );

    if (itemsInProcessing.length === 0) {
      return;
    }

    // Em produção, isso seria uma chamada real à API para verificar o status
    // Por enquanto, vamos apenas simular o progresso
    const interval = setInterval(() => {
      itemsInProcessing.forEach((item) => {
        if (item.status === 'processing' && item.progress < 100) {
          updateItem(item.id, {
            progress: Math.min(item.progress + 2, 100),
          });

          // Se chegou a 100%, marcar como completo
          if (item.progress >= 98) {
            setTimeout(() => {
              updateItem(item.id, {
                status: 'completed',
                progress: 100,
              });
            }, 1000);
          }
        }
      });
    }, 2000);

    return () => clearInterval(interval);
  }, [queue, updateItem]);

  // Remover itens completados automaticamente após 5 segundos
  useEffect(() => {
    const completedItems = queue.filter((item) => item.status === 'completed');

    completedItems.forEach((item) => {
      const timeout = setTimeout(() => {
        removeItem(item.id);
      }, 5000);

      return () => clearTimeout(timeout);
    });
  }, [queue, removeItem]);

  return {
    queue,
    removeItem,
  };
}
