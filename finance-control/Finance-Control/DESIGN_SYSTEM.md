# 🎨 Design System - Finance Control

Documentação visual completa do Design System do Finance Control.

## 🎨 Paleta de Cores

### Cores Primárias

```css
/* Azul Profissional - Cor Principal */
--primary: #2563eb (Light) / #3b82f6 (Dark)
Uso: CTAs, links, elementos interativos principais

/* Verde Sucesso - Receitas */
--success: #10b981
Uso: Receitas, ações positivas, sucesso

/* Vermelho Destrutivo - Despesas */
--destructive: #ef4444
Uso: Despesas, erros, ações destrutivas

/* Amarelo Aviso */
--warning: #f59e0b
Uso: Alertas, avisos importantes
```

### Cores Neutras

```css
/* Backgrounds */
--background: #ffffff (Light) / #0f172a (Dark)
--card: #ffffff (Light) / #1e293b (Dark)
--muted: #f8fafc (Light) / #1e293b (Dark)

/* Texts */
--foreground: #0f172a (Light) / #f8fafc (Dark)
--muted-foreground: #64748b (Light) / #94a3b8 (Dark)

/* Borders */
--border: #e2e8f0 (Light) / #334155 (Dark)
```

### Aplicação de Cores

| Elemento | Cor Light | Cor Dark | Uso |
|----------|-----------|----------|-----|
| Background Principal | `#ffffff` | `#0f172a` | Fundo da aplicação |
| Cards | `#ffffff` | `#1e293b` | Cartões e painéis |
| Texto Principal | `#0f172a` | `#f8fafc` | Títulos e corpo |
| Texto Secundário | `#64748b` | `#94a3b8` | Legendas e hints |
| Primary Button | `#2563eb` | `#3b82f6` | Botões principais |
| Receitas | `#10b981` | `#10b981` | Valores positivos |
| Despesas | `#ef4444` | `#ef4444` | Valores negativos |
| Borders | `#e2e8f0` | `#334155` | Divisores |

## 📝 Tipografia

### Fonte

```css
font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 
             'Helvetica Neue', Arial, sans-serif;
```

### Escala Tipográfica

| Elemento | Tamanho | Peso | Uso |
|----------|---------|------|-----|
| H1 | 30px (1.875rem) | 600 | Títulos de página |
| H2 | 24px (1.5rem) | 600 | Títulos de seção |
| H3 | 20px (1.25rem) | 600 | Subtítulos |
| H4 | 16px (1rem) | 600 | Títulos pequenos |
| Body | 16px (1rem) | 400 | Texto padrão |
| Small | 14px (0.875rem) | 400 | Legendas |
| Tiny | 12px (0.75rem) | 400 | Hints |

### Hierarquia

```tsx
// Página Principal
<h1>Dashboard</h1>                    // 30px, semibold

// Seções
<h2>Transações Recentes</h2>          // 24px, semibold

// Cards
<h3>Saldo Total</h3>                  // 20px, semibold

// Labels
<label>Descrição</label>              // 16px, medium

// Corpo de texto
<p>Texto normal</p>                   // 16px, regular

// Legendas
<span className="text-sm">Legenda</span>  // 14px, regular

// Hints
<span className="text-xs">Dica</span>     // 12px, regular
```

## 🧩 Componentes

### Buttons

#### Variantes

```tsx
// Primary - Ações principais
<Button variant="default">Adicionar Transação</Button>

// Secondary - Ações secundárias
<Button variant="outline">Cancelar</Button>

// Destructive - Ações destrutivas
<Button variant="destructive">Excluir</Button>

// Ghost - Ações sutis
<Button variant="ghost">Voltar</Button>
```

#### Tamanhos

```tsx
<Button size="sm">Pequeno</Button>      // height: 32px, padding: 8px 12px
<Button size="default">Médio</Button>   // height: 40px, padding: 12px 16px
<Button size="lg">Grande</Button>       // height: 48px, padding: 16px 24px
<Button size="icon">Icon</Button>       // width: 40px, height: 40px
```

#### Estados

- **Default**: Cor base
- **Hover**: +10% brightness
- **Active**: -10% brightness
- **Disabled**: 50% opacity, cursor not-allowed
- **Loading**: Spinner + disabled

### Inputs

```tsx
// Input padrão
<Input type="text" placeholder="Digite aqui..." />

// Com label
<div>
  <Label htmlFor="name">Nome</Label>
  <Input id="name" />
</div>

// Com erro
<Input className="border-destructive" />
<p className="text-xs text-destructive">Campo obrigatório</p>

// Disabled
<Input disabled />
```

### Cards

```tsx
// Card padrão
<Card className="p-6">
  <h3>Título do Card</h3>
  <p>Conteúdo</p>
</Card>

// KPI Card
<KPICard
  title="Saldo Total"
  value="R$ 10.000,00"
  icon={Wallet}
  variant="default"
/>
```

### Badges

```tsx
// Badge de tipo
<TransactionBadge type="INCOME" />   // Verde: "Receita"
<TransactionBadge type="EXPENSE" />  // Vermelho: "Despesa"

// Badge customizado
<Badge variant="default">Novo</Badge>
<Badge variant="secondary">Tag</Badge>
<Badge variant="destructive">Urgente</Badge>
```

### Tables

```tsx
<Table>
  <TableHeader>
    <TableRow>
      <TableHead>Descrição</TableHead>
      <TableHead>Valor</TableHead>
    </TableRow>
  </TableHeader>
  <TableBody>
    <TableRow>
      <TableCell>Salário</TableCell>
      <TableCell>R$ 5.000,00</TableCell>
    </TableRow>
  </TableBody>
</Table>
```

