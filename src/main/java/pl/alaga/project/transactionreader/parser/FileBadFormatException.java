package pl.alaga.project.transactionreader.parser;

public class FileBadFormatException extends RuntimeException {

	public FileBadFormatException(String message) {
		super(message);
	}

	public FileBadFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
