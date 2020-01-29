package pl.alaga.project.transactionreader.parser;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParser {
    List<List<String>> readRecords(MultipartFile file);
}
