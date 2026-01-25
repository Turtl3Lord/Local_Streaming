import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

// Layout
import { Sidebar, Header } from '@/shared/components';

// Features
import { UploadZone, UploadForm, useUpload } from '@/features/upload';
import { QueueList, useProcessingQueue, useQueueStore } from '@/features/queue';
import { LibraryGrid } from '@/features/library';


import { View, MovieDTO } from '@/shared/types';


export function App() {
  const [currentView, setCurrentView] = useState<View>('upload');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const { uploadMovie } = useUpload();
  const { queue, removeItem } = useProcessingQueue();
  const { getActiveCount } = useQueueStore();

  const handleFilesSelected = (files: File[]) => {
    if (files.length > 0) {
      setSelectedFile(files[0]);
    }
  };

  const handleFormSubmit = async (movieData: MovieDTO, file: File) => {
    await uploadMovie(movieData, file);
    setSelectedFile(null);
    setCurrentView('queue');
  };

  const handleFormClose = () => {
    setSelectedFile(null);
  };

  const handleCancelUpload = (id: string) => {
    removeItem(id);
  };

  

  const getHeaderTitle = () => {
    return currentView === 'upload' ? 'Upload Videos' : currentView;
  };

  return (
    <div className="flex h-screen w-full bg-[#F9FAFB] text-gray-900 font-sans overflow-hidden">
      <Sidebar
        currentView={currentView}
        onChangeView={setCurrentView}
        queueCount={getActiveCount()}
      />

      <main className="flex-1 h-full overflow-y-auto relative">
        <Header title={getHeaderTitle()} />

        <div className="h-[calc(100%-4rem)]">
          <AnimatePresence mode="wait">
            {currentView === 'upload' && (
              <motion.div
                key="upload"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
                className="h-full"
              >
                <UploadZone onFilesSelected={handleFilesSelected} />
              </motion.div>
            )}

            {currentView === 'queue' && (
              <motion.div
                key="queue"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
                className="h-full"
              >
                <QueueList items={queue} onCancel={handleCancelUpload} />
              </motion.div>
            )}

            {currentView === 'library' && (
              <motion.div
                key="library"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.2 }}
                className="h-full"
              >
                <LibraryGrid />
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </main>

      {selectedFile && (
        <UploadForm
          file={selectedFile}
          onClose={handleFormClose}
          onSubmit={handleFormSubmit}
        />
      )}
    </div>
  );
}
