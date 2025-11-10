# Projeto Crowdfunding com Microsserviços

## Objetivo do Projeto

Este projeto é uma plataforma simplificada de crowdfunding distribuída em múltiplos microsserviços separados, com um exemplo prático da arquitetura moderna microserviços, utilizando:

- Microsserviços segregados por responsabilidade (ex: pagamentos, usuários, campanhas).
- Comunicação via APIs RESTful entre os microsserviços.
- Empacotamento e execução em containers Docker individuais.
- Testes unitários para garantir qualidade do código.
- Integração Contínua (CI) configurada via GitHub Actions para build e testes automáticos.
- Persistência simples via arquivos JSON para armazenar os dados das campanhas.
- Estrutura organizada para facilitar manutenção e entendimento.

Este trabalho visa demonstrar a separação clara dos serviços, garantir código testável e automatizar rotinas de build e deploy.

***

## Estrutura do Projeto

Cada microsserviço tem sua própria pasta com:

- Código fonte Java Spring Boot com Gradle.
- Dockerfile para geração da imagem container.
- Testes unitários automatizados.
- README específico com detalhes do serviço (endpoints, regras, execuções).
- Arquivos de configuração do Gradle.

A raiz do projeto contém:

- `settings.gradle.kts` e `build.gradle.kts` globais para multi-projeto.
- Pipelines GitHub Actions para CI.
- Pasta `exemplos-requests` com exemplos de chamadas REST via `curl` e `.http`.
- README global (este arquivo).

***

## Como Rodar Localmente Cada Microsserviço

1. Clone este repositório.

2. Navegue até a pasta do microsserviço que deseja executar (exemplo: `campanhas`).

3. Compile o projeto e rode os testes com:

```bash
./gradlew clean build
```

4. Rode o microsserviço localmente com:

```bash
./gradlew bootRun
```

Por padrão, o serviço estará disponível na porta 8080 (configure as portas se quiser rodar vários serviços ao mesmo tempo para evitar conflito).

***

## Como Rodar Usando Docker

1. No terminal, na pasta do microsserviço, construa a imagem Docker:

```bash
docker build -t servico-nome .
```
(substitua `servico-nome` por `servico-campanhas`, `servico-usuarios`, etc.)

2. Execute o container mapeando porta local conforme desejar, ex:

```bash
docker run -p 8081:8080 servico-campanhas
```

Isso expõe o microsserviço na porta 8081 no seu host.

3. Verifique que o container está ativo e acessível.

***

## Testes Unitários

- Execute os testes unitários com:

```bash
./gradlew test
```

- O pipeline CI do GitHub também roda esses testes automaticamente a cada push ou pull request.
- Na aba Actions do GitHub, verifique se o status do workflow está verde para confirmar o sucesso dos testes.

***

## Exemplos de Teste com cURL

Aqui estão alguns exemplos para testar os microsserviços via terminal usando `curl`:

### Campanhas

- Lista todas as campanhas:

```bash
curl -X GET http://localhost:8080/campanhas
```

- Cria uma nova campanha:

```bash
curl -X POST http://localhost:8080/campanhas \
-H 'Content-Type: application/json' \
-d '{
  "titulo": "Campanha de Exemplo",
  "descricao": "Descrição da campanha",
  "meta": 10000,
  "valorArrecadado": 0,
  "status": 1
}'
```

- Atualiza campanha existente:

```bash
curl -X PUT http://localhost:8080/campanhas/1 \
-H 'Content-Type: application/json' \
-d '{"valorArrecadado": 500}'
```

- Remove campanha:

```bash
curl -X DELETE http://localhost:8080/campanhas/1
```

(Substitua a URL e IDs conforme o serviço e dados criados.)

***

## Fluxo do Projeto e Entregas

O projeto atende os seguintes requisitos da entrega:

- Separação por microsserviços com código e Dockerfile próprios.
- Comunicação REST com endpoints CRUD para cada serviço.
- Uso opcional de persistência em JSON para dados simples.
- Contêineres Docker configurados individualmente para cada serviço.
- Testes unitários cobrindo regras básicas.
- GitHub Actions configurado para CI, executando testes em push e pull requests.
- Documentação clara e exemplos para rodar local e via Docker.
- (Opcional) Deploy público registrado no README para bônus.

***

