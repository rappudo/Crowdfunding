# 🚀 Plataforma de Crowdfunding - Microsserviços

> Plataforma distribuída de crowdfunding construída com arquitetura de microsserviços, Spring Boot, Docker e CI/CD via GitHub Actions.

[![Build Status](https://github.com/rappudo/Crowdfunding/actions/workflows/ci.yml/badge.svg)](https://github.com/rappudo/Crowdfunding/actions)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Serviços Disponíveis](#serviços-disponíveis)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Começando](#começando)
  - [Pré-requisitos](#pré-requisitos)
  - [Executando Localmente com Gradle](#executando-localmente-com-gradle)
  - [Executando com Docker Compose](#executando-com-docker-compose)
- [Testando na Produção (Render)](#testando-na-produção-render)
- [Exemplos de Requisições](#exemplos-de-requisições)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [CI/CD](#cicd)
- [Contribuindo](#contribuindo)

---

## 🎯 Sobre o Projeto

Esta é uma plataforma simplificada de crowdfunding distribuída em múltiplos microsserviços independentes. O projeto demonstra:

- ✅ **Arquitetura de Microsserviços** - Cada serviço tem responsabilidade única e bem definida
- ✅ **Comunicação via REST APIs** - Integração entre serviços usando HTTP/JSON
- ✅ **Containerização com Docker** - Cada microsserviço roda em seu próprio container
- ✅ **Testes Automatizados** - Testes unitários garantindo qualidade do código
- ✅ **CI/CD com GitHub Actions** - Build e testes automáticos a cada push
- ✅ **Persistência Simples** - Dados armazenados em arquivos JSON (desenvolvimento) ou banco de dados (produção)
- ✅ **Deploy na Nuvem** - Serviços hospedados no Render.com com alta disponibilidade

---

## 🏗️ Arquitetura

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Usuários   │     │  Campanhas   │     │  Pagamentos  │
│   :8084      │◄───►│   :8080      │◄───►│   :8082      │
└──────────────┘     └──────────────┘     └──────────────┘
                            │
                            ▼
                     ┌──────────────┐     ┌──────────────┐
                     │ Comentários  │     │ Recompensas  │
                     │   :8081      │     │   :8083      │
                     └──────────────┘     └──────────────┘
```

Cada microsserviço é **independente**, possui sua própria base de dados e pode ser desenvolvido, testado e deployado separadamente.

---

## 🌐 Serviços Disponíveis

| Serviço | Descrição | Porta Local | URL Produção (Render) |
|---------|-----------|-------------|----------------------|
| **Campanhas** | Gerenciamento de campanhas de financiamento | 8080 | [crowdfunding-b7hh.onrender.com](https://crowdfunding-b7hh.onrender.com) |
| **Comentários** | Sistema de comentários nas campanhas | 8081 | [comentario-ebzk.onrender.com](https://comentario-ebzk.onrender.com) |
| **Pagamentos** | Processamento de doações e pagamentos | 8082 | [pagamentos-4e9r.onrender.com](https://pagamentos-4e9r.onrender.com) |
| **Recompensas** | Gestão de recompensas para apoiadores | 8083 | [recompensas.onrender.com](https://recompensas.onrender.com) |
| **Usuários** | Autenticação e perfis de usuários | 8084 | [usuarios-lcvs.onrender.com](https://usuarios-lcvs.onrender.com) |

---

## 🛠️ Tecnologias Utilizadas

- **Backend:** Java 17, Spring Boot 3.x, Spring Web
- **Build Tool:** Gradle (Kotlin DSL)
- **Persistência:** Arquivos JSON (dev) / PostgreSQL (prod)
- **Containerização:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Cloud:** Render.com
- **Testes:** JUnit 5, Spring Boot Test
- **Documentação:** Markdown, OpenAPI (futuro)

---

## 🚀 Começando

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Docker** - [Download](https://www.docker.com/get-started)
- **Docker Compose** - Geralmente vem com Docker Desktop
- **Git** - [Download](https://git-scm.com/downloads)

Verifique as instalações:
```bash
java -version    # Deve mostrar Java 17+
docker --version
docker-compose --version
git --version
```

---

### 📦 Executando Localmente com Gradle

#### 1. Clone o repositório

```bash
git clone https://github.com/rappudo/Crowdfunding.git
cd Crowdfunding
```

#### 2. Execute um serviço específico

**Exemplo: Serviço de Campanhas**

```bash
# Navegue até a pasta do serviço
cd campanhas

# Compile o projeto e rode os testes
./gradlew clean build

# Execute o serviço
./gradlew bootRun
```

O serviço estará disponível em `http://localhost:8080`

#### 3. Rodando múltiplos serviços simultaneamente

Para evitar conflito de portas, você pode:

**Opção A: Usar terminais separados e configurar portas diferentes**

Terminal 1 - Campanhas (porta 8080):
```bash
cd campanhas
./gradlew bootRun
```

Terminal 2 - Usuários (porta 8084):
```bash
cd usuarios
./gradlew bootRun --args='--server.port=8084'
```

**Opção B: Usar Docker Compose (recomendado)**

---

### 🐳 Executando com Docker Compose

O Docker Compose permite rodar **todos os serviços simultaneamente** com um único comando.

#### 1. Certifique-se de estar na raiz do projeto

```bash
cd Crowdfunding
```

#### 2. Construa as imagens Docker

```bash
docker-compose build
```

Este comando irá:
- Ler o `docker-compose.yml`
- Construir as imagens Docker de cada microsserviço
- Preparar a rede interna para comunicação entre serviços

#### 3. Inicie todos os serviços

```bash
docker-compose up
```

Ou para rodar em background (modo detached):
```bash
docker-compose up -d
```

#### 4. Verifique os containers em execução

```bash
docker-compose ps
```

Saída esperada:
```
NAME                    STATUS    PORTS
campanhas-service       Up        0.0.0.0:8080->8080/tcp
usuarios-service        Up        0.0.0.0:8084->8084/tcp
pagamentos-service      Up        0.0.0.0:8082->8082/tcp
comentarios-service     Up        0.0.0.0:8081->8081/tcp
recompensas-service     Up        0.0.0.0:8083->8083/tcp
```

#### 5. Visualize os logs

**Todos os serviços:**
```bash
docker-compose logs -f
```

**Serviço específico:**
```bash
docker-compose logs -f campanhas
```

#### 6. Parar os serviços

```bash
docker-compose down
```

Para remover volumes e dados persistidos:
```bash
docker-compose down -v
```

---

### 🔨 Construindo Imagens Docker Individuais

Se preferir construir e rodar serviços individualmente:

```bash
# Navegue até o serviço
cd campanhas

# Construa a imagem
docker build -t crowdfunding-campanhas:latest .

# Execute o container
docker run -p 8080:8080 \
  -e CAMPANHA_JSON_PATH=/tmp/campanhas.json \
  crowdfunding-campanhas:latest

# Verifique se está rodando
docker ps

# Acesse os logs
docker logs <container_id>
```

---

## 🌍 Testando na Produção (Render)

Todos os microsserviços estão deployados no Render e podem ser testados publicamente.

### URLs Base

```bash
CAMPANHAS_URL="https://crowdfunding-b7hh.onrender.com"
COMENTARIOS_URL="https://comentario-ebzk.onrender.com"
PAGAMENTOS_URL="https://pagamentos-4e9r.onrender.com"
RECOMPENSAS_URL="https://recompensas.onrender.com"
USUARIOS_URL="https://usuarios-lcvs.onrender.com"
```

### Health Check

Teste se os serviços estão online:

```bash
curl https://crowdfunding-b7hh.onrender.com/campanhas
curl https://usuarios-lcvs.onrender.com/usuarios
curl https://comentario-ebzk.onrender.com/comentarios
curl https://pagamentos-4e9r.onrender.com/pagamentos
curl https://recompensas.onrender.com/recompensas
```

⚠️ **Nota:** Serviços no Render (free tier) podem levar ~30-60 segundos para "acordar" se estiverem inativos.

---

## 📡 Exemplos de Requisições

### 🎯 Serviço de Campanhas

#### **GET** - Listar todas as campanhas
```bash
curl -X GET https://crowdfunding-b7hh.onrender.com/campanhas
```

#### **GET** - Buscar campanha por ID
```bash
curl -X GET https://crowdfunding-b7hh.onrender.com/campanhas/1
```

#### **POST** - Criar nova campanha
```bash
curl -X POST https://crowdfunding-b7hh.onrender.com/campanhas \
  -H "Content-Type: application/json" \
  -d '{
    "idCriador": 5,
    "titulo": "Projeto Tech na Comunidade",
    "descricao": "Levar educação em tecnologia e programação para jovens de comunidades periféricas.",
    "meta": 25000.00,
    "valorArrecadado": 0.00,
    "dataCriacao": "2025-11-26T10:30:00",
    "dataEncerramento": "2026-06-30T23:59:59",
    "status": 1
  }'
```

#### **PUT** - Atualizar campanha
```bash
curl -X PUT https://crowdfunding-b7hh.onrender.com/campanhas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "idCriador": 5,
    "titulo": "Projeto Tech Atualizado",
    "descricao": "Descrição atualizada da campanha.",
    "meta": 30000.00,
    "valorArrecadado": 5000.00,
    "dataCriacao": "2025-11-26T10:30:00",
    "dataEncerramento": "2026-12-31T23:59:59",
    "status": 1
  }'
```

#### **DELETE** - Remover campanha
```bash
curl -X DELETE https://crowdfunding-b7hh.onrender.com/campanhas/1
```

#### **POST** - Fazer doação
```bash
curl -X POST https://crowdfunding-b7hh.onrender.com/campanhas/1/doar \
  -H "Content-Type: application/json" \
  -d '500.00'
```

---

### 👤 Serviço de Usuários

#### **GET** - Listar todos os usuários
```bash
curl -X GET https://usuarios-lcvs.onrender.com/usuarios
```

#### **GET** - Buscar usuário por ID
```bash
curl -X GET https://usuarios-lcvs.onrender.com/usuarios/1
```

#### **POST** - Criar novo usuário
```bash
curl -X POST https://usuarios-lcvs.onrender.com/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com",
    "senha": "senha123",
    "dataCadastro": "2025-11-26T10:00:00"
  }'
```

#### **PUT** - Atualizar usuário
```bash
curl -X PUT https://usuarios-lcvs.onrender.com/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Santos",
    "email": "joao.santos@email.com",
    "senha": "novaSenha456",
    "dataCadastro": "2025-11-26T10:00:00"
  }'
```

#### **DELETE** - Remover usuário
```bash
curl -X DELETE https://usuarios-lcvs.onrender.com/usuarios/1
```

---

### 💬 Serviço de Comentários

#### **GET** - Listar todos os comentários
```bash
curl -X GET https://comentario-ebzk.onrender.com/comentarios
```

#### **GET** - Buscar comentário por ID
```bash
curl -X GET https://comentario-ebzk.onrender.com/comentarios/1
```

#### **POST** - Criar comentário
```bash
curl -X POST https://comentario-ebzk.onrender.com/comentarios \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idCampanha": 1,
    "conteudo": "Projeto incrível! Muito sucesso!",
    "dataComentario": "2025-11-26T12:00:00"
  }'
```

#### **PUT** - Atualizar comentário
```bash
curl -X PUT https://comentario-ebzk.onrender.com/comentarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idCampanha": 1,
    "conteudo": "Comentário editado: Apoio total a essa iniciativa!",
    "dataComentario": "2025-11-26T12:00:00"
  }'
```

#### **DELETE** - Remover comentário
```bash
curl -X DELETE https://comentario-ebzk.onrender.com/comentarios/1
```

---

### 💰 Serviço de Pagamentos

#### **GET** - Listar todos os pagamentos
```bash
curl -X GET https://pagamentos-4e9r.onrender.com/pagamentos
```

#### **GET** - Buscar pagamento por ID
```bash
curl -X GET https://pagamentos-4e9r.onrender.com/pagamentos/1
```

#### **POST** - Criar pagamento
```bash
curl -X POST https://pagamentos-4e9r.onrender.com/pagamentos \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idCampanha": 1,
    "valor": 500.00,
    "metodoPagamento": "cartao_credito",
    "statusPagamento": "concluido",
    "dataPagamento": "2025-11-26T14:30:00"
  }'
```

#### **PUT** - Atualizar pagamento
```bash
curl -X PUT https://pagamentos-4e9r.onrender.com/pagamentos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idCampanha": 1,
    "valor": 500.00,
    "metodoPagamento": "cartao_credito",
    "statusPagamento": "estornado",
    "dataPagamento": "2025-11-26T14:30:00"
  }'
```

#### **DELETE** - Remover pagamento
```bash
curl -X DELETE https://pagamentos-4e9r.onrender.com/pagamentos/1
```

---

### 🎁 Serviço de Recompensas

#### **GET** - Listar todas as recompensas
```bash
curl -X GET https://recompensas.onrender.com/recompensas
```

#### **GET** - Buscar recompensa por ID
```bash
curl -X GET https://recompensas.onrender.com/recompensas/1
```

#### **POST** - Criar recompensa
```bash
curl -X POST https://recompensas.onrender.com/recompensas \
  -H "Content-Type: application/json" \
  -d '{
    "idCampanha": 1,
    "titulo": "Agradecimento Especial",
    "descricao": "Nome no site e certificado digital",
    "valorMinimo": 50.00,
    "quantidadeDisponivel": 100
  }'
```

#### **PUT** - Atualizar recompensa
```bash
curl -X PUT https://recompensas.onrender.com/recompensas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "idCampanha": 1,
    "titulo": "Agradecimento Premium",
    "descricao": "Nome no site, certificado e camiseta exclusiva",
    "valorMinimo": 100.00,
    "quantidadeDisponivel": 50
  }'
```

#### **DELETE** - Remover recompensa
```bash
curl -X DELETE https://recompensas.onrender.com/recompensas/1
```

---

## 📁 Estrutura do Projeto

```
Crowdfunding/
├── campanhas/                    # Microsserviço de Campanhas
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/eseg/campanhas/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   └── dto/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── data/
│   │   └── test/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── README.md
│
├── usuarios/                     # Microsserviço de Usuários
├── comentarios/                  # Microsserviço de Comentários
├── pagamentos/                   # Microsserviço de Pagamentos
├── recompensas/                  # Microsserviço de Recompensas
│
├── .github/
│   └── workflows/
│       └── ci.yml               # Pipeline CI/CD
│
├── docker-compose.yml           # Orquestração dos containers
├── settings.gradle.kts          # Configuração multi-projeto
├── build.gradle.kts             # Build global
└── README.md                    # Este arquivo
```

---

## 🔄 CI/CD

O projeto utiliza **GitHub Actions** para automação de build e testes.

### Pipeline Configurado

- ✅ **Build automático** a cada push
- ✅ **Execução de testes unitários**
- ✅ **Validação do código**
- ✅ **Notificação de falhas**

### Visualizar Status

Acesse a aba **Actions** no GitHub: [github.com/rappudo/Crowdfunding/actions](https://github.com/rappudo/Crowdfunding/actions)

### Executar Testes Localmente

```bash
# Testar todos os serviços
./gradlew test

# Testar serviço específico
cd campanhas
./gradlew test

# Gerar relatório de cobertura
./gradlew test jacocoTestReport
```
---

## 📄 Licença

Este projeto é de código aberto para fins educacionais.

---

## 👨‍💻 Autor

**Desenvolvido por:** [rappudo](https://github.com/rappudo)

**Repositório:** [github.com/rappudo/Crowdfunding](https://github.com/rappudo/Crowdfunding)


---

## 🎓 Aprendizados

Este projeto demonstra conceitos fundamentais de:

- Arquitetura de Microsserviços
- RESTful APIs
- Containerização com Docker
- CI/CD com GitHub Actions
- Versionamento com Git
- Deploy em Cloud (Render)
- Boas práticas de desenvolvimento Java/Spring Boot

---
