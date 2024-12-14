package pl.manyroutes.errorhandling;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
