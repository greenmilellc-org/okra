package okra.exception;

public class InvalidOkraConfigurationException extends RuntimeException {
    public InvalidOkraConfigurationException() {
        super("MongoScheduler.InvalidConfiguration");
    }
}
