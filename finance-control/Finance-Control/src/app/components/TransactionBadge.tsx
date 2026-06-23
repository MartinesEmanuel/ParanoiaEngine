import { Badge } from './ui/badge';
import type { TransactionType } from '../types';

interface TransactionBadgeProps {
  type: TransactionType;
  size?: 'default' | 'sm' | 'lg';
}

export function TransactionBadge({ type, size = 'default' }: TransactionBadgeProps) {
  const isIncome = type === 'INCOME';
  
  return (
    <Badge 
      variant={isIncome ? 'default' : 'destructive'}
      className={`
        ${isIncome ? 'bg-success/10 text-success hover:bg-success/20' : 'bg-destructive/10 text-destructive hover:bg-destructive/20'}
        border-0
        ${size === 'sm' ? 'text-xs px-2 py-0' : ''}
        ${size === 'lg' ? 'text-base px-4 py-1' : ''}
      `}
    >
      {isIncome ? 'Receita' : 'Despesa'}
    </Badge>
  );
}
