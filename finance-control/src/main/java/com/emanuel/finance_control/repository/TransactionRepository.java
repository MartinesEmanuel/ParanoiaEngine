package com.emanuel.finance_control.repository;

import com.emanuel.finance_control.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT SUM(
                CASE
                    WHEN t.type = 'INCOME' THEN t.amount
                    ELSE -t.amount
                END
            )
            FROM Transaction t
            WHERE t.date BETWEEN :start AND :end
            """)
    Double sumByPeriod(@Param("start") LocalDate start,
                       @Param("end") LocalDate end);

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.date BETWEEN :start AND :end
            """)
    List<Transaction> findByDateBetween(@Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    List<Transaction> findByCategory(String category);
}
