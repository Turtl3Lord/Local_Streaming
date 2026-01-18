# Local Streaming - API (Servidor)

## 1. Escopo e objetivos

Local Streaming é uma plataforma local para hospedar e distribuir vídeos dentro da sua rede. O objetivo é permitir o envio de vídeos pelo frontend para a API Java, que converte os arquivos para HLS (segmentos .ts + playlist .m3u8) e os disponibiliza via Nginx em uma pasta local. Os metadados dos vídeos são salvos em um banco PostgreSQL.

Observação: o projeto ainda não está concluído. Algumas funcionalidades e endpoints estão parcialmente implementados ou marcados como TODO.

## 2. Começo rápido / o que configurar após `git clone`

1. Instale as ferramentas necessárias (Java, Maven, Node.js) conforme a seção "Configuração do ambiente" abaixo.
2. Copie e ajuste o arquivo `src/main/resources/application.properties` com as credenciais do seu banco local.
   - Propriedades relevantes:
     - `spring.datasource.url` (ex.: `jdbc:postgresql://localhost:5432/local_streaming`)
     - `spring.datasource.username`
     - `spring.datasource.password`
     - `spring.flyway.url`, `spring.flyway.user`, `spring.flyway.password` (podem apontar para o mesmo banco)
3. Crie o banco PostgreSQL (por exemplo `local_streaming`) e o usuário conforme `application.properties`.
4. Execute as migrações (veja seção de Migrações) ou deixe o Flyway rodar automaticamente ao iniciar a aplicação.

## 3. Configuração do ambiente

Ferramentas recomendadas (exemplos de versões):

- Java 17+
- Spring Boot 3.x
- Maven 3.8+
- Node.js 18+
- PostgreSQL 14 ou 15 — documentação: https://www.postgresql.org/download/
- Nginx (usado para servir os arquivos HLS gerados)
- Flyway (para migrações)

Verifique versões:

```bash
java -version
mvn -v
node -v
psql --version
```

## 4. Executando a aplicação localmente

1. Ajuste `src/main/resources/application.properties` com sua URL, usuário e senha do Postgres.
2. Para rodar o backend:

```bash
mvn spring-boot:run
```

3. (Frontend) Se houver um frontend neste repositório em outra pasta, siga seu README para instalar e executar (por exemplo `npm install && npm start`).

Observação: `application.properties` atual contém: 

```
spring.datasource.url=jdbc:postgresql://localhost:5432/local_streaming
spring.datasource.username=pedro
spring.datasource.password=meruem40d
spring.flyway.url=jdbc:postgresql://localhost:5432/local_streaming
spring.flyway.user=pedro
spring.flyway.password=meruem40d
```

Altere conforme seu ambiente.

## 5. Migrações de banco de dados

As migrações estão na pasta `src/main/resources/db/migration` e usam Flyway. A primeira migração `V1__create_movies.sql` cria a tabela `movies` com colunas compatíveis com a entidade `Movies`.

Você pode executar as migrações automaticamente ao subir a aplicação (o projeto já habilita `spring.flyway.enabled=true`), ou manualmente via código/CLI. Em código Java, basta instanciar `Flyway` e chamar `flyway.migrate()`.

## 6. Docker 

Há um `docker-compose.yml` para Nginx em `src/ngix/docker-compose.yml` que monta as pastas `midia` e `conf.d`:

- `src/ngix/docker-compose.yml` usa a imagem `nginx:stable` e expõe a porta 80.
- `src/ngix/conf.d/media.conf` configura o Nginx para servir `root /data/midia;` com `autoindex on;`.

Se preferir usar Docker para rodar o Nginx (útil para testes de integração):

```bash
cd src/ngix
docker compose up --build
```

OBS: Os testes de integração podem usar Testcontainers e exigir imagens específicas; consulte os testes antes de executar em CI.

## 7. Fluxo de envio / processamento

