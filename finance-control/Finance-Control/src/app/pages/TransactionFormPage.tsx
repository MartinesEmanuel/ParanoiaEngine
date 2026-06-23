import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router';
import { ArrowLeft, Loader2, Save } from 'lucide-react';
import { AppLayout } from '../components/AppLayout';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
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
import type { TransactionInput, TransactionType } from '../types';
import { toast } from 'sonner';

export function TransactionFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { logout } = useAuth();
  const isEditMode = !!id;

  const [isLoading, setIsLoading] = useState(false);
  const [isFetching, setIsFetching] = useState(isEditMode);
  const [formData, setFormData] = useState<TransactionInput>({
    description: '',
    amount: 0,
    type: 'EXPENSE',
    date: new Date().toISOString().split('T')[0],
    category: ''
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isEditMode) {
      loadTransaction();
    }
  }, [id]);

  const loadTransaction = async () => {
    if (!id) return;

    setIsFetching(true);
    try {
      const transaction = await transactionsApi.getById(parseInt(id));
      setFormData({
        description: transaction.description,
        amount: transaction.amount,
        type: transaction.type,
        date: transaction.date,
        category: transaction.category || ''
      });
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          logout();
          navigate('/login');
          return;
        }
        if (err.status === 404) {
          toast.error('Transação não encontrada');
          navigate('/transactions');
          return;
        }
        toast.error(err.problemDetail.detail || err.problemDetail.title);
      } else {
        toast.error('Erro ao carregar transação');
      }
      navigate('/transactions');
    } finally {
      setIsFetching(false);
    }
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.description.trim()) {
      newErrors.description = 'Descrição é obrigatória';
    }

    if (formData.amount <= 0) {
      newErrors.amount = 'Valor deve ser maior que zero';
    }

    if (!formData.type) {
      newErrors.type = 'Tipo é obrigatório';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) {
      toast.error('Verifique os campos obrigatórios');
      return;
    }

    setIsLoading(true);

    try {
      const dataToSubmit = {
        ...formData,
        category: formData.category?.trim() || undefined
      };

      if (isEditMode && id) {
        await transactionsApi.update(parseInt(id), dataToSubmit);
        toast.success('Transação atualizada com sucesso');
      } else {
        await transactionsApi.create(dataToSubmit);
        toast.success('Transação criada com sucesso');
      }
      
      navigate('/transactions');
    } catch (err) {
      if (err instanceof ApiError) {
        if (err.status === 401) {
          logout();
          navigate('/login');
          return;
        }
        toast.error(err.problemDetail.detail || err.problemDetail.title);
      } else {
        toast.error(isEditMode ? 'Erro ao atualizar transação' : 'Erro ao criar transação');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (field: keyof TransactionInput, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    // Clear error when field changes
    if (errors[field]) {
      setErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  if (isFetching) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="max-w-2xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate('/transactions')}
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>
          <div>
            <h1 className="text-3xl font-semibold tracking-tight">
              {isEditMode ? 'Editar Transação' : 'Nova Transação'}
            </h1>
            <p className="text-muted-foreground mt-1">
              {isEditMode 
                ? 'Atualize os dados da transação' 
                : 'Adicione uma nova transação ao seu controle financeiro'
              }
            </p>
          </div>
        </div>

        {/* Form */}
        <Card className="p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">
                Descrição <span className="text-destructive">*</span>
              </Label>
              <Input
                id="description"
                value={formData.description}
                onChange={(e) => handleChange('description', e.target.value)}
                placeholder="Ex: Salário, Aluguel, Supermercado..."
                disabled={isLoading}
                className={errors.description ? 'border-destructive' : ''}
              />
              {errors.description && (
                <p className="text-sm text-destructive">{errors.description}</p>
              )}
            </div>

            {/* Amount */}
            <div className="space-y-2">
              <Label htmlFor="amount">
                Valor <span className="text-destructive">*</span>
              </Label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                  R$
                </span>
                <Input
                  id="amount"
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={formData.amount || ''}
                  onChange={(e) => handleChange('amount', parseFloat(e.target.value) || 0)}
                  placeholder="0,00"
                  disabled={isLoading}
                  className={`pl-10 ${errors.amount ? 'border-destructive' : ''}`}
                />
              </div>
              {errors.amount && (
                <p className="text-sm text-destructive">{errors.amount}</p>
              )}
            </div>

            {/* Type and Date */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="type">
                  Tipo <span className="text-destructive">*</span>
                </Label>
                <Select
                  value={formData.type}
                  onValueChange={(value: TransactionType) => handleChange('type', value)}
                  disabled={isLoading}
                >
                  <SelectTrigger id="type" className={errors.type ? 'border-destructive' : ''}>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="INCOME">
                      <div className="flex items-center gap-2">
                        <span className="h-2 w-2 rounded-full bg-success" />
                        Receita
                      </div>
                    </SelectItem>
                    <SelectItem value="EXPENSE">
                      <div className="flex items-center gap-2">
                        <span className="h-2 w-2 rounded-full bg-destructive" />
                        Despesa
                      </div>
                    </SelectItem>
                  </SelectContent>
                </Select>
                {errors.type && (
                  <p className="text-sm text-destructive">{errors.type}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="date">Data</Label>
                <Input
                  id="date"
                  type="date"
                  value={formData.date}
                  onChange={(e) => handleChange('date', e.target.value)}
                  disabled={isLoading}
                />
              </div>
            </div>

            {/* Category */}
            <div className="space-y-2">
              <Label htmlFor="category">Categoria (opcional)</Label>
              <Input
                id="category"
                value={formData.category}
                onChange={(e) => handleChange('category', e.target.value)}
                placeholder="Ex: Trabalho, Alimentação, Lazer..."
                disabled={isLoading}
              />
              <p className="text-xs text-muted-foreground">
                Categorize suas transações para facilitar a organização
              </p>
            </div>

            {/* Preview Card */}
            <Card className="p-4 bg-muted/50">
              <p className="text-sm font-medium mb-2">Pré-visualização:</p>
              <div className="space-y-1">
                <p className="text-lg font-semibold">
                  {formData.description || 'Descrição'}
                </p>
                <p className={`text-2xl font-bold ${
                  formData.type === 'INCOME' ? 'text-success' : 'text-destructive'
                }`}>
                  {formData.type === 'INCOME' ? '+' : '-'} R$ {formData.amount.toFixed(2)}
                </p>
                <p className="text-sm text-muted-foreground">
                  {formData.category || 'Sem categoria'} • {formData.date || 'Hoje'}
                </p>
              </div>
            </Card>

            {/* Actions */}
            <div className="flex gap-3 pt-4">
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/transactions')}
                disabled={isLoading}
                className="flex-1"
              >
                Cancelar
              </Button>
              <Button
                type="submit"
                disabled={isLoading}
                className="flex-1"
              >
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Salvando...
                  </>
                ) : (
                  <>
                    <Save className="mr-2 h-4 w-4" />
                    {isEditMode ? 'Atualizar' : 'Criar'} Transação
                  </>
                )}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </AppLayout>
  );
}
