package com.emanuel.finance_control.dto;

import com.emanuel.finance_control.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record TransactionRequest(
        @NotBlank String description,
        @NotNull @Positive Double amount,
        @NotNull TransactionType type,
        LocalDate date,
        String category
) {
}

