# 🤝 Guia de Contribuição

Obrigado por considerar contribuir com o Finance Control! Este documento descreve o processo para contribuir com o projeto.

## 📋 Índice

- [Como Contribuir](#como-contribuir)
- [Processo de Desenvolvimento](#processo-de-desenvolvimento)
- [Padrões de Código](#padrões-de-código)
- [Commits](#commits)
- [Pull Requests](#pull-requests)
- [Reportar Bugs](#reportar-bugs)
- [Sugerir Funcionalidades](#sugerir-funcionalidades)

## 🚀 Como Contribuir

### 1. Fork o Projeto

```bash
# Clone seu fork
git clone https://github.com/seu-usuario/finance-control.git
cd finance-control

# Adicione o repositório original como upstream
git remote add upstream https://github.com/original/finance-control.git
```

### 2. Crie uma Branch

```bash
# Atualize sua main
git checkout main
git pull upstream main

# Crie uma branch para sua feature/fix
git checkout -b feature/nome-da-feature
# ou
git checkout -b fix/nome-do-bug
```

### 3. Desenvolva

Faça suas alterações seguindo os [padrões de código](#padrões-de-código).

### 4. Teste

```bash
# Execute o projeto localmente
npm run dev

# Teste todas as funcionalidades afetadas
```

### 5. Commit

```bash
git add .
git commit -m "feat: adiciona nova funcionalidade X"
```

### 6. Push

```bash
git push origin feature/nome-da-feature
```

### 7. Pull Request

Abra um Pull Request no GitHub com:
- Título claro e descritivo
- Descrição detalhada das mudanças
- Screenshots (se aplicável)
- Issues relacionadas

## 🔄 Processo de Desenvolvimento

### Setup Inicial

1. Instale as dependências:
```bash
npm install
```

2. Configure o ambiente:
```bash
cp .env.example .env
# Edite o .env com suas configurações
```

3. Execute em desenvolvimento:
```bash
npm run dev
```

### Estrutura de Branches

- `main` - Branch principal (produção)
- `develop` - Branch de desenvolvimento
- `feature/*` - Novas funcionalidades
- `fix/*` - Correções de bugs
- `hotfix/*` - Correções urgentes
- `docs/*` - Alterações na documentação

### Workflow

1. Sempre baseie seu trabalho na branch `main` ou `develop`
2. Crie uma branch específica para cada feature/fix
3. Faça commits pequenos e frequentes
4. Teste localmente antes de fazer push
5. Abra PR para a branch apropriada

## 📐 Padrões de Código

### TypeScript

- ✅ Use TypeScript estrito (sem `any`)
- ✅ Defina interfaces para todas as props
- ✅ Use tipos explícitos quando necessário
- ✅ Evite type assertions desnecessárias

### React

- ✅ Componentes funcionais com hooks
- ✅ Props destructuring
- ✅ Export nomeado (`export function`)
- ✅ Use `React.memo` para componentes pesados
- ✅ Custom hooks começam com `use`

### Styling

- ✅ Use Tailwind CSS classes
- ✅ Siga a ordem: Layout → Display → Spacing → Colors
- ✅ Use variáveis do theme.css
- ✅ Mantenha classes organizadas e legíveis

### Nomenclatura

```tsx
// Componentes: PascalCase
function UserCard() {}

// Variáveis e funções: camelCase
const userName = 'João';
function calculateTotal() {}

// Constantes: SCREAMING_SNAKE_CASE
const API_URL = 'http://localhost:8080';

// Booleans: is*, has*, should*
const isLoading = true;
const hasError = false;

// Event handlers: handle*
const handleClick = () => {};
const handleSubmit = () => {};
```

Veja mais em [STYLE_GUIDE.md](./STYLE_GUIDE.md)

## 📝 Commits

### Formato

```
tipo(escopo): mensagem curta

Descrição mais detalhada (opcional)

BREAKING CHANGE: descrição da mudança (se aplicável)
```

### Tipos

- `feat` - Nova funcionalidade
- `fix` - Correção de bug
- `docs` - Alteração na documentação
- `style` - Formatação, ponto e vírgula, etc
- `refactor` - Refatoração de código
- `test` - Adicionar ou modificar testes
- `chore` - Atualizar tarefas de build, configs, etc
- `perf` - Melhoria de performance

### Exemplos

```bash
# Nova funcionalidade
git commit -m "feat(transactions): adiciona filtro por período customizado"

# Correção de bug
git commit -m "fix(auth): corrige validação de token expirado"

# Documentação
git commit -m "docs(readme): atualiza instruções de instalação"

# Refatoração
git commit -m "refactor(api): extrai lógica de autenticação para service"

# Performance
git commit -m "perf(dashboard): otimiza renderização de transações"
```

## 🔀 Pull Requests

### Checklist

Antes de abrir um PR, verifique:

- [ ] Código está funcionando localmente
- [ ] Segue os padrões de código do projeto
- [ ] Não há console.logs ou código de debug
- [ ] Não há conflitos com a branch base
- [ ] Commits seguem o padrão de mensagens
- [ ] Documentação foi atualizada (se necessário)
- [ ] Screenshots foram incluídos (se aplicável)

### Template de PR

```markdown
## Descrição
Breve descrição das mudanças

## Tipo de Mudança
- [ ] Bug fix
- [ ] Nova funcionalidade
- [ ] Breaking change
- [ ] Documentação

## Como Testar
1. Faça checkout desta branch
2. Execute `npm install`
3. Execute `npm run dev`
4. Navegue para X
5. Teste Y

## Screenshots
(Se aplicável)

## Issues Relacionadas
Fixes #123
Closes #456
```

### Revisão

- PRs serão revisados por mantenedores
- Feedback será dado em até 48 horas
- Mudanças podem ser solicitadas
- Seja receptivo a feedback construtivo

## 🐛 Reportar Bugs

### Antes de Reportar

1. Verifique se o bug já foi reportado nas [Issues](https://github.com/usuario/finance-control/issues)
2. Confirme que o bug é reproduzível
3. Teste na versão mais recente

### Template de Bug Report

```markdown
**Descrição do Bug**
Descrição clara e concisa do bug

**Como Reproduzir**
1. Vá para '...'
2. Clique em '...'
3. Veja o erro

**Comportamento Esperado**
O que deveria acontecer

**Screenshots**
Se aplicável, adicione screenshots

**Ambiente**
- OS: [ex: Windows 10]
- Browser: [ex: Chrome 120]
- Versão do Node: [ex: 20.10.0]

**Informações Adicionais**
Qualquer outra informação relevante
```

## 💡 Sugerir Funcionalidades

### Template de Feature Request

```markdown
**A funcionalidade resolve um problema? Descreva.**
Descrição clara do problema

**Descreva a solução desejada**
Como você gostaria que funcionasse

**Alternativas consideradas**
Outras abordagens que você pensou

**Contexto Adicional**
Screenshots, mockups, exemplos de outros apps
```

## 🎯 Áreas que Precisam de Ajuda

Contribuições são especialmente bem-vindas em:

### Frontend
- [ ] Melhorias de UX/UI
- [ ] Novas visualizações de dados
- [ ] Componentes reutilizáveis
- [ ] Animações e transições
- [ ] Acessibilidade (a11y)

### Features
- [ ] Gráficos e dashboards avançados
- [ ] Exportação de dados (CSV, PDF)
- [ ] Metas financeiras
- [ ] Categorias customizadas
- [ ] Temas customizáveis

### Documentação
- [ ] Melhorias no README
- [ ] Tutoriais em vídeo
- [ ] Exemplos de uso
- [ ] Traduções (i18n)
- [ ] API documentation

### Testes
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Testes E2E
- [ ] Testes de acessibilidade

### DevOps
- [ ] CI/CD pipeline
- [ ] Docker setup
- [ ] Deploy automation
- [ ] Monitoring

## 📚 Recursos Úteis

- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Radix UI Primitives](https://www.radix-ui.com/)
- [Conventional Commits](https://www.conventionalcommits.org/)

## 🙋 Perguntas?

- Abra uma [Discussion](https://github.com/usuario/finance-control/discussions)
- Entre em contato via [email](mailto:contato@example.com)

## 📜 Código de Conduta

### Nossa Promessa

Estamos comprometidos em proporcionar uma experiência acolhedora e inspiradora para todos.

### Comportamento Esperado

- Use linguagem acolhedora e inclusiva
- Seja respeitoso com pontos de vista diferentes
- Aceite críticas construtivas com elegância
- Foque no que é melhor para a comunidade
- Mostre empatia com outros membros

### Comportamento Inaceitável

- Uso de linguagem ou imagens sexualizadas
- Trolling, comentários insultuosos/depreciativos
- Assédio público ou privado
- Publicar informações privadas de outros
- Conduta não profissional

## ⭐ Agradecimentos

Toda contribuição é valorizada! Contribuidores serão reconhecidos em:
- README.md (seção de Contributors)
- CHANGELOG.md
- Release notes

Obrigado por ajudar a tornar o Finance Control melhor! 💙

---

**Desenvolvido com ❤️ pela comunidade**
