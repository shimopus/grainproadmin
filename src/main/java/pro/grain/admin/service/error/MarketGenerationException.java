package pro.grain.admin.service.error;

public class MarketGenerationException extends Exception {
    public MarketGenerationException(String message) {
        super(message);
    }

    public MarketGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketGenerationException(Throwable cause) {
        super(cause);
    }
}
