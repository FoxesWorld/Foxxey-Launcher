package pro.gravit.utils.command;

public class UnknownCommandException extends CommandException {

    private static final long serialVersionUID = -6582814593912117772L;
    public static final String MESSAGE = "Unknown command: '%s'";

    public UnknownCommandException(String command) {
        super(String.format(MESSAGE, command));
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
