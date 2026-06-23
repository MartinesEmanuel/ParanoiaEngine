# 📐 Guia de Estilo - Finance Control

Convenções de código e boas práticas para o projeto Finance Control.

## 📂 Estrutura de Arquivos

### Nomenclatura de Arquivos

- **Componentes React**: `PascalCase.tsx` (ex: `KPICard.tsx`)
- **Páginas**: `PascalCase.tsx` com sufixo `Page` (ex: `DashboardPage.tsx`)
- **Contextos**: `PascalCase.tsx` com sufixo `Context` (ex: `AuthContext.tsx`)
- **Serviços**: `camelCase.ts` (ex: `api.ts`)
- **Types**: `index.ts` ou `camelCase.ts`
- **Styles**: `kebab-case.css` (ex: `theme.css`)

### Organização de Pastas

```
src/app/
├── components/        # Componentes reutilizáveis
│   ├── ui/           # Componentes base do design system
│   └── [Component].tsx
├── contexts/         # React Contexts
├── pages/           # Páginas/rotas da aplicação
├── services/        # Lógica de negócio e API
├── types/           # TypeScript types e interfaces
└── routes.ts        # Configuração de rotas
```

## 🎨 Componentes React

### Estrutura de Componente

```tsx
import { useState } from 'react';
import { Button } from './ui/button';
import type { MyType } from '../types';

interface MyComponentProps {
  title: string;
  onAction: () => void;
  isLoading?: boolean;
}

export function MyComponent({ title, onAction, isLoading = false }: MyComponentProps) {
  const [state, setState] = useState('');

  const handleClick = () => {
    // Lógica aqui
    onAction();
  };

  return (
    <div className="space-y-4">
      <h2>{title}</h2>
      <Button onClick={handleClick} disabled={isLoading}>
        Ação
      </Button>
    </div>
  );
}
```

### Boas Práticas

1. **Props Interface**: Sempre defina interface para props
2. **Default Props**: Use destructuring com valores padrão
3. **Export nomeado**: Prefira `export function` ao invés de default
4. **Early Return**: Valide condições no início
5. **Componentes pequenos**: Máximo 150 linhas, extraia se maior

### Exemplo Completo

```tsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { Loader2 } from 'lucide-react';
import { Button } from './ui/button';
import { Card } from './ui/card';
import type { Transaction } from '../types';

interface TransactionListProps {
  userId: string;
  onTransactionClick?: (id: number) => void;
}

export function TransactionList({ userId, onTransactionClick }: TransactionListProps) {
  const navigate = useNavigate();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTransactions();
  }, [userId]);

  const loadTransactions = async () => {
    setIsLoading(true);
    setError(null);
    
    try {
      // Carregar dados
    } catch (err) {
      setError('Erro ao carregar transações');
    } finally {
      setIsLoading(false);
    }
  };

  if (error) {
    return <div className="text-destructive">{error}</div>;
  }

  if (isLoading) {
    return <Loader2 className="animate-spin" />;
  }

  return (
    <Card className="p-4">
      {transactions.map((transaction) => (
        <div key={transaction.id}>
          {/* Conteúdo */}
        </div>
      ))}
    </Card>
  );
}
```

## 🎯 TypeScript

### Types vs Interfaces

```tsx
// Use type para tipos simples e unions
type TransactionType = 'INCOME' | 'EXPENSE';
type Status = 'pending' | 'completed' | 'failed';

// Use interface para objetos e props de componentes
interface Transaction {
  id: number;
  description: string;
  amount: number;
}

interface ButtonProps {
  onClick: () => void;
  children: React.ReactNode;
}
```

### Tipos Genéricos

```tsx
// Evite usar 'any'
// ❌ Ruim
function getData(): any {
  return fetch('/api/data');
}

// ✅ Bom
function getData<T>(): Promise<T> {
  return fetch('/api/data').then(res => res.json());
}

// Uso
const transactions = await getData<Transaction[]>();
```

### Assertions

```tsx
// Use 'as' apenas quando necessário
const element = document.getElementById('root') as HTMLElement;

// Prefira type guards
function isTransaction(obj: any): obj is Transaction {
  return typeof obj.id === 'number' && typeof obj.description === 'string';
}
```

## 💅 Tailwind CSS

### Ordem das Classes

Siga a ordem: Layout → Display → Espaçamento → Cores → Tipografia → Outros

```tsx
// ✅ Bom
<div className="flex items-center gap-4 p-6 bg-card rounded-lg border text-foreground">

// ❌ Ruim (ordem aleatória)
<div className="text-foreground rounded-lg flex p-6 border bg-card items-center gap-4">
```

### Classes Condicionais

```tsx
// Use template literals para condições simples
<div className={`p-4 ${isActive ? 'bg-primary' : 'bg-muted'}`}>

// Use biblioteca 'clsx' para múltiplas condições
import { clsx } from 'clsx';

<div className={clsx(
  'p-4 rounded-lg',
  isActive && 'bg-primary text-primary-foreground',
  isDisabled && 'opacity-50 cursor-not-allowed',
  size === 'lg' && 'text-lg'
)}>
```

### Variáveis CSS

```css
/* Use variáveis do theme.css */
.custom-element {
  color: var(--primary);
  background: var(--card);
  border-radius: var(--radius);
}
```

