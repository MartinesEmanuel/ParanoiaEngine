# 🚀 Quick Start Guide - Finance Control

Guia rápido para começar a usar o Finance Control em 5 minutos.

## ⚡ Setup Rápido

### 1. Clone e Instale (1 minuto)

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/finance-control.git
cd finance-control

# Instale as dependências
npm install
```

### 2. Configure o Backend (1 minuto)

Crie o arquivo `.env`:

```bash
# Copie o exemplo
cp .env.example .env

# Edite e configure a URL do backend
# .env
VITE_API_URL=http://localhost:8080
```

### 3. Execute (30 segundos)

```bash
npm run dev
```

Acesse: `http://localhost:5173`

## 📱 Primeiro Acesso

1. **Criar Conta**
   - Clique em "Criar conta"
   - Usuário: 3-80 caracteres
   - Senha: 6-120 caracteres
   - Clique em "Criar Conta"

2. **Dashboard**
   - Você será redirecionado automaticamente
   - Veja seus KPIs (saldo, receitas, despesas)

3. **Adicionar Primeira Transação**
   - Clique em "Adicionar Transação"
   - Preencha os dados
   - Clique em "Criar Transação"

## 🎯 Funcionalidades Principais

### Dashboard
- 📊 Visualize saldo total, receitas e despesas
- 🔍 Filtre por mês e ano
- 📝 Veja transações recentes

### Transações
- ➕ Criar nova transação
- ✏️ Editar transação existente
- 🗑️ Excluir transação
- 🔍 Buscar por descrição ou categoria
- 🏷️ Filtrar por tipo (Receita/Despesa)
- 📅 Filtrar por período

### Perfil
- 🌓 Alternar tema (claro/escuro)
- 🚪 Logout

## 🛠️ Scripts Disponíveis

```bash
# Desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview
```

## 📖 Estrutura de Pastas

```
src/app/
├── components/      # Componentes reutilizáveis
├── contexts/        # React Contexts
├── pages/           # Páginas da aplicação
├── services/        # Serviços de API
├── types/           # TypeScript types
└── routes.ts        # Configuração de rotas
```

## 🔌 Endpoints Necessários

Certifique-se de que seu backend possui:

- `POST /api/auth/register` - Criar conta
- `POST /api/auth/login` - Login
- `GET /api/transactions` - Listar transações
- `POST /api/transactions` - Criar transação
- `PUT /api/transactions/{id}` - Atualizar
- `DELETE /api/transactions/{id}` - Excluir
- `GET /api/transactions/balance` - Saldo
- `GET /api/transactions/simplePeriod` - Por período

Veja mais detalhes em [BACKEND_INTEGRATION.md](./BACKEND_INTEGRATION.md)

## 🎨 Personalização Rápida

### Mudar Cor Primária

Edite `/src/styles/theme.css`:

```css
:root {
  --primary: #2563eb; /* Sua cor aqui */
}
```

### Adicionar Nova Página

1. Crie em `/src/app/pages/MinhaPage.tsx`
2. Adicione rota em `/src/app/routes.ts`
3. Adicione no menu em `/src/app/components/AppLayout.tsx`

## 🐛 Troubleshooting

### Backend não conecta
```bash
# Verifique se o backend está rodando
curl http://localhost:8080/api/transactions

# Verifique o .env
cat .env
```

### Erro de CORS
Adicione no backend Spring Boot:
```java
@CrossOrigin(origins = "http://localhost:5173")
```

### Token expirado
- Faça logout e login novamente
- Verifique a validade do token JWT no backend

## 📚 Recursos Adicionais

- [README.md](./README.md) - Documentação completa
- [BACKEND_INTEGRATION.md](./BACKEND_INTEGRATION.md) - Guia de integração com backend

## 💡 Dicas

1. **Use o Dark Mode** - Clique no ícone de lua/sol no header
2. **Filtre por Período** - Use os filtros para ver transações específicas
3. **Categorize Tudo** - Facilita a organização posterior
4. **Mobile First** - A interface se adapta perfeitamente ao celular

## 🎯 Próximos Passos

Após o setup básico:

1. ✅ Adicione algumas transações de exemplo
2. ✅ Teste os filtros e busca
3. ✅ Experimente o modo escuro
4. ✅ Teste em diferentes tamanhos de tela
5. ✅ Explore o código para entender a arquitetura

## 🆘 Precisa de Ajuda?

- 📖 Leia a [documentação completa](./README.md)
- 🔌 Veja o [guia de integração](./BACKEND_INTEGRATION.md)
- 🐛 Abra uma issue no GitHub

---

**Pronto!** Você está pronto para usar o Finance Control 💰

Tempo total de setup: ~3 minutos ⚡
