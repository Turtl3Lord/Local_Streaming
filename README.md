# Servidor de Streaming Local

[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/Turtl3Lord/Local_Streaming)

Este repositório contém o serviço de backend para uma plataforma local de streaming de mídia. Trata-se de uma aplicação Spring Boot escrita em Java que gerencia uma biblioteca de mídia e converte arquivos de vídeo para o formato HTTP Live Streaming (HLS) para um streaming eficiente e adaptativo.

## Funcionalidades

* **Gerenciamento de Biblioteca de Mídia**: Fornece um modelo de dados e uma camada de persistência para organizar mídias em Séries, Temporadas, Episódios e Filmes independentes.
* **Conversão de Vídeo para HLS**: Utiliza o JavaCV (um wrapper Java para o FFmpeg) para transcodificar automaticamente arquivos de vídeo para o formato HLS, criando playlists `.m3u8` e segmentos de vídeo `.ts`.
* **Integração com Banco de Dados**: Usa Spring Data JPA e PostgreSQL para armazenar e gerenciar metadados de mídia.
* **Arquitetura RESTful**: Construído com Spring WebMVC para expor funcionalidades de gerenciamento de mídia (embora os controllers não estejam definidos neste escopo).

## Componentes Principais

* **`HLSConverter.java`**: O serviço central responsável por converter um arquivo de vídeo de entrada para HLS. Ele lida com a segmentação do vídeo, a definição dos codecs apropriados e a geração da playlist mestre.
* **Modelos JPA**:

  * `Series`: Representa uma série de TV.
  * `Seasons`: Representa uma temporada dentro de uma série.
  * `Episodes`: Representa um episódio individual dentro de uma temporada.
  * `Movies`: Representa um filme independente.
* **Repositórios JPA**: Repositórios Spring Data (`SeriesRepository`, `MoviesRepository`, etc.) fornecem uma camada de abstração para interações com o banco de dados.

## Pré-requisitos

* **Java 17** ou superior.
* **Maven** para gerenciamento de dependências e build do projeto.
* **PostgreSQL**: Uma instância em execução de um banco de dados PostgreSQL.
* **FFmpeg**: As bibliotecas nativas do FFmpeg devem estar instaladas no sistema onde a aplicação será executada, pois o JavaCV depende delas para o processamento de vídeo.

## Primeiros Passos

Siga estes passos para configurar e executar a aplicação localmente.

### 1. Clonar o Repositório

```bash
git clone https://github.com/turtl3lord/local_streaming.git
cd local_streaming/LocalServer/api/server
```

### 2. Configurar o Banco de Dados

Abra o arquivo `src/main/resources/application.properties` e adicione os detalhes de conexão do seu banco de dados PostgreSQL.

```properties
spring.application.name=server

# Configuração do DataSource PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuração JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build da Aplicação

Use o Maven Wrapper para buildar o projeto. Isso fará o download de todas as dependências necessárias.

```bash
./mvnw clean install
```

### 4. Executar a Aplicação

Após a conclusão do build, você pode executar a aplicação usando o seguinte comando:

```bash
./mvnw spring-boot:run
```

Alternativamente, você pode executar o arquivo JAR empacotado:

```bash
java -jar target/server-0.0.1-SNAPSHOT.jar
```

O servidor será iniciado na porta padrão (geralmente 8080).
