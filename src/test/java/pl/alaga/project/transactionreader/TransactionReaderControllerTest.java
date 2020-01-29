package pl.alaga.project.transactionreader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import pl.alaga.project.transactionreader.currency.ExchangeRateService;
import pl.alaga.project.transactionreader.transaction.TransactionService;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class TransactionReaderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    public void shouldUploadTransactionFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("transactions-ok.txt", getClass());
        MockMultipartFile multipartFile = new MockMultipartFile("file", resource.getFilename(),
                "text/plain", resource.getInputStream());

        this.mvc.perform(multipart("/uploadTransactions").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.transactionService).should().accept(multipartFile);
    }

    @Test
    public void shouldUploadRatesFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("rates-ok.txt", getClass());

        MockMultipartFile multipartFile = new MockMultipartFile("file", resource.getFilename(),
                "text/plain", resource.getInputStream());

        this.mvc.perform(multipart("/uploadRates").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.exchangeRateService).should().accept(multipartFile);
    }
}
