# 🔌 Guia de Integração com Backend

Este documento explica como conectar o Finance Control com seu backend Java + Spring Boot.

## 📋 Pré-requisitos do Backend

Certifique-se de que seu backend possui:

- ✅ Java 21 + Spring Boot
- ✅ PostgreSQL configurado
- ✅ Autenticação JWT implementada
- ✅ CORS configurado para aceitar requisições do frontend
- ✅ Todos os endpoints REST funcionando

## 🔧 Configuração do CORS no Backend

Adicione esta configuração no seu Spring Boot para permitir requisições do frontend:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```

## 🌐 Configuração da URL da API

### Desenvolvimento Local

Crie um arquivo `.env` na raiz do projeto frontend:

```env
VITE_API_URL=http://localhost:9000
```

### Produção

Para deploy em produção, configure a variável de ambiente:

```env
VITE_API_URL=https://sua-api-backend.com
```

## 📡 Endpoints Esperados

O frontend espera que os seguintes endpoints estejam disponíveis:

### Autenticação

```
POST /api/auth/register
Body: { "username": "string", "password": "string" }
Response: { "token": "string", "username": "string" }

POST /api/auth/login
Body: { "username": "string", "password": "string" }
Response: { "token": "string", "username": "string" }
```

### Transações

```
GET /api/transactions
Headers: Authorization: Bearer {token}
Response: Transaction[]

POST /api/transactions
Headers: Authorization: Bearer {token}
Body: {
  "description": "string",
  "amount": number,
  "type": "INCOME" | "EXPENSE",
  "date": "YYYY-MM-DD",
  "category": "string" (optional)
}
Response: Transaction

GET /api/transactions/{id}
Headers: Authorization: Bearer {token}
Response: Transaction

PUT /api/transactions/{id}
Headers: Authorization: Bearer {token}
Body: TransactionInput
Response: Transaction

DELETE /api/transactions/{id}
Headers: Authorization: Bearer {token}
Response: 204 No Content

GET /api/transactions/balance
Headers: Authorization: Bearer {token}
Response: {
  "total": number,
  "income": number,
  "expense": number
}

GET /api/transactions/simplePeriod?month={1-12}&year={YYYY}
Headers: Authorization: Bearer {token}
Response: Transaction[]

GET /api/transactions/category?category={string}
Headers: Authorization: Bearer {token}
Response: Transaction[]
```

## 📦 Modelo de Dados

### Transaction Entity

```java
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 1, max = 255)
    private String description;
    
    @Positive
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME or EXPENSE
    
    private LocalDate date;
    
    private String category;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private String createdBy;
}
```

### ProblemDetail para Erros

O frontend espera erros no formato ProblemDetail (RFC 7807):

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Dados Inválidos",
  "status": 400,
  "detail": "O campo descrição é obrigatório",
  "instance": "/api/transactions"
}
```

Configure seu `@ControllerAdvice` para retornar neste formato:

```java
@ExceptionHandler(ValidationException.class)
public ResponseEntity<ProblemDetail> handleValidation(ValidationException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        ex.getMessage()
    );
    problem.setTitle("Dados Inválidos");
    return ResponseEntity.badRequest().body(problem);
}
```

## 🔐 Autenticação JWT

### Formato do Token

O frontend envia o token JWT no header:

```
Authorization: Bearer {seu-token-jwt}
```

### Implementação no Backend

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String token = extractToken(request);
        
        if (token != null && jwtService.validateToken(token)) {
            Authentication auth = jwtService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

## 🧪 Testando a Conexão

### 1. Inicie o Backend

```bash
cd seu-backend-java
./mvnw spring-boot:run
```

Verifique que está rodando em `http://localhost:9000`

### 2. Inicie o Frontend

```bash
cd finance-control
npm run dev
```

### 3. Teste o Fluxo

1. Abra `http://localhost:5173`
2. Clique em "Criar conta"
3. Preencha os dados e registre
4. Você deve ser redirecionado para o Dashboard
5. Adicione uma transação de teste

### 4. Verifique os Logs

No console do navegador (F12 → Network), você deve ver:

- ✅ `POST /api/auth/register` → 200 OK
- ✅ `GET /api/transactions` → 200 OK
- ✅ `GET /api/transactions/balance` → 200 OK

## 🐛 Troubleshooting

### Erro de CORS

**Sintoma:** Erro "CORS policy" no console

**Solução:** Configure o CORS no backend (veja seção acima)

### Erro 401 Unauthorized

**Sintoma:** Todas as requisições retornam 401

**Solução:** 
1. Verifique se o token JWT está sendo gerado corretamente no login
2. Verifique se o `JwtAuthenticationFilter` está configurado
3. Verifique se o token está sendo enviado no header

### Erro "Network Error"

**Sintoma:** Erro de conexão

**Solução:**
1. Verifique se o backend está rodando
2. Confirme a URL no `.env`
3. Teste a API com Postman/Insomnia

### Validação não funciona

**Sintoma:** Backend aceita dados inválidos

**Solução:** 
1. Adicione `@Valid` nos controllers
2. Configure `@NotBlank`, `@Positive` nas entidades
3. Implemente `@ControllerAdvice` para erros

## 📝 Exemplo Completo - Controller

```java
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService service;
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionInput input) {
        Transaction created = service.create(input);
        return ResponseEntity.status(201).body(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionInput input) {
        return ResponseEntity.ok(service.update(id, input));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance() {
        return ResponseEntity.ok(service.calculateBalance());
    }
    
    @GetMapping("/simplePeriod")
    public ResponseEntity<List<Transaction>> getByPeriod(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(service.findByPeriod(month, year));
    }
}
```

## ✅ Checklist Final

Antes de começar a usar a aplicação:

- [ ] Backend rodando em http://localhost:9000
- [ ] Banco de dados PostgreSQL ativo
- [ ] CORS configurado
- [ ] JWT funcionando
- [ ] Todos os endpoints implementados
- [ ] `.env` configurado no frontend
- [ ] Frontend rodando em http://localhost:5173
- [ ] Login funcional
- [ ] Dashboard carregando dados

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs do backend Spring Boot
2. Abra o DevTools do navegador (F12) → Network
3. Teste os endpoints diretamente com Postman
4. Verifique se todas as dependências estão instaladas

---

**Dica:** Use `console.log()` no arquivo `/src/app/services/api.ts` para debugar as requisições durante o desenvolvimento.
