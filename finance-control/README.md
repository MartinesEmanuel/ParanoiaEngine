# Finance Control API

API REST para controle financeiro pessoal, construída com Java 21 + Spring Boot + PostgreSQL.

## Preview do projeto

Algumas telas do projeto para visualização rápida no GitHub:

### Login

![Tela de Login](Prints/Pasted%20image.png)

### Dashboard

![Tela de Dashboard](Prints/Pasted%20image%20(2).png)

### Transações

![Tela de Transações](Prints/Pasted%20image%20(3).png)

### Nova transação

![Tela de Nova transação](Prints/Pasted%20image%20(4).png)

### Filtros e gestão

![Tela de Filtros e gestão](Prints/Pasted%20image%20(5).png)

## Stack

- Java 21
- Spring Boot 4
- Spring Data JPA (Hibernate)
- Spring Security com JWT
- PostgreSQL (dev)
- H2 (test)
- Springdoc OpenAPI (Swagger)
- Spring Boot Actuator

## Arquitetura

Pacotes principais:

- `controller`: endpoints HTTP
- `service`: regras de negocio
- `repository`: acesso a dados
- `model`: entidades e enums
- `dto`: contratos de entrada/saida
- `security`: JWT e filtro de autenticacao
- `exception`: tratamento global de erros
- `config`: seguranca e bootstrap

## Funcionalidades

- CRUD de transacoes
- Consulta de saldo total
- Consulta de saldo por periodo
- Filtro por periodo, mes/ano e categoria
- Registro e login com JWT
- Endpoints protegidos por token
- Respostas de erro padronizadas com `ProblemDetail`

## Configuracao local

Configure PostgreSQL em `src/main/resources/application.properties`.

Propriedades principais:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `security.jwt.secret` (Base64)

Admin bootstrap (dev):

- `app.bootstrap-admin.username`
- `app.bootstrap-admin.password`

## Executar

```bash
./mvnw spring-boot:run
```

## Testes

```bash
./mvnw test
```

Os testes usam profile `test` com H2 em memoria (`application-test.properties`).

## Fluxo de autenticacao

1. Registrar usuario em `POST /api/auth/register`
2. Fazer login em `POST /api/auth/login`
3. Receber token JWT
4. Enviar header `Authorization: Bearer <token>` nos endpoints protegidos

## Endpoints principais

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/transactions`
- `POST /api/transactions`
- `GET /api/transactions/{id}`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`
- `GET /api/transactions/balance`
- `GET /api/transactions/balance/period?start=YYYY-MM-DD&end=YYYY-MM-DD`
- `GET /api/transactions/period?start=YYYY-MM-DD&end=YYYY-MM-DD`
- `GET /api/transactions/simplePeriod?month=3&year=2026`
- `GET /api/transactions/category?category=Trabalho`

Swagger:

- `/swagger-ui.html`

Actuator:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

## Exemplo rapido com curl

Registrar:

```bash
curl -s -X POST http://localhost:9000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"martins","password":"123456"}'
```

Login:

```bash
curl -s -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"martins","password":"123456"}'
```

Criar transacao (substitua TOKEN):

```bash
curl -s -X POST http://localhost:9000/api/transactions \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"Salario","amount":3000,"type":"INCOME","date":"2026-03-10"}'
```

