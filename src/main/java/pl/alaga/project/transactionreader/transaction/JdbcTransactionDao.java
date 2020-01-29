package pl.alaga.project.transactionreader.transaction;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTransactionDao implements TransactionDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTransactionDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<PaymentTransaction> getAllTransactions() {
        return namedParameterJdbcTemplate.query("SELECT * FROM payment_transaction ORDER BY id",
                (rs, rowNumber) -> new PaymentTransaction(
                        rs.getInt("transaction_id"),
                        rs.getDate("transaction_date"),
                        rs.getString("title"),
                        rs.getBigDecimal("amount_pln"),
                        rs.getBigDecimal("amount_eur"))
        );

    }

    @Override
    public void addTransactions(List<PaymentTransaction> transactions) {
        List<Map<String, Object>> batchValues = new ArrayList<>(transactions.size());
        for (PaymentTransaction transaction : transactions) {
            batchValues.add(
                    new MapSqlParameterSource("transaction_id", transaction.getTransactionId())
                            .addValue("transaction_date", transaction.getTransactionDate())
                            .addValue("title", transaction.getTitle())
                            .addValue("amount_pln", transaction.getAmountPln())
                            .addValue("amount_eur", transaction.getAmountEur())
                            .getValues());
        }

        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(
                "insert into payment_transaction (transaction_id, transaction_date, title, amount_pln, amount_eur) " +
                        "values (:transaction_id, :transaction_date, :title, :amount_pln, :amount_eur)",
                batchValues.toArray(new Map[transactions.size()]));
    }
}
