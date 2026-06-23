import type { Transaction, Balance, AuthResponse } from '../types';

// Mock data for development without backend

export const mockUser: AuthResponse = {
  token: 'mock-jwt-token-12345',
  username: 'usuario.demo'
};

export const mockTransactions: Transaction[] = [
  {
    id: 1,
    description: 'Salário Mensal',
    amount: 5500.00,
    type: 'INCOME',
    date: '2026-03-01',
    category: 'Trabalho',
    createdAt: '2026-03-01T09:00:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 2,
    description: 'Aluguel',
    amount: 1200.00,
    type: 'EXPENSE',
    date: '2026-03-05',
    category: 'Moradia',
    createdAt: '2026-03-05T10:30:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 3,
    description: 'Supermercado',
    amount: 450.00,
    type: 'EXPENSE',
    date: '2026-03-10',
    category: 'Alimentação',
    createdAt: '2026-03-10T14:20:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 4,
    description: 'Freelance Design',
    amount: 800.00,
    type: 'INCOME',
    date: '2026-03-12',
    category: 'Trabalho',
    createdAt: '2026-03-12T16:45:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 5,
    description: 'Academia',
    amount: 89.90,
    type: 'EXPENSE',
    date: '2026-03-15',
    category: 'Saúde',
    createdAt: '2026-03-15T08:00:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 6,
    description: 'Cinema',
    amount: 60.00,
    type: 'EXPENSE',
    date: '2026-03-18',
    category: 'Lazer',
    createdAt: '2026-03-18T20:30:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 7,
    description: 'Venda Produto',
    amount: 350.00,
    type: 'INCOME',
    date: '2026-03-20',
    category: 'Vendas',
    createdAt: '2026-03-20T11:15:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 8,
    description: 'Uber',
    amount: 45.00,
    type: 'EXPENSE',
    date: '2026-03-22',
    category: 'Transporte',
    createdAt: '2026-03-22T18:00:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 9,
    description: 'Restaurante',
    amount: 120.00,
    type: 'EXPENSE',
    date: '2026-03-23',
    category: 'Alimentação',
    createdAt: '2026-03-23T19:30:00Z',
    createdBy: 'usuario.demo'
  },
  {
    id: 10,
    description: 'Investimento Dividendos',
    amount: 250.00,
    type: 'INCOME',
    date: '2026-03-25',
    category: 'Investimentos',
    createdAt: '2026-03-25T10:00:00Z',
    createdBy: 'usuario.demo'
  }
];

export const mockBalance: Balance = {
  total: 4935.10,
  income: 6900.00,
  expense: 1964.90
};

// Helper function to calculate balance from transactions
export function calculateMockBalance(transactions: Transaction[]): Balance {
  const income = transactions
    .filter(t => t.type === 'INCOME')
    .reduce((sum, t) => sum + t.amount, 0);
  
  const expense = transactions
    .filter(t => t.type === 'EXPENSE')
    .reduce((sum, t) => sum + t.amount, 0);
  
  return {
    total: income - expense,
    income,
    expense
  };
}
