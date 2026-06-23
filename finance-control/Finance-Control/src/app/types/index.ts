// Types for Finance Control API

export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  description: string;
  amount: number;
  type: TransactionType;
  date: string;
  category: string;
  createdAt: string;
  createdBy: string;
}

export interface TransactionInput {
  description: string;
  amount: number;
  type: TransactionType;
  date?: string;
  category?: string;
}

export interface Balance {
  total: number;
  income: number;
  expense: number;
}

export interface User {
  username: string;
}

export interface AuthResponse {
  token: string;
  username: string;
}

export interface RegisterInput {
  username: string;
  password: string;
}

export interface LoginInput {
  username: string;
  password: string;
}

export interface ProblemDetail {
  type?: string;
  title: string;
  status: number;
  detail?: string;
  instance?: string;
}
