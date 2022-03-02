package elefteria.cc.finanteqtask.exception;

public class WrongRecordException extends RuntimeException {
    public WrongRecordException() {
        super();
    }

    public WrongRecordException(String message) {
        super(message);
    }

    public WrongRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongRecordException(Throwable cause) {
        super(cause);
    }
}
