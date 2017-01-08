package pro.grain.admin.web.errors;

public class PojoConversionException extends RuntimeException {
    public PojoConversionException(String message) {
        super(message);
    }

    public PojoConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PojoConversionException(Throwable cause) {
        super(cause);
    }
}
