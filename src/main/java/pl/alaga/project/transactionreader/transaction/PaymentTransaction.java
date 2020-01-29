package pl.alaga.project.transactionreader.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@AllArgsConstructor
public class PaymentTransaction {
    private int transactionId;
    private Date transactionDate;
    private String title;
    private BigDecimal amountPln;
    private BigDecimal amountEur;

}
