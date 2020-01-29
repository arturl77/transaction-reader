package pl.alaga.project.transactionreader.currency;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {

    public Optional<ExchangeRate> getRate(String currency, Date checkDate);

    public void addRates(List<ExchangeRate> rates);

}
