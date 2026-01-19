import axios, { AxiosInstance, AxiosError } from 'axios';
import { MovieDTO } from '@/shared/types';

const API_BASE_URL = 'http://localhost:8080';

class ApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 0, 
    });

    // Interceptor para tratamento de erros
    this.client.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response) {
          // Erro com resposta do servidor
          const status = error.response.status;
          const data = error.response.data as any;
          
          if (status === 400) {
            throw new Error(data.message || 'Erro de validação');
          } else if (status === 500) {
            throw new Error(data.message || 'Erro ao processar vídeo');
          } else {
            throw new Error(data.message || `Erro ${status}: ${error.message}`);
          }
        } else if (error.request) {
          // Erro de rede
          throw new Error('Erro de conexão com o servidor. Verifique se o servidor está rodando.');
        } else {
          // Outro erro
          throw new Error(error.message || 'Erro desconhecido');
        }
      }
    );
  }

  async createMovie(file: File, movieDTO: MovieDTO): Promise<MovieDTO> {
    try {
      // Criar FormData para enviar multipart/form-data
      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', movieDTO.title);
      
      if (movieDTO.description) {
        formData.append('description', movieDTO.description);
      }
      
      if (movieDTO.coverUrl) {
        formData.append('coverUrl', movieDTO.coverUrl);
      }
      
      if (movieDTO.durationMinutes !== undefined && movieDTO.durationMinutes !== null) {
        formData.append('durationMinutes', movieDTO.durationMinutes.toString());
      }
      
      if (movieDTO.releaseYear !== undefined && movieDTO.releaseYear !== null) {
        formData.append('releaseYear', movieDTO.releaseYear.toString());
      }
      
      const response = await this.client.post<MovieDTO>('/api/movies', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Erro ao criar filme');
    }
  }

  async getMovies(): Promise<MovieDTO[]> {
    try {
      const response = await this.client.get<MovieDTO[]>('/api/movies');
      return response.data;
    } catch (error) {
      if (error instanceof Error) {
        throw error;
      }
      throw new Error('Erro ao buscar filmes');
    }
  }
}

export const apiService = new ApiService();
