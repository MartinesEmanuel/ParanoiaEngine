package com.exemplo;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ArrayList;

public class ContaService {

    private RestTemplate restTemplate;
    private double saldo;
    private final String constante = "fixo";
    private List<String> auditLog;

    public ContaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.auditLog = new ArrayList<>();
    }

    @Transactional
    public void debitar(double valor) {
        if (saldo >= valor) {
            saldo -= valor;
            restTemplate.postForEntity("http://ledger/api/debito", null, Void.class);
        }
    }

    @Transactional
    public void creditar(double valor) {
        saldo += valor;
    }

    public double getSaldo() {
        return saldo;
    }

    public void reset() {
        saldo = 0;
        auditLog = new ArrayList<>();
    }
}
