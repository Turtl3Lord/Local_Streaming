import { motion } from 'framer-motion';

interface ProgressBarProps {
  progress: number;
  className?: string;
}

export function ProgressBar({
  progress,
  className = ''
}: ProgressBarProps) {
  return (
    <div className={`h-2 w-full bg-gray-100 rounded-full overflow-hidden ${className}`}>
      <motion.div
        className="h-full bg-blue-500 rounded-full"
        initial={{ width: 0 }}
        animate={{ width: `${progress}%` }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
      />
    </div>
  );
}
