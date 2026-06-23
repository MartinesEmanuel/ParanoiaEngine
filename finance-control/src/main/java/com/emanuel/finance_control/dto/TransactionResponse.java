package com.emanuel.finance_control.dto;

import com.emanuel.finance_control.model.TransactionType;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        String description,
        Double amount,
        TransactionType type,
        LocalDate date,
        String category,
        LocalDate createdAt,
        String createdBy
) {
}

