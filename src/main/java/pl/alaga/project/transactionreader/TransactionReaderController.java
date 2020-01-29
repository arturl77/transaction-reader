package pl.alaga.project.transactionreader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.alaga.project.transactionreader.parser.FileBadFormatException;
import pl.alaga.project.transactionreader.transaction.TransactionService;

import java.util.function.Consumer;

@Controller
public class TransactionReaderController {

    private static final Logger logger = LogManager.getLogger(TransactionReaderController.class);

    private TransactionService transactionService;
    private Consumer<MultipartFile> exchangeRateImporter;

    public TransactionReaderController(TransactionService transactionService, @Qualifier("exchangeRateService") Consumer<MultipartFile> exchangeRateImporter) {
        this.transactionService = transactionService;
        this.exchangeRateImporter = exchangeRateImporter;
    }

    @GetMapping("/")
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getTransactionList());
        return "main";
    }

    @PostMapping("/uploadTransactions")
    public String uploadTransactions(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        transactionService.accept(file);
        redirectAttributes.addFlashAttribute("message",
                "Plik z transakcjami wczytany [" + file.getOriginalFilename() + "]");

        return "redirect:/";
    }

    @PostMapping("/uploadRates")
    public String uploadRates(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        exchangeRateImporter.accept(file);
        redirectAttributes.addFlashAttribute("message",
                "Plik z kursami wczytany [" + file.getOriginalFilename() + "]");

        return "redirect:/";
    }

	@ExceptionHandler(FileBadFormatException.class)
	public String handleException(FileBadFormatException exception, RedirectAttributes redirectAttributes) {
        return printError("Błędny format pliku: " + exception.getMessage(), redirectAttributes);
	}

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, RedirectAttributes redirectAttributes) {
       return printError(exception.toString(), redirectAttributes);
    }

    private String printError(String error, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", error);
        return "redirect:/";
    }
}
