# 📋 Changelog - Finance Control

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-03-26

### ✨ Adicionado

#### Autenticação
- Sistema completo de login e registro
- Autenticação JWT com localStorage
- Validação inline de campos (username 3-80, senha 6-120)
- Proteção de rotas privadas
- Tratamento de token expirado com redirect para login
- Feedback visual de erros de autenticação

#### Dashboard
- KPI cards para saldo total, receitas e despesas
- Filtros por período (mês/ano)
- Lista de transações recentes (últimas 5)
- Loading states com skeleton
- Navegação rápida para adicionar transação
- Formatação de moeda em pt-BR

#### Transações
- Listagem completa de transações
- CRUD completo (criar, ler, atualizar, excluir)
- Busca em tempo real por descrição/categoria
- Filtros por tipo (Receita/Despesa)
- Filtro por categoria dinâmica
- Tabela responsiva (desktop) → Cards (mobile)
- Modal de confirmação para exclusão
- Badges coloridos para tipo de transação
- Validações de formulário inline
- Preview em tempo real ao criar/editar

#### Design System
- Tema claro e escuro completos
- Paleta de cores profissional SaaS
  - Primary: Azul (#2563eb)
  - Success: Verde (#10b981) para receitas
  - Destructive: Vermelho (#ef4444) para despesas
- Componentes UI base (Radix UI)
  - Buttons (primary, secondary, ghost, destructive)
  - Inputs e Forms
  - Cards e KPIs
  - Tables responsivas
  - Modals e Dialogs
  - Badges e Chips
  - Select dropdowns
  - Empty states
  - Error displays
  - Loading skeletons
- Tipografia Inter
- Grid de 8px
- Border radius suave
- Sombras leves

#### UX/UI
- Layout responsivo completo (1440px, 1024px, 390px)
- Header fixo com navegação
- Menu mobile com hamburger
- Toggle de tema claro/escuro
- Toasts de feedback (sucesso/erro)
- Empty states elegantes
- Estados de loading
- Animações suaves de entrada
- Transições fluidas
- Microcopy em pt-BR

#### Integração Backend
- Serviço completo de API REST
- Tratamento de erros com ProblemDetail
- Retry em caso de erro
- Headers de autenticação automáticos
- Suporte a todos os endpoints do backend Java + Spring Boot

#### Documentação
- README.md completo
- QUICKSTART.md para início rápido
- BACKEND_INTEGRATION.md para integração
- Comentários no código
- TypeScript types documentados
- .env.example para variáveis de ambiente

### 🎨 Design
- Interface moderna estilo fintech
- Hierarquia visual clara
- Contraste adequado (WCAG AA)
- Ícones minimalistas (Lucide)
- Espaçamento consistente
- Cores semânticas (sucesso, erro, aviso)

### 🚀 Performance
- Lazy loading de rotas
- Memoização de componentes pesados
- Debounce em buscas
- Loading states granulares
- Bundle otimizado com Vite

### 📱 Responsividade
- Mobile-first approach
- Breakpoints bem definidos
- Touch-friendly (botões maiores no mobile)
- Menu adaptativo
- Tabelas que viram cards no mobile

### 🔒 Segurança
- Autenticação JWT
- Rotas protegidas
- Validações client-side
- Sanitização de inputs
- Headers seguros

### 📊 Funcionalidades Financeiras
- Cálculo automático de saldo
- Separação de receitas e despesas
- Categorização de transações
- Filtros por período
- Busca avançada
- Histórico completo

---

## [Futuro] - Roadmap

### 🔮 Funcionalidades Planejadas

#### v1.1.0 - Visualizações
- [ ] Gráficos de receitas vs despesas (Recharts)
- [ ] Gráfico de pizza por categoria
- [ ] Evolução do saldo ao longo do tempo
- [ ] Comparativo entre meses

#### v1.2.0 - Relatórios
- [ ] Exportação para CSV
- [ ] Exportação para PDF
- [ ] Relatórios mensais automáticos
- [ ] Resumo por categoria

#### v1.3.0 - Metas e Planejamento
- [ ] Definir metas mensais
- [ ] Alertas de gastos
- [ ] Budget por categoria
- [ ] Previsões baseadas em histórico

#### v1.4.0 - Melhorias UX
- [ ] Atalhos de teclado
- [ ] Filtros salvos
- [ ] Transações recorrentes
- [ ] Templates de transações

#### v1.5.0 - Avançado
- [ ] Multi-moeda
- [ ] Anexos em transações (recibos)
- [ ] Notas e comentários
- [ ] Tags customizadas
- [ ] Compartilhamento de relatórios

#### v2.0.0 - Premium
- [ ] Múltiplas contas bancárias
- [ ] Investimentos
- [ ] Cartões de crédito
- [ ] Sincronização bancária (Open Banking)
- [ ] IA para categorização automática

### 🐛 Correções Conhecidas
Nenhuma no momento.

### ⚠️ Breaking Changes
Nenhuma no momento.

---

## Tipo de Mudanças

- `✨ Adicionado` - Para novas funcionalidades
- `🔄 Modificado` - Para mudanças em funcionalidades existentes
- `🗑️ Removido` - Para funcionalidades removidas
- `🐛 Corrigido` - Para correção de bugs
- `🔒 Segurança` - Para correções de segurança
- `📝 Documentação` - Para mudanças na documentação
- `🎨 Estilo` - Para mudanças que não afetam o funcionamento
- `🚀 Performance` - Para melhorias de performance

---

**Finance Control** - Versão 1.0.0 - MVP Completo 🎉
