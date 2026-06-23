package com.emanuel.finance_control.controller;

import com.emanuel.finance_control.dto.BalanceResponse;
import com.emanuel.finance_control.dto.TransactionMapper;
import com.emanuel.finance_control.dto.TransactionRequest;
import com.emanuel.finance_control.dto.TransactionResponse;
import com.emanuel.finance_control.model.Transaction;
import com.emanuel.finance_control.service.TransactionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TransactionResponse> listar() {
        return service.listarTodas().stream().map(TransactionMapper::toResponse).toList();
    }

    @GetMapping("/balance")
    public BalanceResponse buscarSaldo() {
        return new BalanceResponse(service.calcularSaldo());
    }

    @GetMapping("/balance/period")
    public BalanceResponse buscarSaldoPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return new BalanceResponse(service.calcularSaldoPorPeriodo(start, end));
    }

    @GetMapping("/{id}")
    public TransactionResponse buscarPorId(@PathVariable Long id) {
        return TransactionMapper.toResponse(service.buscarPorId(id));
    }

    @PostMapping
    public TransactionResponse criar(@Valid @RequestBody TransactionRequest request, Authentication authentication) {
        Transaction entity = TransactionMapper.toEntity(request);
        String username = authentication == null ? "anonymous" : authentication.getName();
        return TransactionMapper.toResponse(service.criar(entity, username));
    }

    @PutMapping("/{id}")
    public TransactionResponse atualizar(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return TransactionMapper.toResponse(service.atualizar(id, TransactionMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/period")
    public List<TransactionResponse> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return service.listarPorPeriodo(start, end).stream().map(TransactionMapper::toResponse).toList();
    }

    @GetMapping("/simplePeriod")
    public List<TransactionResponse> listarPorPeriodoSimples(@RequestParam int month, @RequestParam int year) {
        return service.listarPorPeriodoSimples(month, year).stream().map(TransactionMapper::toResponse).toList();
    }

    @GetMapping("/category")
    public List<TransactionResponse> listarPorCategoria(@RequestParam String category) {
        return service.listarPorCategoria(category).stream().map(TransactionMapper::toResponse).toList();
    }
}
