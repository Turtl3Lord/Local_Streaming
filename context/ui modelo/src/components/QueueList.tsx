import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FileVideo, X, CheckCircle2, Loader2 } from 'lucide-react';
import { ProgressBar } from './ProgressBar';
export interface QueueItem {
  id: string;
  name: string;
  size: string;
  progress: number;
  status: 'uploading' | 'completed' | 'error';
}
interface QueueListProps {
  items: QueueItem[];
}
export function QueueList({
  items
}: QueueListProps) {
  if (items.length === 0) {
    return <div className="flex flex-col items-center justify-center h-full text-gray-400">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <Loader2 className="w-8 h-8 animate-spin-slow" />
        </div>
        <p className="text-lg font-medium text-gray-500">No active uploads</p>
        <p className="text-sm">Files you upload will appear here</p>
      </div>;
  }
  return <div className="max-w-3xl mx-auto py-8 px-4">
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-2xl font-semibold text-gray-900">Upload Queue</h2>
        <span className="text-sm font-medium text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
          {items.filter(i => i.status === 'uploading').length} active
        </span>
      </div>

      <div className="space-y-4">
        <AnimatePresence mode="popLayout">
          {items.map(item => <motion.div key={item.id} layout initial={{
          opacity: 0,
          y: 20
        }} animate={{
          opacity: 1,
          y: 0
        }} exit={{
          opacity: 0,
          scale: 0.95
        }} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center gap-4">
              <div className="w-12 h-12 bg-blue-50 rounded-lg flex items-center justify-center flex-shrink-0 text-blue-500">
                <FileVideo className="w-6 h-6" />
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex justify-between items-center mb-2">
                  <h4 className="font-medium text-gray-900 truncate">
                    {item.name}
                  </h4>
                  <span className="text-xs font-medium text-gray-500">
                    {item.status === 'completed' ? 'Done' : `${Math.round(item.progress)}%`}
                  </span>
                </div>

                {item.status === 'completed' ? <div className="flex items-center gap-1.5 text-xs font-medium text-green-600">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    <span>Upload complete</span>
                  </div> : <ProgressBar progress={item.progress} />}
              </div>

              <button className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-50 rounded-lg transition-colors">
                <X className="w-5 h-5" />
              </button>
            </motion.div>)}
        </AnimatePresence>
      </div>
    </div>;
}