# 📡 Exemplos de Requisições API

Este documento contém exemplos práticos de todas as requisições da API Finance Control.

## 🔐 Autenticação

### Registrar Novo Usuário

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao.silva",
    "password": "senha123"
  }'
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "joao.silva"
}
```

### Fazer Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao.silva",
    "password": "senha123"
  }'
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "joao.silva"
}
```

---

## 💰 Transações

### Listar Todas as Transações

```bash
curl -X GET http://localhost:8080/api/transactions \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME",
    "date": "2026-03-01",
    "category": "Trabalho",
    "createdAt": "2026-03-01T10:00:00Z",
    "createdBy": "joao.silva"
  },
  {
    "id": 2,
    "description": "Supermercado",
    "amount": 350.00,
    "type": "EXPENSE",
    "date": "2026-03-05",
    "category": "Alimentação",
    "createdAt": "2026-03-05T14:30:00Z",
    "createdBy": "joao.silva"
  }
]
```

### Criar Nova Transação

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Freelance",
    "amount": 800.00,
    "type": "INCOME",
    "date": "2026-03-26",
    "category": "Trabalho"
  }'
```

**Resposta (201 Created):**
```json
{
  "id": 3,
  "description": "Freelance",
  "amount": 800.00,
  "type": "INCOME",
  "date": "2026-03-26",
  "category": "Trabalho",
  "createdAt": "2026-03-26T10:15:00Z",
  "createdBy": "joao.silva"
}
```

### Buscar Transação por ID

```bash
curl -X GET http://localhost:8080/api/transactions/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "description": "Salário",
  "amount": 5000.00,
  "type": "INCOME",
  "date": "2026-03-01",
  "category": "Trabalho",
  "createdAt": "2026-03-01T10:00:00Z",
  "createdBy": "joao.silva"
}
```

### Atualizar Transação

```bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Salário Mensal",
    "amount": 5500.00,
    "type": "INCOME",
    "date": "2026-03-01",
    "category": "Trabalho"
  }'
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "description": "Salário Mensal",
  "amount": 5500.00,
  "type": "INCOME",
  "date": "2026-03-01",
  "category": "Trabalho",
  "createdAt": "2026-03-01T10:00:00Z",
  "createdBy": "joao.silva"
}
```

### Excluir Transação

```bash
curl -X DELETE http://localhost:8080/api/transactions/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (204 No Content):**
(Sem corpo de resposta)

---

## 📊 Consultas

### Buscar Saldo Total

```bash
curl -X GET http://localhost:8080/api/transactions/balance \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
{
  "total": 4650.00,
  "income": 5000.00,
  "expense": 350.00
}
```

### Buscar Saldo por Período

```bash
curl -X GET "http://localhost:8080/api/transactions/balance/period?start=2026-03-01&end=2026-03-31" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
{
  "total": 4650.00,
  "income": 5000.00,
  "expense": 350.00
}
```

### Buscar Transações por Período

```bash
curl -X GET "http://localhost:8080/api/transactions/period?start=2026-03-01&end=2026-03-31" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME",
    "date": "2026-03-01",
    "category": "Trabalho",
    "createdAt": "2026-03-01T10:00:00Z",
    "createdBy": "joao.silva"
  }
]
```

### Buscar Transações por Mês/Ano

```bash
curl -X GET "http://localhost:8080/api/transactions/simplePeriod?month=3&year=2026" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME",
    "date": "2026-03-01",
    "category": "Trabalho",
    "createdAt": "2026-03-01T10:00:00Z",
    "createdBy": "joao.silva"
  }
]
```

### Buscar Transações por Categoria

```bash
curl -X GET "http://localhost:8080/api/transactions/category?category=Trabalho" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "description": "Salário",
    "amount": 5000.00,
    "type": "INCOME",
    "date": "2026-03-01",
    "category": "Trabalho",
    "createdAt": "2026-03-01T10:00:00Z",
    "createdBy": "joao.silva"
  }
]
```

---

## ⚠️ Tratamento de Erros

### 400 - Bad Request (Validação)

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Dados Inválidos",
  "status": 400,
  "detail": "O campo descrição é obrigatório",
  "instance": "/api/transactions"
}
```

### 401 - Unauthorized (Não Autenticado)

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "Não Autorizado",
  "status": 401,
  "detail": "Token JWT inválido ou expirado",
  "instance": "/api/transactions"
}
```

### 404 - Not Found (Recurso Não Encontrado)

```json
{
  "type": "https://example.com/errors/not-found",
  "title": "Não Encontrado",
  "status": 404,
  "detail": "Transação com ID 999 não encontrada",
  "instance": "/api/transactions/999"
}
```

### 500 - Internal Server Error

```json
{
  "type": "https://example.com/errors/server-error",
  "title": "Erro no Servidor",
  "status": 500,
  "detail": "Ocorreu um erro inesperado no servidor",
  "instance": "/api/transactions"
}
```

---

## 🧪 Testando com JavaScript (Fetch)

### Exemplo: Login e Criar Transação

```javascript
// 1. Fazer login
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'joao.silva',
    password: 'senha123'
  })
});

const { token } = await loginResponse.json();

// 2. Criar transação
const transactionResponse = await fetch('http://localhost:8080/api/transactions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    description: 'Freelance Design',
    amount: 800.00,
    type: 'INCOME',
    date: '2026-03-26',
    category: 'Trabalho'
  })
});

const transaction = await transactionResponse.json();
console.log('Transação criada:', transaction);
```

---

## 📝 Notas Importantes

1. **Token JWT**: Sempre inclua o token no header `Authorization: Bearer {token}`
2. **Datas**: Use formato ISO 8601 (`YYYY-MM-DD`)
3. **Valores**: Use números decimais (não strings)
4. **Tipos**: Apenas `INCOME` ou `EXPENSE` (maiúsculas)
5. **Categoria**: Campo opcional, pode ser `null` ou omitido

---

## 🔧 Ferramentas Recomendadas

- **Postman**: Para testes manuais
- **Insomnia**: Alternativa ao Postman
- **cURL**: Para testes via terminal
- **HTTPie**: cURL mais amigável

---

**Dica**: Salve essas requisições em uma collection do Postman para reutilizar!
