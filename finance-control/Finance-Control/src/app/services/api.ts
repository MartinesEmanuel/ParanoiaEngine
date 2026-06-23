import type { 
  Transaction, 
  TransactionInput, 
  Balance, 
  AuthResponse, 
  RegisterInput, 
  LoginInput,
  ProblemDetail 
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:9000';

class ApiError extends Error {
  constructor(public status: number, public problemDetail: ProblemDetail) {
    super(problemDetail.detail || problemDetail.title);
    this.name = 'ApiError';
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    if (response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
    }

    let problemDetail: ProblemDetail;
    try {
      problemDetail = await response.json();
    } catch {
      problemDetail = {
        title: 'Erro de Comunicação',
        status: response.status,
        detail: 'Não foi possível processar a resposta do servidor'
      };
    }
    throw new ApiError(response.status, problemDetail);
  }

  // Handle 204 No Content
  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

function normalizeBalance(payload: unknown): Balance {
  if (typeof payload !== 'object' || payload === null) {
    return { total: 0, income: 0, expense: 0 };
  }

  const data = payload as Record<string, unknown>;

  // Backend atual envia { balance }, mas o front usa total/income/expense.
  if (typeof data.balance === 'number') {
    return {
      total: data.balance,
      income: 0,
      expense: 0
    };
  }

  return {
    total: typeof data.total === 'number' ? data.total : 0,
    income: typeof data.income === 'number' ? data.income : 0,
    expense: typeof data.expense === 'number' ? data.expense : 0
  };
}

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
  };
}

// Auth API
export const authApi = {
  register: async (data: RegisterInput): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return handleResponse<AuthResponse>(response);
  },

  login: async (data: LoginInput): Promise<AuthResponse> => {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return handleResponse<AuthResponse>(response);
  }
};

// Transactions API
export const transactionsApi = {
  getAll: async (): Promise<Transaction[]> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions`, {
      headers: getAuthHeaders()
    });
    return handleResponse<Transaction[]>(response);
  },

  getById: async (id: number): Promise<Transaction> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions/${id}`, {
      headers: getAuthHeaders()
    });
    return handleResponse<Transaction>(response);
  },

  create: async (data: TransactionInput): Promise<Transaction> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data)
    });
    return handleResponse<Transaction>(response);
  },

  update: async (id: number, data: TransactionInput): Promise<Transaction> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(data)
    });
    return handleResponse<Transaction>(response);
  },

  delete: async (id: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    return handleResponse<void>(response);
  },

  getBalance: async (): Promise<Balance> => {
    const response = await fetch(`${API_BASE_URL}/api/transactions/balance`, {
      headers: getAuthHeaders()
    });
    const payload = await handleResponse<unknown>(response);
    return normalizeBalance(payload);
  },

  getBalanceByPeriod: async (start: string, end: string): Promise<Balance> => {
    const response = await fetch(
      `${API_BASE_URL}/api/transactions/balance/period?start=${start}&end=${end}`,
      { headers: getAuthHeaders() }
    );
    const payload = await handleResponse<unknown>(response);
    return normalizeBalance(payload);
  },

  getByPeriod: async (start: string, end: string): Promise<Transaction[]> => {
    const response = await fetch(
      `${API_BASE_URL}/api/transactions/period?start=${start}&end=${end}`,
      { headers: getAuthHeaders() }
    );
    return handleResponse<Transaction[]>(response);
  },

  getBySimplePeriod: async (month: number, year: number): Promise<Transaction[]> => {
    const response = await fetch(
      `${API_BASE_URL}/api/transactions/simplePeriod?month=${month}&year=${year}`,
      { headers: getAuthHeaders() }
    );
    return handleResponse<Transaction[]>(response);
  },

  getByCategory: async (category: string): Promise<Transaction[]> => {
    const response = await fetch(
      `${API_BASE_URL}/api/transactions/category?category=${encodeURIComponent(category)}`,
      { headers: getAuthHeaders() }
    );
    return handleResponse<Transaction[]>(response);
  }
};

export { ApiError };
