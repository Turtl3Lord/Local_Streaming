import { AnimatePresence } from 'framer-motion';
import { Loader2 } from 'lucide-react';
import { QueueItem } from './QueueItem';
import { QueueItem as QueueItemType } from '@/shared/types';

interface QueueListProps {
  items: QueueItemType[];
  onCancel?: (id: string) => void;
}

export function QueueList({ items, onCancel }: QueueListProps) {
  if (items.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full text-gray-400">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Loader2 className="w-8 h-8 animate-spin" />
        </div>
        <p className="text-lg font-medium text-gray-500">Nenhum upload ativo</p>
        <p className="text-sm">Os arquivos que você enviar aparecerão aqui</p>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-8 px-4">
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-2xl font-semibold text-gray-900">Fila de Processamento</h2>
        <span className="text-sm font-medium text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
          {items.filter(i => i.status === 'uploading' || i.status === 'processing').length} ativo(s)
        </span>
      </div>

      <div className="space-y-4">
        <AnimatePresence mode="popLayout">
          {items.map((item) => (
            <QueueItem key={item.id} item={item} onCancel={onCancel} />
          ))}
        </AnimatePresence>
      </div>
    </div>
  );
}
