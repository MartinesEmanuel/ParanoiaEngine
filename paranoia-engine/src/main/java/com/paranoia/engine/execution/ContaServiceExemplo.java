package com.paranoia.engine.execution;

import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ContaServiceExemplo {

    private static final Logger log = LoggerFactory.getLogger(ContaServiceExemplo.class);

    private int saldo;

    public ContaServiceExemplo() {
        this.saldo = 1000;
    }

    public void debitarSemLock(double valor) {
        if (saldo >= valor) {
            simularAtraso();
            saldo -= valor;
        }
    }

    public void creditar(double valor) {
        int atual = saldo;
        simularAtraso();
        saldo = atual + (int) valor;
    }

    public int getSaldo() {
        return saldo;
    }

    public void reset() {
        saldo = 1000;
    }

    private void simularAtraso() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
