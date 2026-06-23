package com.emanuel.finance_control;

import com.emanuel.finance_control.model.Transaction;
import com.emanuel.finance_control.model.TransactionType;
import com.emanuel.finance_control.repository.TransactionRepository;
import com.emanuel.finance_control.service.TransactionService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class FinanceControlApplicationTests {

    private static final String TEST_USER = "test-user";

    @Autowired
    private TransactionService service;

    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    void limparDados() {
        repository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void deveRetornarSaldoZeroQuandoNaoHaTransacoes() {
        double saldo = service.calcularSaldo();
        assertEquals(0.0, saldo);
    }

    @Test
    void deveCalcularSaldoCorretamenteComIncomeEExpense() {
        Transaction income = new Transaction("Salario", 3000.0, TransactionType.INCOME, LocalDate.now());
        Transaction expense = new Transaction("Aluguel", 1200.0, TransactionType.EXPENSE, LocalDate.now());

        service.criar(income, TEST_USER);
        service.criar(expense, TEST_USER);

        double saldo = service.calcularSaldo();
        assertEquals(1800.0, saldo);
    }

    @Test
    void deveLancarExcecaoQuandoAmountNull() {
        Transaction transaction = new Transaction("Teste", null, TransactionType.INCOME, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.criar(transaction, TEST_USER));
    }

    @Test
    void deveLancarExcecaoQuandoAmountMenorOuIgualZero() {
        Transaction transaction = new Transaction("Teste", -100.0, TransactionType.INCOME, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.criar(transaction, TEST_USER));
    }

    @Test
    void deveLancarExcecaoQuandoDescriptionVazia() {
        Transaction transaction = new Transaction("", 100.0, TransactionType.INCOME, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.criar(transaction, TEST_USER));
    }

    @Test
    void deveLancarExcecaoQuandoTypeNull() {
        Transaction transaction = new Transaction("Teste", 100.0, null, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> service.criar(transaction, TEST_USER));
    }

    @Test
    void deveListarTransacoesPorPeriodo() {
        Transaction t1 = new Transaction("Salario", 3000.0, TransactionType.INCOME, LocalDate.of(2026, 3, 10));
        Transaction t2 = new Transaction("Bonus", 500.0, TransactionType.INCOME, LocalDate.of(2026, 4, 10));

        service.criar(t1, TEST_USER);
        service.criar(t2, TEST_USER);

        var resultado = service.listarPorPeriodoSimples(3, 2026);
        assertEquals(1, resultado.size());
        assertEquals("Salario", resultado.get(0).getDescription());
    }

    @Test
    void deveListarTransacoesPorCategoria() {
        Transaction t1 = new Transaction("Salario", 3000.0, TransactionType.INCOME, LocalDate.now());
        t1.setCategory("Trabalho");

        Transaction t2 = new Transaction("Aluguel", 1200.0, TransactionType.EXPENSE, LocalDate.now());
        t2.setCategory("Moradia");

        service.criar(t1, TEST_USER);
        service.criar(t2, TEST_USER);

        var resultado = service.listarPorCategoria("Trabalho");
        assertEquals(1, resultado.size());
        assertEquals("Salario", resultado.get(0).getDescription());
    }
}