## 🔧 Hooks

### Custom Hooks

```tsx
// Sempre prefixe com 'use'
function useTransactions() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const loadTransactions = async () => {
    setIsLoading(true);
    try {
      const data = await api.getTransactions();
      setTransactions(data);
    } finally {
      setIsLoading(false);
    }
  };

  return { transactions, isLoading, loadTransactions };
}

// Uso
function MyComponent() {
  const { transactions, isLoading, loadTransactions } = useTransactions();
  
  useEffect(() => {
    loadTransactions();
  }, []);
}
```

### Dependencies Array

```tsx
// ✅ Bom - todas as dependências
useEffect(() => {
  fetchData(userId);
}, [userId]);

// ❌ Ruim - dependência faltando
useEffect(() => {
  fetchData(userId);
}, []); // ESLint vai avisar!
```

## 📝 Nomenclatura

### Variáveis e Funções

```tsx
// camelCase para variáveis e funções
const userName = 'João';
const totalAmount = 100;

function calculateBalance() {}
function handleSubmit() {}

// Booleanos começam com 'is', 'has', 'should'
const isLoading = true;
const hasError = false;
const shouldDisplay = true;

// Event handlers começam com 'handle'
const handleClick = () => {};
const handleChange = () => {};
const handleSubmit = () => {};
```

### Constantes

```tsx
// SCREAMING_SNAKE_CASE para constantes globais
const API_BASE_URL = 'http://localhost:8080';
const MAX_RETRIES = 3;
const DEFAULT_TIMEOUT = 5000;
```

### Componentes e Types

```tsx
// PascalCase para componentes e types
function TransactionCard() {}
interface UserProfile {}
type TransactionType = 'INCOME' | 'EXPENSE';
```

## 🎯 Estado e Dados

### Estado Local vs Context

```tsx
// ✅ Use useState para estado local do componente
function Counter() {
  const [count, setCount] = useState(0);
}

// ✅ Use Context para estado compartilhado
function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  // ... contexto compartilhado entre componentes
}
```

### Imutabilidade

```tsx
// ✅ Bom - cria novo objeto
setUser({ ...user, name: 'Novo Nome' });
setTransactions([...transactions, newTransaction]);

// ❌ Ruim - mutação direta
user.name = 'Novo Nome';
transactions.push(newTransaction);
```

## 🔄 Async/Await

```tsx
// ✅ Bom - com tratamento de erro
async function loadData() {
  try {
    const data = await api.getData();
    setData(data);
  } catch (error) {
    console.error('Erro:', error);
    setError('Erro ao carregar dados');
  } finally {
    setLoading(false);
  }
}

// ❌ Ruim - sem tratamento
async function loadData() {
  const data = await api.getData(); // Pode quebrar!
  setData(data);
}
```

## 📦 Imports

### Ordem dos Imports

```tsx
// 1. React e bibliotecas externas
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { Loader2, Check } from 'lucide-react';

// 2. Componentes locais
import { Button } from './ui/button';
import { Card } from './ui/card';

// 3. Contextos e hooks
import { useAuth } from '../contexts/AuthContext';

// 4. Serviços e utilitários
import { api } from '../services/api';
import { formatCurrency } from '../utils/format';

// 5. Types
import type { Transaction } from '../types';
```

## 🎨 Comentários

```tsx
// ✅ Bom - comentário útil explicando o "porquê"
// Aguarda 300ms após digitar antes de buscar (debounce)
const debouncedSearch = useDebounce(searchTerm, 300);

// ✅ Bom - documentação de função complexa
/**
 * Calcula o saldo total baseado nas transações
 * @param transactions - Array de transações
 * @returns Balance com total, income e expense
 */
function calculateBalance(transactions: Transaction[]): Balance {
  // ...
}

// ❌ Ruim - comentário óbvio
// Incrementa o contador
setCount(count + 1);
```

## ✅ Checklist de Code Review

Antes de fazer commit:

- [ ] Código está tipado (sem `any`)
- [ ] Componentes têm menos de 150 linhas
- [ ] Props têm interface definida
- [ ] Imports estão organizados
- [ ] Classes Tailwind estão ordenadas
- [ ] Funções têm nome descritivo
- [ ] Estado é imutável
- [ ] Erros são tratados
- [ ] Loading states estão implementados
- [ ] Código está formatado (Prettier)
- [ ] Não há console.logs esquecidos

## 🚀 Performance

```tsx
// ✅ Use React.memo para componentes pesados
export const TransactionList = React.memo(function TransactionList({ transactions }) {
  // ...
});

// ✅ Use useCallback para funções passadas como props
const handleDelete = useCallback((id: number) => {
  deleteTransaction(id);
}, [deleteTransaction]);

// ✅ Use useMemo para cálculos pesados
const balance = useMemo(() => {
  return calculateBalance(transactions);
}, [transactions]);
```

## 📱 Acessibilidade

```tsx
// ✅ Bom - com atributos de acessibilidade
<button
  aria-label="Excluir transação"
  onClick={handleDelete}
>
  <Trash2 />
</button>

// ✅ Bom - com label descritivo
<input
  id="amount"
  type="number"
  aria-describedby="amount-hint"
/>
<span id="amount-hint">Digite o valor em reais</span>
```

---

**Mantenha o código limpo, consistente e legível!** 🎯
