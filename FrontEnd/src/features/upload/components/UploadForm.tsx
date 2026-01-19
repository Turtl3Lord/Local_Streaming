import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X } from 'lucide-react';
import { MovieDTO } from '@/shared/types';

interface UploadFormProps {
  file: File;
  onClose: () => void;
  onSubmit: (data: MovieDTO, file: File) => void;
}

export function UploadForm({ file, onClose, onSubmit }: UploadFormProps) {
  const [formData, setFormData] = useState<MovieDTO>({
    title: file.name.replace(/\.[^/.]+$/, ''), // Remove extensão
    description: '',
    coverUrl: '',
    durationMinutes: undefined,
    releaseYear: undefined,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.title || formData.title.trim().length === 0) {
      newErrors.title = 'Título é obrigatório';
    }

    if (formData.durationMinutes !== undefined && formData.durationMinutes < 0) {
      newErrors.durationMinutes = 'Duração deve ser um valor positivo';
    }

    if (formData.releaseYear !== undefined) {
      const currentYear = new Date().getFullYear();
      if (formData.releaseYear < 1888 || formData.releaseYear > currentYear) {
        newErrors.releaseYear = `Ano deve estar entre 1888 e ${currentYear}`;
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validate()) {
      onSubmit(formData, file);
      onClose();
    }
  };

  const handleChange = (field: keyof MovieDTO, value: string | number | undefined) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Limpar erro do campo quando o usuário começar a digitar
    if (errors[field]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  return (
    <AnimatePresence>
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 0.95 }}
          className="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        >
          <div className="sticky top-0 bg-white border-b border-gray-100 px-6 py-4 flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Adicionar Metadados</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Arquivo
              </label>
              <div className="p-3 bg-gray-50 rounded-lg text-sm text-gray-600">
                {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Título <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => handleChange('title', e.target.value)}
                className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                  errors.title ? 'border-red-500' : 'border-gray-200'
                }`}
                placeholder="Digite o título do filme"
              />
              {errors.title && (
                <p className="mt-1 text-sm text-red-500">{errors.title}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Descrição
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => handleChange('description', e.target.value)}
                rows={3}
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Digite a descrição do filme"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                URL da Capa
              </label>
              <input
                type="url"
                value={formData.coverUrl}
                onChange={(e) => handleChange('coverUrl', e.target.value)}
                className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="https://example.com/capa.jpg"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Duração (minutos)
                </label>
                <input
                  type="number"
                  value={formData.durationMinutes || ''}
                  onChange={(e) =>
                    handleChange('durationMinutes', e.target.value ? parseInt(e.target.value) : undefined)
                  }
                  min="0"
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.durationMinutes ? 'border-red-500' : 'border-gray-200'
                  }`}
                  placeholder="120"
                />
                {errors.durationMinutes && (
                  <p className="mt-1 text-sm text-red-500">{errors.durationMinutes}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Ano de Lançamento
                </label>
                <input
                  type="number"
                  value={formData.releaseYear || ''}
                  onChange={(e) =>
                    handleChange('releaseYear', e.target.value ? parseInt(e.target.value) : undefined)
                  }
                  min="1888"
                  max={new Date().getFullYear()}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.releaseYear ? 'border-red-500' : 'border-gray-200'
                  }`}
                  placeholder="2023"
                />
                {errors.releaseYear && (
                  <p className="mt-1 text-sm text-red-500">{errors.releaseYear}</p>
                )}
              </div>
            </div>

            <div className="flex gap-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 px-4 py-2 border border-gray-200 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
              >
                Enviar
              </button>
            </div>
          </form>
        </motion.div>
      </div>
    </AnimatePresence>
  );
}
