import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sidebar } from './components/Sidebar';
import { UploadZone } from './components/UploadZone';
import { UploadForm } from './components/UploadForm';
import { QueueList } from './components/QueueList';
import { LibraryGrid, LibraryItem } from './components/LibraryGrid';
import { useUpload } from './hooks/useUpload';
import { useProcessingQueue } from './hooks/useProcessingQueue';
import { useQueueStore } from './store/queueStore';
import { View, MovieDTO } from './types';

const colors = [
  'bg-red-100', 'bg-orange-100', 'bg-amber-100', 'bg-yellow-100',
  'bg-lime-100', 'bg-green-100', 'bg-emerald-100', 'bg-teal-100',
  'bg-cyan-100', 'bg-sky-100', 'bg-blue-100', 'bg-indigo-100',
  'bg-violet-100', 'bg-purple-100', 'bg-fuchsia-100', 'bg-pink-100', 'bg-rose-100'
];

export function App() {
  const [currentView, setCurrentView] = useState<View>('upload');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [library, setLibrary] = useState<LibraryItem[]>([]);
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

  // Mover itens completados para a biblioteca
  useEffect(() => {
    const completedItems = queue.filter((item) => item.status === 'completed');
    
    completedItems.forEach((item) => {
      // Verificar se já não está na biblioteca
      if (!library.find((libItem) => libItem.id === item.id)) {
        const newLibraryItem: LibraryItem = {
          id: item.id,
          name: item.name,
          size: item.size,
          date: 'Just now',
          color: colors[Math.floor(Math.random() * colors.length)],
        };
        setLibrary((prev) => [newLibraryItem, ...prev]);
      }
    });
  }, [queue, library]);

  return (
    <div className="flex h-screen w-full bg-[#F9FAFB] text-gray-900 font-sans overflow-hidden">
      <Sidebar
        currentView={currentView}
        onChangeView={setCurrentView}
        queueCount={getActiveCount()}
      />

      <main className="flex-1 h-full overflow-y-auto relative">
        <header className="sticky top-0 z-10 bg-[#F9FAFB]/80 backdrop-blur-md px-8 py-4 flex justify-between items-center">
          <h1 className="text-xl font-semibold text-gray-900 capitalize">
            {currentView === 'upload' ? 'Upload Videos' : currentView}
          </h1>
          <div className="flex items-center gap-4">
            <button className="w-8 h-8 rounded-full bg-white border border-gray-200 flex items-center justify-center text-gray-500 hover:text-gray-900 transition-colors relative">
              <span className="sr-only">Notifications</span>
              <div className="w-2 h-2 bg-red-500 rounded-full absolute top-0 right-0 translate-x-1/4 -translate-y-1/4" />
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                />
              </svg>
            </button>
          </div>
        </header>

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
                <LibraryGrid items={library} />
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
