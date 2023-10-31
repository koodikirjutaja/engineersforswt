package ee.ut.math.tvt.salessystem;

public class FieldFormatException extends SalesSystemException{
    public FieldFormatException() {
        super();
    }

    public FieldFormatException(String message) {
        super(message);
    }

    public FieldFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
