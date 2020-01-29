package pl.alaga.project.transactionreader.currency;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcExchangeRateDao implements ExchangeRateDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcExchangeRateDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<ExchangeRate> getRate(String currency, Date checkDate) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("currency", currency)
                .addValue("checkDate", checkDate);

        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM exchange_rate WHERE currency=:currency AND :checkDate BETWEEN start_date AND end_date ORDER BY id DESC LIMIT 1",
                    namedParameters,
                    (rs, rowNumber) -> new ExchangeRate(rs.getString("currency"), rs.getBigDecimal("rate"), rs.getDate("start_date"), rs.getDate("end_date")))
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addRates(List<ExchangeRate> rates) {
        List<Map<String, Object>> batchValues = new ArrayList<>(rates.size());
        for (ExchangeRate rate : rates) {
            batchValues.add(
                    new MapSqlParameterSource("currency", rate.getCurrency())
                            .addValue("start_date", rate.getStartDate())
                            .addValue("end_date", rate.getEndDate())
                            .addValue("rate", rate.getRate())
                            .getValues());
        }

        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(
                "insert into exchange_rate (currency, start_date, end_date, rate) values (:currency, :start_date, :end_date, :rate)",
                batchValues.toArray(new Map[rates.size()]));
    }
}
