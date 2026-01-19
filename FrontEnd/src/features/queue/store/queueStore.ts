import { create } from 'zustand';
import { QueueItem } from '@/shared/types';

interface QueueStore {
  queue: QueueItem[];
  addToQueue: (item: QueueItem) => void;
  updateItem: (id: string, updates: Partial<QueueItem>) => void;
  removeItem: (id: string) => void;
  getActiveCount: () => number;
}

export const useQueueStore = create<QueueStore>((set, get) => ({
  queue: [],

  addToQueue: (item) => {
    set((state) => ({
      queue: [...state.queue, item],
    }));
  },

  updateItem: (id, updates) => {
    set((state) => ({
      queue: state.queue.map((item) =>
        item.id === id ? { ...item, ...updates } : item
      ),
    }));
  },

  removeItem: (id) => {
    set((state) => ({
      queue: state.queue.filter((item) => item.id !== id),
    }));
  },

  getActiveCount: () => {
    const state = get();
    return state.queue.filter(
      (item) => item.status === 'uploading' || item.status === 'processing'
    ).length;
  },
}));
