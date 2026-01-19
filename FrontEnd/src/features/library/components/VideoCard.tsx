import { motion } from 'framer-motion';

interface VideoCardProps {
  title: string;
  size: string;
  thumbnailColor: string;
  date: string;
}

export function VideoCard({
  title,
  size,
  thumbnailColor,
  date
}: VideoCardProps) {
  return (
    <motion.div
      className="group relative bg-white rounded-xl overflow-hidden shadow-sm border border-gray-100 cursor-pointer"
      whileHover={{ y: -4, scale: 1.01 }}
      transition={{ type: 'spring', stiffness: 300, damping: 20 }}
    >
      {/* Thumbnail */}
      <div className={`aspect-video w-full ${thumbnailColor} relative flex items-center justify-center overflow-hidden`}>
        <div className="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors duration-300" />
      </div>

      {/* Info */}
      <div className="p-4">
        <div className="flex justify-between items-start mb-1">
          <h3 className="font-medium text-gray-900 truncate pr-4" title={title}>
            {title}
          </h3>
        </div>
        <div className="flex items-center text-xs text-gray-500 space-x-2">
          <span>{size}</span>
          <span>•</span>
          <span>{date}</span>
        </div>
      </div>
    </motion.div>
  );
}