### Modals

```tsx
<Dialog open={isOpen} onOpenChange={setIsOpen}>
  <DialogContent>
    <DialogHeader>
      <DialogTitle>Título do Modal</DialogTitle>
      <DialogDescription>Descrição</DialogDescription>
    </DialogHeader>
    <div>Conteúdo</div>
    <DialogFooter>
      <Button variant="outline">Cancelar</Button>
      <Button>Confirmar</Button>
    </DialogFooter>
  </DialogContent>
</Dialog>
```

## 📐 Espaçamento

### Grid Base: 8px

```css
/* Espaçamento */
gap-1    /* 4px */
gap-2    /* 8px */
gap-3    /* 12px */
gap-4    /* 16px */
gap-6    /* 24px */
gap-8    /* 32px */

/* Padding */
p-2      /* 8px */
p-4      /* 16px */
p-6      /* 24px */
p-8      /* 32px */

/* Margin */
m-2      /* 8px */
m-4      /* 16px */
m-6      /* 24px */
m-8      /* 32px */
```

### Hierarquia de Espaçamento

| Contexto | Espaçamento | Uso |
|----------|-------------|-----|
| Entre elementos inline | 8px (gap-2) | Ícone + texto |
| Entre componentes | 16px (gap-4) | Campos de form |
| Entre seções | 24px (gap-6) | Cards em grid |
| Entre páginas | 32px (gap-8) | Espaçamento de página |

## 🔘 Border Radius

```css
--radius: 8px (0.5rem)

rounded-sm   /* 4px */  - Elementos pequenos
rounded-md   /* 6px */  - Padrão
rounded-lg   /* 8px */  - Cards, inputs
rounded-xl   /* 12px */ - Cards grandes
rounded-full /* 50% */  - Avatares, badges
```

## 🌓 Sombras

```css
/* Card padrão */
shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05)

/* Card hover */
shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1)

/* Modal */
shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1)

/* Dropdown */
shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.15)
```

## 📱 Breakpoints

```css
/* Mobile First */
sm: 640px    /* Tablet */
md: 768px    /* Desktop pequeno */
lg: 1024px   /* Desktop médio */
xl: 1280px   /* Desktop grande */
2xl: 1536px  /* Desktop extra grande */
```

### Aplicação

```tsx
// Mobile: Stack vertical
// Tablet+: Grid de 2 colunas
// Desktop: Grid de 3 colunas
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <Card />
  <Card />
  <Card />
</div>
```

## 🎭 Animações

### Duração

```css
duration-150  /* 150ms */  - Hover rápido
duration-200  /* 200ms */  - Padrão
duration-300  /* 300ms */  - Transições
duration-500  /* 500ms */  - Animações longas
```

### Easing

```css
ease-in       /* Aceleração */
ease-out      /* Desaceleração */
ease-in-out   /* Suave */
```

### Uso

```tsx
// Hover em botão
<Button className="transition-colors duration-200 hover:bg-primary/90">

// Fade in
<div className="animate-in fade-in duration-300">

// Slide up
<div className="animate-in slide-in-from-bottom-4 duration-300">
```

## 🎨 Estados

### Loading

```tsx
// Skeleton
<Skeleton className="h-4 w-32" />

// Spinner
<Loader2 className="animate-spin" />

// Pulse
<div className="animate-pulse bg-muted rounded h-4" />
```

### Empty States

```tsx
<EmptyState
  icon={FileQuestion}
  title="Nenhuma transação encontrada"
  description="Adicione sua primeira transação"
  action={{
    label: "Adicionar",
    onClick: handleAdd
  }}
/>
```

### Error States

```tsx
<ErrorDisplay
  status={400}
  title="Dados Inválidos"
  message="Verifique os campos obrigatórios"
  onRetry={handleRetry}
/>
```

## 🎯 Ícones

### Biblioteca: Lucide React

```tsx
import { 
  Wallet,      // Finanças
  TrendingUp,  // Receitas
  TrendingDown,// Despesas
  Plus,        // Adicionar
  Edit,        // Editar
  Trash2,      // Excluir
  Search,      // Buscar
  Filter,      // Filtrar
  Calendar,    // Data
  User,        // Usuário
  LogOut,      // Sair
  Moon,        // Dark mode
  Sun          // Light mode
} from 'lucide-react';
```

### Tamanhos

```tsx
<Icon className="h-4 w-4" />  // 16px - Inline text
<Icon className="h-5 w-5" />  // 20px - Botões
<Icon className="h-6 w-6" />  // 24px - Headers
<Icon className="h-8 w-8" />  // 32px - Features
<Icon className="h-12 w-12" /> // 48px - Empty states
```

## ✅ Checklist de Design

Ao criar um novo componente:

- [ ] Segue a paleta de cores
- [ ] Usa fonte Inter
- [ ] Respeita a hierarquia tipográfica
- [ ] Grid base de 8px
- [ ] Border radius consistente
- [ ] Sombras apropriadas
- [ ] Responsivo (mobile first)
- [ ] Estados (hover, active, disabled)
- [ ] Dark mode funcional
- [ ] Animações suaves
- [ ] Acessível (a11y)
- [ ] Consistente com o resto do sistema

## 📚 Recursos

- [Tailwind CSS Docs](https://tailwindcss.com)
- [Radix UI](https://www.radix-ui.com)
- [Lucide Icons](https://lucide.dev)
- [Inter Font](https://rsms.me/inter/)

---

**Finance Control Design System v1.0** 🎨
