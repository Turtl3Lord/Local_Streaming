import { motion } from 'framer-motion';
import { Upload, ListVideo, LayoutGrid } from 'lucide-react';
import { View } from '@/shared/types';

interface SidebarProps {
  currentView: View;
  onChangeView: (view: View) => void;
  queueCount: number;
}

export function Sidebar({
  currentView,
  onChangeView,
  queueCount
}: SidebarProps) {
  const menuItems = [
    {
      id: 'upload' as const,
      label: 'Upload',
      icon: Upload
    },
    {
      id: 'queue' as const,
      label: 'Queue',
      icon: ListVideo,
      badge: queueCount
    },
    {
      id: 'library' as const,
      label: 'Library',
      icon: LayoutGrid
    }
  ];

  return (
    <aside className="w-64 bg-white border-r border-gray-100 flex flex-col h-full flex-shrink-0 z-20">
      <div className="p-8">
        <div className="flex items-center gap-3 text-blue-600 mb-8">
          <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
            <div className="w-4 h-4 border-2 border-white rounded-full border-t-transparent" />
          </div>
          <span className="font-bold text-xl tracking-tight text-gray-900">
            StreamFlow
          </span>
        </div>

        <nav className="space-y-1">
          {menuItems.map((item) => {
            const isActive = currentView === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onChangeView(item.id)}
                className={`
                  w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200 relative group
                  ${isActive ? 'text-blue-600 bg-blue-50' : 'text-gray-500 hover:text-gray-900 hover:bg-gray-50'}
                `}
              >
                {isActive && (
                  <motion.div
                    layoutId="activeTab"
                    className="absolute left-0 w-1 h-6 bg-blue-600 rounded-r-full"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                  />
                )}
                <item.icon
                  className={`w-5 h-5 ${isActive ? 'text-blue-600' : 'text-gray-400 group-hover:text-gray-600'}`}
                />
                <span>{item.label}</span>
                {item.badge ? (
                  <span className="ml-auto bg-blue-100 text-blue-600 py-0.5 px-2 rounded-full text-xs font-bold">
                    {item.badge}
                  </span>
                ) : null}
              </button>
            );
          })}
        </nav>
      </div>
    </aside>
  );
}
