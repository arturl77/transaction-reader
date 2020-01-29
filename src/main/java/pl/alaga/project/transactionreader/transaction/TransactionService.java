package pl.alaga.project.transactionreader.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.alaga.project.transactionreader.currency.ExchangeRateService;
import pl.alaga.project.transactionreader.parser.CsvFileParser;
import pl.alaga.project.transactionreader.parser.FileBadFormatException;
import pl.alaga.project.transactionreader.parser.FileParser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

@Service
public class TransactionService implements Consumer<MultipartFile> {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    private final ExchangeRateService exchangeRateService;
    private final TransactionDao transactionDao;
    private final FileParser fileParser;

    public TransactionService(ExchangeRateService exchangeRateService, TransactionDao transactionDao, FileParser fileParser) {
        this.exchangeRateService = exchangeRateService;
        this.transactionDao = transactionDao;
        this.fileParser = fileParser;
    }

    public List<PaymentTransaction> getTransactionList() {
        return transactionDao.getAllTransactions();
    }

    @Override
    public void accept (MultipartFile file) {
        logger.debug("Import transactions");

        List<List<String>> recordList = fileParser.readRecords(file);

        List<PaymentTransaction> transactionList = new ArrayList<>(recordList.size());
        for (List<String> record : recordList) {
            PaymentTransaction transaction = readTransaction(record);
            transaction.setAmountEur(exchangeRateService.convertPlnToCurrency(
                    transaction.getAmountPln(),
                    Currency.getInstance("EUR"),
                    transaction.getTransactionDate()).
                    orElse(null));
            transactionList.add(transaction);
        }

        logger.debug("Transactions imported:" + transactionList);

        transactionDao.addTransactions(transactionList);
    }

    private PaymentTransaction readTransaction(List<String> csvLine) {
        if (csvLine == null || csvLine.size() != 4) {
            throw new FileBadFormatException("Niewłaściwa liczba kolumn");
        }

        PaymentTransaction transaction = null;
        try {
            transaction = new PaymentTransaction(
                    Integer.parseInt(csvLine.get(0)),
                    new java.sql.Date(CsvFileParser.dateFormat.parse(csvLine.get(1)).getTime()),
                    StringUtils.trim(csvLine.get(2)),
                    new BigDecimal(csvLine.get(3).replaceAll("(\\D*)([\\d\\.]*)(.*)", "$2")), null);
        } catch (ParseException e) {
            throw new FileBadFormatException(e.getMessage());
        }

        return transaction;
    }

}
