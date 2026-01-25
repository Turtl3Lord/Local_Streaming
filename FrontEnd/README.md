# Local Streaming Frontend

Frontend React + TypeScript para o sistema de streaming local.

## Tecnologias

- React 18
- TypeScript
- Vite
- Tailwind CSS
- Framer Motion
- Zustand (gerenciamento de estado)
- Axios (cliente HTTP)

## Instalação

```bash
npm install
```

## Configuração

Crie um arquivo `.env` na raiz do projeto com:

```
VITE_API_BASE_URL=http://localhost:8080
```

## Desenvolvimento

```bash
npm run dev
```

## Build

```bash
npm run build
```

## Funcionalidades

- Upload de vídeos (drag & drop ou seleção)
- Formulário de metadados
- Fila de processamento em tempo real
- Biblioteca de vídeos processados
- Integração com backend Spring Boot
