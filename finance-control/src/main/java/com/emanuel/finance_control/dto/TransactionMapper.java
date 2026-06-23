package com.emanuel.finance_control.dto;

import com.emanuel.finance_control.model.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getCategory(),
                transaction.getCreatedAt(),
                transaction.getCreatedBy()
        );
    }

    public static Transaction toEntity(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());
        transaction.setCategory(request.category());
        return transaction;
    }
}
