package pro.gravit.launchserver.auth.protect.hwid;

public class HWIDException extends Exception {
    @SuppressWarnings("unused")
    public HWIDException() {
    }

    public HWIDException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public HWIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public HWIDException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public HWIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
