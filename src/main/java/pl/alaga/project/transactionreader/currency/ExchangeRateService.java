package pl.alaga.project.transactionreader.currency;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.alaga.project.transactionreader.parser.CsvFileParser;
import pl.alaga.project.transactionreader.parser.FileBadFormatException;
import pl.alaga.project.transactionreader.parser.FileParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService implements Consumer<MultipartFile> {

    private static final Logger logger = LogManager.getLogger(ExchangeRateService.class);

    private final JdbcExchangeRateDao exchangeRateDao;
    private final FileParser fileParser;

    public ExchangeRateService(JdbcExchangeRateDao exchangeRateDao, FileParser fileParser) {
        this.exchangeRateDao = exchangeRateDao;
        this.fileParser = fileParser;
    }

    @Override
    public void accept(MultipartFile file) {
        logger.debug("Import rates");

        List<List<String>> recordList = fileParser.readRecords(file);
        List<ExchangeRate> rateList = readRates(recordList, Currency.getInstance("EUR"));

        logger.debug("Rates imported:" + rateList);

        exchangeRateDao.addRates(rateList);
    }

    public Optional<BigDecimal> convertPlnToCurrency(BigDecimal amountPln, Currency currency, Date checkDate) {
        BigDecimal rate = exchangeRateDao.getRate(currency.getCurrencyCode(), checkDate).map(ExchangeRate::getRate).orElse(BigDecimal.ZERO);
        return Optional.ofNullable(rate.doubleValue() > 0 ? amountPln.multiply(rate).setScale(2, RoundingMode.HALF_UP) : null);
    }

    private List<ExchangeRate> readRates(List<List<String>> csvFile, Currency currency) {
        if (CollectionUtils.isEmpty(csvFile)) {
            throw new FileBadFormatException("Pusty plik kursów");
        }
        List<ExchangeRate> rateList = csvFile.stream().
                filter(e -> e != null && e.size() == 3).
                map((e) -> {
                    try {
                        return new ExchangeRate(
                                currency.getCurrencyCode(),
                                new BigDecimal(e.get(2).replaceAll("(\\D*)([\\d\\.]*)(.*)", "$2")),
                                new Date(CsvFileParser.dateFormat.parse(e.get(0)).getTime()),
                                new Date(CsvFileParser.dateFormat.parse(e.get(1)).getTime()));
                    } catch (ParseException ex) {
                        throw new FileBadFormatException(ex.getMessage());
                    }
                })
                 .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(rateList)) {
            throw new FileBadFormatException("Niewłaściwa liczba kolumn");
        }

        return rateList;
    }
}