1. O frontend envia um arquivo de vídeo para o endpoint `POST /api/movies` (multipart/form-data).
2. O backend salva o arquivo temporariamente em `pending/`, gera um `MovieDTO` e chama o serviço de processamento (`MovieProcessingService`).
3. O serviço executa a conversão para HLS (ex.: usando `ffmpeg`), grava os arquivos HLS em `src/ngix/midia/movies/[movieName]/` e retorna a entidade `Movies` com `video_url` apontando para o local relativo.
4. O backend salva os metadados no PostgreSQL (tabela `movies`).
5. Outros endpoints permitem atualizar, deletar e (em futuro) enviar o vídeo para um requisitante.

Caminho dos arquivos HLS (exemplo):

```
src/ngix/midia/movies/1768748396331_2026-01-12 22-33-42/1768748396331_2026-01-12 22-33-42.m3u8
```

### Estrutura do envio (multipart/form-data)

O endpoint `POST /api/movies` espera um envio multipart/form-data com os seguintes `RequestPart` (nomes e tipos conforme a `LocalServerController`):

- `file` (obrigatório): o arquivo de vídeo (tipo multipart file)
- `title` (obrigatório): string
- `description` (opcional): string
- `coverUrl` (opcional): string (URL da capa)
- `durationMinutes` (opcional): integer (duração em minutos)
- `releaseYear` (opcional): integer (ano de lançamento)

Exemplo com `curl`:

```bash
curl -v -X POST "http://localhost:8080/api/movies" \
  -F "file=@/caminho/para/video.mp4" \
  -F "title=Meu Filme" \
  -F "description=Uma descrição curta" \
  -F "coverUrl=http://exemplo.com/capa.jpg" \
  -F "durationMinutes=120" \
  -F "releaseYear=2026"
```

Exemplo em JavaScript usando `fetch` e `FormData` (browser/Node com `form-data`):

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]); // arquivo File no browser
formData.append('title', 'Meu Filme');
formData.append('description', 'Uma descrição curta');
formData.append('coverUrl', 'http://exemplo.com/capa.jpg');
formData.append('durationMinutes', '120');
formData.append('releaseYear', '2026');

fetch('http://localhost:8080/api/movies', {
  method: 'POST',
  body: formData
}).then(res => res.json()).then(console.log).catch(console.error);
```

Observações:

- Os limites de upload estão configurados em `src/main/resources/application.properties`:

```
spring.servlet.multipart.max-file-size=2000MB
spring.servlet.multipart.max-request-size=2000MB
```

- O backend valida se o `file` é enviado e se o `Content-Type` começa com `video/`.
- Ao enviar, a API responde com o objeto `MovieDTO` criado (sem `id`/timestamps no DTO) ou com um erro em caso de validação/erro de processamento.

## 8. Endpoints: implementados vs faltando

Baseado no código fonte (classe `LocalServerController` em `src/main/java/com/StreamingServer/server/controllers/LocalServerController.java`):

Implementados:
- `POST /api/movies` — cria um novo filme a partir de multipart/form-data (arquivo + metadados).
- `PUT /api/movies/{id}` — atualiza metadados do filme.
- `DELETE /api/movies/{id}` — remove um filme do banco.
- `POST /api/movies/{id}/send-video` — endpoint reservado para "enviar o vídeo ao requisitante" (atualmente retorna 501 Not Implemented).

Faltando / TODOs (observados no código):
- Implementação efetiva do envio de vídeo para requisitante (`sendVideoToRequester`).
- Endpoints adicionais que o frontend possa requerer (listar filmes, buscar por id com GET `/api/movies/{id}` e listar todos) podem não estar presentes; é necessário conferir o frontend.

(Se desejar, posso varrer outras classes para montar uma lista completa de endpoints.)

## 9. Testes

- Os testes do projeto devem estar sob `src/test/java/...` (procure por pacotes com `test` no nome). No repositório atualmente não encontrei READMEs separados de frontend/backend nem testes de integração específicos com Testcontainers; se houver testes, execute:

```bash
mvn test
```

- Observação sobre Testcontainers: alguns testes podem subir containers (por exemplo Nginx) e requerer imagens específicas — leia os testes antes de rodá-los em máquinas locais.

## 10. Stack tecnológico

- Java 17+
- Spring Boot 3.x
- Maven 3.8+
- PostgreSQL 14+
- Nginx (serve HLS gerado)
- Flyway (migrações)
- ffmpeg 
