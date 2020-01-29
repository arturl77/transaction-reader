package pl.alaga.project.transactionreader.parser;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvFileParser implements FileParser {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final String amountRegexp = "(\\D*)([\\d\\.]*)(.*)";

    @Override
    public List<List<String>> readRecords(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileBadFormatException("Pusty plik!");
        } else {
            List<List<String>> recordList = new ArrayList<>();

            try (InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream())) {

                CSVReader csvReader = new CSVReader(inputStreamReader);
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    recordList.add(Arrays.asList(line));
                }

            } catch (IOException e) {
                throw new FileBadFormatException(e.getMessage(), e);
            }
            return recordList;
        }
    }

}
