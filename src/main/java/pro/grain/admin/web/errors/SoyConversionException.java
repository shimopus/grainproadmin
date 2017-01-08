package pro.grain.admin.web.errors;

public class SoyConversionException extends Exception {

    public SoyConversionException(String message) {
        super(message);
    }

    public SoyConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoyConversionException(Throwable cause) {
        super(cause);
    }

}
