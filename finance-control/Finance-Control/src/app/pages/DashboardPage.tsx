import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { 
  Wallet, 
  TrendingUp, 
  TrendingDown, 
  Plus,
  Filter,
  Calendar
} from 'lucide-react';
import { AppLayout } from '../components/AppLayout';
import { KPICard } from '../components/KPICard';
import { TransactionBadge } from '../components/TransactionBadge';
import { EmptyState } from '../components/EmptyState';
import { ErrorDisplay } from '../components/ErrorDisplay';
import { Button } from '../components/ui/button';
import { Card } from '../components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';
import { transactionsApi, ApiError } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import type { Transaction, Balance } from '../types';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

export function DashboardPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [balance, setBalance] = useState<Balance | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<{ status: number; message: string } | null>(null);
  
  const currentDate = new Date();
  const [selectedMonth, setSelectedMonth] = useState(currentDate.getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());

  useEffect(() => {
    loadDashboardData();
  }, [selectedMonth, selectedYear]);

  const loadDashboardData = async () => {
    setIsLoading(true);
    setError(null);

    const start = new Date(selectedYear, selectedMonth - 1, 1);
    const end = new Date(selectedYear, selectedMonth, 0);
    const startDate = start.toISOString().split('T')[0];
    const endDate = end.toISOString().split('T')[0];

    try {
      const [transactionsData, balanceData] = await Promise.all([
        transactionsApi.getBySimplePeriod(selectedMonth, selectedYear),
        transactionsApi.getBalanceByPeriod(startDate, endDate)
      ]);

      const income = transactionsData
        .filter((transaction) => transaction.type === 'INCOME')
        .reduce((sum, transaction) => sum + transaction.amount, 0);

      const expense = transactionsData
        .filter((transaction) => transaction.type === 'EXPENSE')
        .reduce((sum, transaction) => sum + transaction.amount, 0);

      setTransactions(transactionsData.slice(0, 5)); // Last 5 transactions
      setBalance({
        total: balanceData.total,
        income,
        expense
      });
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          logout();
          navigate('/login');
          return;
        }
        setError({
          status: err.status,
          message: err.problemDetail.detail || err.problemDetail.title
        });
      } else {
        setError({
          status: 500,
          message: 'Erro ao carregar dados do dashboard'
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const formatDate = (date: string) => {
    return format(new Date(date), "dd 'de' MMM", { locale: ptBR });
  };

  const months = [
    { value: '1', label: 'Janeiro' },
    { value: '2', label: 'Fevereiro' },
    { value: '3', label: 'Março' },
    { value: '4', label: 'Abril' },
    { value: '5', label: 'Maio' },
    { value: '6', label: 'Junho' },
    { value: '7', label: 'Julho' },
    { value: '8', label: 'Agosto' },
    { value: '9', label: 'Setembro' },
    { value: '10', label: 'Outubro' },
    { value: '11', label: 'Novembro' },
    { value: '12', label: 'Dezembro' }
  ];

  const years = Array.from({ length: 5 }, (_, i) => currentDate.getFullYear() - i);

  if (error) {
    return (
      <AppLayout>
        <ErrorDisplay
          status={error.status}
          message={error.message}
          onRetry={loadDashboardData}
        />
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="space-y-8">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-3xl font-semibold tracking-tight">Dashboard</h1>
            <p className="text-muted-foreground mt-1">
              Visão geral das suas finanças
            </p>
          </div>
          <Button onClick={() => navigate('/transactions/new')}>
            <Plus className="mr-2 h-4 w-4" />
            Adicionar Transação
          </Button>
        </div>

        {/* Filters */}
        <Card className="p-4">
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center">
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Filter className="h-4 w-4" />
              Período:
            </div>
            <div className="flex gap-2 flex-1">
              <Select
                value={selectedMonth.toString()}
                onValueChange={(value) => setSelectedMonth(parseInt(value))}
              >
                <SelectTrigger className="w-[180px]">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {months.map((month) => (
                    <SelectItem key={month.value} value={month.value}>
                      {month.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              
              <Select
                value={selectedYear.toString()}
                onValueChange={(value) => setSelectedYear(parseInt(value))}
              >
                <SelectTrigger className="w-[120px]">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {years.map((year) => (
                    <SelectItem key={year} value={year.toString()}>
                      {year}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </Card>

        {/* KPI Cards */}
        <div className="grid gap-4 md:grid-cols-3">
          <KPICard
            title="Saldo Total"
            value={formatCurrency(balance?.total || 0)}
            icon={Wallet}
            variant="default"
            isLoading={isLoading}
          />
          <KPICard
            title="Total de Receitas"
            value={formatCurrency(balance?.income || 0)}
            icon={TrendingUp}
            variant="success"
            isLoading={isLoading}
          />
          <KPICard
            title="Total de Despesas"
            value={formatCurrency(balance?.expense || 0)}
            icon={TrendingDown}
            variant="danger"
            isLoading={isLoading}
          />
        </div>

        {/* Recent Transactions */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-xl font-semibold">Transações Recentes</h2>
              <p className="text-sm text-muted-foreground mt-1">
                Últimas transações do período selecionado
              </p>
            </div>
            <Button variant="outline" onClick={() => navigate('/transactions')}>
              Ver Todas
            </Button>
          </div>

          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex items-center gap-4 py-4 border-b last:border-0 animate-pulse">
                  <div className="h-10 w-10 bg-muted rounded-full" />
                  <div className="flex-1 space-y-2">
                    <div className="h-4 bg-muted rounded w-1/3" />
                    <div className="h-3 bg-muted rounded w-1/4" />
                  </div>
                  <div className="h-6 w-20 bg-muted rounded" />
                </div>
              ))}
            </div>
          ) : transactions.length === 0 ? (
            <EmptyState
              icon={Calendar}
              title="Nenhuma transação encontrada"
              description="Não há transações para o período selecionado. Adicione sua primeira transação para começar."
              action={{
                label: 'Adicionar Transação',
                onClick: () => navigate('/transactions/new')
              }}
            />
          ) : (
            <div className="space-y-4">
              {transactions.map((transaction) => (
                <div
                  key={transaction.id}
                  className="flex items-center gap-4 py-4 border-b last:border-0 hover:bg-muted/50 transition-colors rounded-lg px-2 cursor-pointer"
                  onClick={() => navigate(`/transactions/${transaction.id}`)}
                >
                  <div className={`h-10 w-10 rounded-full flex items-center justify-center ${
                    transaction.type === 'INCOME' 
                      ? 'bg-success/10 text-success' 
                      : 'bg-destructive/10 text-destructive'
                  }`}>
                    {transaction.type === 'INCOME' ? (
                      <TrendingUp className="h-5 w-5" />
                    ) : (
                      <TrendingDown className="h-5 w-5" />
                    )}
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <p className="font-medium truncate">{transaction.description}</p>
                    <div className="flex items-center gap-2 mt-1">
                      <p className="text-xs text-muted-foreground">
                        {formatDate(transaction.date)}
                      </p>
                      {transaction.category && (
                        <>
                          <span className="text-xs text-muted-foreground">•</span>
                          <p className="text-xs text-muted-foreground truncate">
                            {transaction.category}
                          </p>
                        </>
                      )}
                    </div>
                  </div>
                  
                  <div className="text-right">
                    <p className={`font-semibold ${
                      transaction.type === 'INCOME' ? 'text-success' : 'text-destructive'
                    }`}>
                      {transaction.type === 'INCOME' ? '+' : '-'} {formatCurrency(transaction.amount)}
                    </p>
                    <TransactionBadge type={transaction.type} size="sm" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </AppLayout>
  );
}
