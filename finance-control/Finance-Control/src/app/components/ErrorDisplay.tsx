import { AlertCircle, XCircle, WifiOff, Lock } from 'lucide-react';
import { Card } from './ui/card';
import { Button } from './ui/button';

interface ErrorDisplayProps {
  status: number;
  title?: string;
  message?: string;
  onRetry?: () => void;
}

export function ErrorDisplay({ status, title, message, onRetry }: ErrorDisplayProps) {
  const errorConfig = {
    400: {
      icon: AlertCircle,
      defaultTitle: 'Dados Inválidos',
      defaultMessage: 'Por favor, verifique os campos obrigatórios e tente novamente.',
      color: 'text-warning'
    },
    401: {
      icon: Lock,
      defaultTitle: 'Não Autorizado',
      defaultMessage: 'Sua sessão expirou. Por favor, faça login novamente.',
      color: 'text-destructive'
    },
    404: {
      icon: XCircle,
      defaultTitle: 'Não Encontrado',
      defaultMessage: 'O recurso solicitado não foi encontrado.',
      color: 'text-muted-foreground'
    },
    500: {
      icon: WifiOff,
      defaultTitle: 'Erro no Servidor',
      defaultMessage: 'Ocorreu um erro no servidor. Tente novamente mais tarde.',
      color: 'text-destructive'
    }
  };

  const config = errorConfig[status as keyof typeof errorConfig] || errorConfig[500];
  const Icon = config.icon;

  return (
    <Card className="p-8 max-w-md mx-auto">
      <div className="flex flex-col items-center text-center space-y-4">
        <div className={`rounded-full bg-muted p-4 ${config.color}`}>
          <Icon className="w-12 h-12" />
        </div>
        <div className="space-y-2">
          <h3 className="text-xl font-semibold">
            {title || config.defaultTitle}
          </h3>
          <p className="text-sm text-muted-foreground">
            {message || config.defaultMessage}
          </p>
          {status !== 404 && (
            <p className="text-xs text-muted-foreground">
              Código do erro: {status}
            </p>
          )}
        </div>
        {onRetry && (
          <Button onClick={onRetry} className="mt-4">
            Tentar Novamente
          </Button>
        )}
      </div>
    </Card>
  );
}
