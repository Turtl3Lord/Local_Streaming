import { motion } from 'framer-motion';
import { FileVideo, X, CheckCircle2, Loader2, AlertCircle } from 'lucide-react';
import { ProgressBar } from './ProgressBar';
import { QueueItem as QueueItemType } from '../types';

interface QueueItemProps {
  item: QueueItemType;
  onCancel?: (id: string) => void;
}

export function QueueItem({ item, onCancel }: QueueItemProps) {
  const getStatusIcon = () => {
    switch (item.status) {
      case 'completed':
        return <CheckCircle2 className="w-6 h-6 text-green-600" />;
      case 'error':
        return <AlertCircle className="w-6 h-6 text-red-600" />;
      case 'processing':
        return <Loader2 className="w-6 h-6 text-blue-600 animate-spin" />;
      default:
        return <FileVideo className="w-6 h-6 text-blue-500" />;
    }
  };

  const getStatusText = () => {
    switch (item.status) {
      case 'uploading':
        return 'Enviando...';
      case 'processing':
        return 'Processando...';
      case 'completed':
        return 'Concluído';
      case 'error':
        return 'Erro';
      default:
        return '';
    }
  };

  const getStatusColor = () => {
    switch (item.status) {
      case 'completed':
        return 'text-green-600';
      case 'error':
        return 'text-red-600';
      case 'processing':
        return 'text-blue-600';
      default:
        return 'text-blue-500';
    }
  };

  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.95 }}
      className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center gap-4"
    >
      <div className={`w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0 ${
        item.status === 'completed' ? 'bg-green-50' :
        item.status === 'error' ? 'bg-red-50' :
        item.status === 'processing' ? 'bg-blue-50' :
        'bg-blue-50'
      }`}>
        {getStatusIcon()}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex justify-between items-center mb-2">
          <h4 className="font-medium text-gray-900 truncate">
            {item.name}
          </h4>
          <span className={`text-xs font-medium ${getStatusColor()}`}>
            {item.status === 'completed' ? 'Concluído' : 
             item.status === 'error' ? 'Erro' :
             item.status === 'processing' ? 'Processando' :
             `${Math.round(item.progress)}%`}
          </span>
        </div>

        {item.status === 'completed' ? (
          <div className="flex items-center gap-1.5 text-xs font-medium text-green-600">
            <CheckCircle2 className="w-3.5 h-3.5" />
            <span>Upload completo</span>
          </div>
        ) : item.status === 'error' ? (
          <div className="flex items-center gap-1.5 text-xs font-medium text-red-600">
            <AlertCircle className="w-3.5 h-3.5" />
            <span>{item.errorMessage || 'Erro no processamento'}</span>
          </div>
        ) : (
          <ProgressBar progress={item.progress} />
        )}

        {item.status !== 'completed' && item.status !== 'error' && (
          <p className="mt-1 text-xs text-gray-500">{getStatusText()}</p>
        )}
      </div>

      {item.status === 'uploading' && onCancel && (
        <button
          onClick={() => onCancel(item.id)}
          className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      )}
    </motion.div>
  );
}
