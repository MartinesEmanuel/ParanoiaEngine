# 💰 Finance Control

Sistema SaaS web profissional para controle financeiro pessoal, desenvolvido em React + TypeScript com design moderno e interface intuitiva.

![Finance Control](https://img.shields.io/badge/Status-Production%20Ready-success)
![React](https://img.shields.io/badge/React-18.3.1-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue)
![Tailwind CSS](https://img.shields.io/badge/Tailwind-4.x-38bdf8)

---

## 📚 Documentação Completa

| Documento | Descrição | Quando Usar |
|-----------|-----------|-------------|
| **[⚡ Quick Start](./QUICKSTART.md)** | Comece em 5 minutos | Primeira vez aqui |
| **[🔌 Integração Backend](./BACKEND_INTEGRATION.md)** | Conecte sua API Java/Spring Boot | Configurando backend |
| **[📡 Exemplos de API](./API_EXAMPLES.md)** | Requisições práticas | Testando endpoints |
| **[🎨 Design System](./DESIGN_SYSTEM.md)** | Guia visual completo | Entendendo o design |
| **[📐 Guia de Estilo](./STYLE_GUIDE.md)** | Padrões de código | Desenvolvendo |
| **[🤝 Como Contribuir](./CONTRIBUTING.md)** | Guia de contribuição | Contribuindo |
| **[📋 Changelog](./CHANGELOG.md)** | Histórico de versões | Acompanhando mudanças |
| **[📊 Resumo do Projeto](./PROJECT_SUMMARY.md)** | Visão executiva | Apresentando o projeto |
| **[📦 Índice de Arquivos](./FILE_INDEX.md)** | Todos os arquivos | Navegando no código |

---

## 🎯 Visão Geral

Finance Control é uma aplicação web completa para gestão financeira pessoal, com foco em produtividade, usabilidade e design premium. O projeto está pronto para MVP e portfólio profissional.

## 🖼️ Preview do Produto

As imagens abaixo mostram as principais telas da aplicação em funcionamento.

### Login

![Tela de Login](../Prints/Pasted%20image.png)

### Dashboard

![Tela de Dashboard](../Prints/Pasted%20image%20(2).png)

### Lista de Transações

![Tela de Transações](../Prints/Pasted%20image%20(3).png)

### Nova Transação

![Tela de Nova Transação](../Prints/Pasted%20image%20(4).png)

### Filtros e Gestão

![Tela com Filtros e Gestão](../Prints/Pasted%20image%20(5).png)

### ✨ Características Principais

- 🔐 **Autenticação JWT** - Login e registro seguro
- 📊 **Dashboard Intuitivo** - KPIs e visualização de dados em tempo real
- 💸 **Gestão de Transações** - CRUD completo com filtros avançados
- 📱 **Totalmente Responsivo** - Desktop, tablet e mobile
- 🌓 **Tema Claro/Escuro** - Modo dark incluso
- 🎨 **Design System Completo** - Componentes reutilizáveis
- ⚡ **Performance Otimizada** - Carregamento rápido e UX fluida
- 🔔 **Feedback Visual** - Toasts, loading states e validações

## 🛠️ Tecnologias

- **React 18.3** - Framework principal
- **TypeScript** - Type safety
- **React Router 7** - Navegação
- **Tailwind CSS 4** - Estilização
- **Radix UI** - Componentes acessíveis
- **Lucide React** - Ícones
- **Recharts** - Gráficos (preparado para expansão)
- **Date-fns** - Manipulação de datas
- **Sonner** - Sistema de notificações

## 📋 Pré-requisitos

- Node.js 18+ ou 20+
- pnpm, npm ou yarn
- Backend Java 21 + Spring Boot rodando

## 🚀 Instalação e Uso

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/finance-control.git
cd finance-control
```

### 2. Instale as dependências
```bash
npm install
# ou
pnpm install
```

### 3. Configure o ambiente
Crie um arquivo `.env` na raiz do projeto:
```env
VITE_API_URL=http://localhost:9000
```

### 4. Execute o projeto
```bash
npm run dev
# ou
pnpm dev
```

A aplicação estará disponível em `http://localhost:5173`

## 🏗️ Estrutura do Projeto

```
finance-control/
├── src/
│   ├── app/
│   │   ├── components/         # Componentes reutilizáveis
│   │   │   ├── ui/            # Design system base
│   │   │   ├── AppLayout.tsx  # Layout principal
│   │   │   ├── KPICard.tsx    # Cards de métricas
│   │   │   ├── TransactionBadge.tsx
│   │   │   ├── EmptyState.tsx
│   │   │   ├── ErrorDisplay.tsx
│   │   │   └── ProtectedRoute.tsx
│   │   ├── contexts/          # Contexts do React
│   │   │   ├── AuthContext.tsx
│   │   │   └── ThemeContext.tsx
│   │   ├── pages/             # Páginas da aplicação
│   │   │   ├── LoginPage.tsx
│   │   │   ├── RegisterPage.tsx
│   │   │   ├── DashboardPage.tsx
│   │   │   ├── TransactionsPage.tsx
│   │   │   └── TransactionFormPage.tsx
│   │   ├── services/          # Serviços de API
│   │   │   └── api.ts
│   │   ├── types/             # TypeScript types
│   │   │   └── index.ts
│   │   ├── routes.ts          # Configuração de rotas
│   │   └── App.tsx            # Componente raiz
│   └── styles/                # Estilos globais
│       ├── theme.css          # Tema e cores
│       ├── fonts.css          # Fonte Inter
│       └── tailwind.css       # Config Tailwind
├── .env.example               # Exemplo de variáveis
└── README.md
```

## 🎨 Design System

### Paleta de Cores

**Modo Claro:**
- Primary: `#2563eb` (Azul profissional)
- Success: `#10b981` (Verde para receitas)
- Destructive: `#ef4444` (Vermelho para despesas)
- Muted: `#f8fafc` (Backgrounds secundários)

**Modo Escuro:**
- Ajustes automáticos para melhor contraste

### Componentes

- Buttons (primary, secondary, ghost, destructive)
- Inputs & Forms
- Cards & KPIs
- Tables responsivas
- Modals & Dialogs
- Badges & Chips
- Empty States
- Error Displays
- Loading Skeletons

## 📱 Telas

### 1. **Autenticação**
- Login com validação
- Registro com validação inline
- Tratamento de erros

### 2. **Dashboard**
- KPIs de saldo, receitas e despesas
- Filtros por período (mês/ano)
- Lista de transações recentes
- Navegação intuitiva

### 3. **Transações**
- Tabela completa com todas as transações
- Busca por descrição/categoria
- Filtros por tipo e categoria
- Responsiva (tabela → cards no mobile)
- Ações: criar, editar, excluir

### 4. **Formulário de Transação**
- Campos validados
- Preview em tempo real
- Suporte a edição
- Feedback visual

## 🔌 Integração com API

### Endpoints Utilizados

**Autenticação:**
- `POST /api/auth/register` - Criar conta
- `POST /api/auth/login` - Login

**Transações:**
- `GET /api/transactions` - Listar todas
- `POST /api/transactions` - Criar
- `GET /api/transactions/{id}` - Buscar por ID
- `PUT /api/transactions/{id}` - Atualizar
- `DELETE /api/transactions/{id}` - Excluir
- `GET /api/transactions/balance` - Saldo total
- `GET /api/transactions/simplePeriod?month=X&year=Y` - Por período

### Tratamento de Erros

O sistema trata automaticamente:
- 400 - Validação de dados
- 401 - Não autorizado (redireciona para login)
- 404 - Recurso não encontrado
- 500 - Erro do servidor

## 🎯 Funcionalidades

### ✅ Implementadas

- [x] Autenticação completa (login/registro)
- [x] Dashboard com KPIs
- [x] CRUD de transações
- [x] Filtros avançados (período, tipo, categoria)
- [x] Busca em tempo real
- [x] Responsividade completa
- [x] Dark mode
- [x] Validações de formulário
- [x] Tratamento de erros
- [x] Loading states
- [x] Empty states
- [x] Toasts de feedback

### 🚀 Próximos Passos (Sugestões)

- [ ] Gráficos e visualizações avançadas
- [ ] Exportação de dados (CSV/PDF)
- [ ] Metas financeiras
- [ ] Categorias customizadas
- [ ] Relatórios por período
- [ ] Multi-currency
- [ ] Anexos em transações

## 📖 Guia de Uso

### Configuração Inicial

1. Inicie o backend Java + Spring Boot
2. Configure a variável `VITE_API_URL` no `.env`
3. Execute a aplicação React
4. Acesse e crie sua conta

### Fluxo Típico

1. **Registro/Login** - Crie uma conta ou faça login
2. **Dashboard** - Visualize o resumo das suas finanças
3. **Adicionar Transação** - Clique em "Adicionar Transação"
4. **Gerenciar** - Veja, edite ou exclua transações na tela de Transações
5. **Filtrar** - Use os filtros para encontrar transações específicas

## 🎨 Customização

### Alterar Cores do Tema

Edite `/src/styles/theme.css`:
```css
:root {
  --primary: #2563eb;  /* Sua cor aqui */
  --success: #10b981;
  --destructive: #ef4444;
}
```

### Adicionar Nova Categoria

As categorias são dinâmicas e criadas pelos usuários ao adicionar transações.

## 🔒 Segurança

- Autenticação JWT
- Tokens armazenados localmente (localStorage)
- Headers de autenticação em todas as requisições
- Rotas protegidas com redirect
- Validações no frontend e backend

## 🤝 Contribuindo

Este é um projeto de portfólio. Sugestões e melhorias são bem-vindas!

## 📄 Licença

Projeto desenvolvido para fins educacionais e de portfólio.

## 👨‍💻 Autor

Desenvolvido como projeto SaaS profissional para demonstração de habilidades em:
- Desenvolvimento Frontend moderno
- React + TypeScript
- Design System
- UX/UI
- Integração com APIs REST
- Código limpo e organizado

---

**Finance Control** - Controle suas finanças com simplicidade e elegância 💰