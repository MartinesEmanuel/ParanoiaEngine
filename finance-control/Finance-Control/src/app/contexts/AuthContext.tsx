import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authApi, ApiError } from '../services/api';
import type { User, LoginInput, RegisterInput } from '../types';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (credentials: LoginInput) => Promise<void>;
  register: (data: RegisterInput) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on mount
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (token && username) {
      setUser({ username });
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginInput) => {
    try {
      const response = await authApi.login(credentials);
      localStorage.setItem('token', response.token);
      localStorage.setItem('username', response.username);
      setUser({ username: response.username });
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new Error('Erro ao fazer login');
    }
  };

  const register = async (data: RegisterInput) => {
    try {
      const response = await authApi.register(data);
      localStorage.setItem('token', response.token);
      localStorage.setItem('username', response.username);
      setUser({ username: response.username });
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new Error('Erro ao criar conta');
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        login,
        register,
        logout,
        isAuthenticated: !!user
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
