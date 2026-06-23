package com.emanuel.finance_control.service;

import com.emanuel.finance_control.exception.ResourceNotFoundException;
import com.emanuel.finance_control.model.Transaction;
import com.emanuel.finance_control.model.TransactionType;
import com.emanuel.finance_control.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> listarTodas() {
        return repository.findAll();
    }

    public Transaction buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
    }

    public Transaction criar(Transaction transaction, String username) {
        validarTransacao(transaction);
        transaction.setCreatedBy(username);
        return repository.save(transaction);
    }

    public Transaction atualizar(Long id, Transaction incoming) {
        validarTransacao(incoming);
        Transaction existing = buscarPorId(id);

        existing.setDescription(incoming.getDescription());
        existing.setAmount(incoming.getAmount());
        existing.setType(incoming.getType());
        existing.setDate(incoming.getDate());
        existing.setCategory(incoming.getCategory());

        return repository.save(existing);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found: " + id);
        }
        repository.deleteById(id);
    }

    public double calcularSaldo() {
        return repository.findAll().stream()
                .filter(transaction -> transaction.getAmount() != null && transaction.getType() != null)
                .mapToDouble(transaction -> transaction.getType() == TransactionType.INCOME
                        ? transaction.getAmount()
                        : -transaction.getAmount())
                .sum();
    }

    public List<Transaction> listarPorPeriodo(LocalDate start, LocalDate end) {
        validarPeriodo(start, end);
        return repository.findByDateBetween(start, end);
    }

    public double calcularSaldoPorPeriodo(LocalDate start, LocalDate end) {
        validarPeriodo(start, end);
        Double value = repository.sumByPeriod(start, end);
        return value == null ? 0.0 : value;
    }

    public List<Transaction> listarPorPeriodoSimples(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return repository.findByDateBetween(start, end);
    }

    public List<Transaction> listarPorCategoria(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category must not be blank");
        }
        return repository.findByCategory(category);
    }

    private void validarTransacao(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }
    }

    private void validarPeriodo(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be equal or after start date");
        }
    }
}
