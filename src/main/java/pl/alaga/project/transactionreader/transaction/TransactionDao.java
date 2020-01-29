package pl.alaga.project.transactionreader.transaction;

import java.util.List;

public interface TransactionDao {

    public List<PaymentTransaction> getAllTransactions();

    public void addTransactions(List<PaymentTransaction> transactions);

}
