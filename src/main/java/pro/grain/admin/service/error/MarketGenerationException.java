package pro.grain.admin.service.error;

import java.util.List;

public class MarketGenerationException extends Exception {
    private List<String> errors;

    public MarketGenerationException(String message) {
        super(message);
    }

    public MarketGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketGenerationException(String message, List<String> errors, Throwable cause) {
        super(message, cause);
        this.errors = errors;
    }

    public MarketGenerationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public MarketGenerationException(Throwable cause) {
        super(cause);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
