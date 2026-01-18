import  { useState, useRef } from 'react';
import { motion } from 'framer-motion';
import { UploadCloud, FileVideo } from 'lucide-react';

interface UploadZoneProps {
  onFilesSelected: (files: File[]) => void;
}

export function UploadZone({
  onFilesSelected
}: UploadZoneProps) {
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const videoFiles = Array.from(e.dataTransfer.files).filter(file =>
        file.type.startsWith('video/')
      );
      if (videoFiles.length > 0) {
        onFilesSelected(videoFiles);
      }
    }
  };

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const videoFiles = Array.from(e.target.files).filter(file =>
        file.type.startsWith('video/')
      );
      if (videoFiles.length > 0) {
        onFilesSelected(videoFiles);
      }
    }
  };

  return (
    <div className="w-full h-full flex flex-col items-center justify-center p-8">
      <motion.div
        onClick={handleClick}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        className={`
          relative w-full max-w-2xl aspect-[4/3] md:aspect-[16/9] 
          rounded-3xl border-2 border-dashed transition-all duration-300 cursor-pointer
          flex flex-col items-center justify-center gap-6 p-12
          ${isDragging ? 'border-blue-500 bg-blue-50/50 scale-[1.02]' : 'border-gray-200 hover:border-blue-400 hover:bg-gray-50'}
        `}
        layout
      >
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileInput}
          className="hidden"
          multiple
          accept="video/*"
        />

        <div
          className={`
          w-20 h-20 rounded-2xl flex items-center justify-center transition-colors duration-300
          ${isDragging ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-400 group-hover:text-blue-500'}
        `}
        >
          <UploadCloud className="w-10 h-10" />
        </div>

        <div className="text-center space-y-2">
          <h2 className="text-2xl font-semibold text-gray-900">
            {isDragging ? 'Drop videos to upload' : 'Upload Videos'}
          </h2>
          <p className="text-gray-500 text-lg">
            Drag and drop files here, or{' '}
            <span className="text-blue-600 font-medium">browse</span>
          </p>
        </div>

        <div className="mt-4 flex items-center gap-8 text-sm text-gray-400">
          <div className="flex items-center gap-2">
            <FileVideo className="w-4 h-4" />
            <span>MP4, MOV, WEBM</span>
          </div>
          <div className="w-1 h-1 bg-gray-300 rounded-full" />
          <span>Up to 2GB</span>
        </div>
      </motion.div>
    </div>
  );
}
