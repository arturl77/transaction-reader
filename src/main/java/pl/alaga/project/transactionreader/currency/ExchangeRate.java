package pl.alaga.project.transactionreader.currency;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
public class ExchangeRate {
    private String currency;
    private BigDecimal rate;
    private Date startDate;
    private Date endDate;
}
