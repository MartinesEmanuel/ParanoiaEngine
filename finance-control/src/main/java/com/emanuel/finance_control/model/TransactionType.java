package com.emanuel.finance_control.model;

public enum TransactionType {
    INCOME("Receita"),
    EXPENSE("Despesa");

    private final String descricao;

    TransactionType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
