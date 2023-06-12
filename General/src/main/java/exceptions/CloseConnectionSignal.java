package exceptions;

public class CloseConnectionSignal extends RuntimeException {
    public CloseConnectionSignal(String message) {
        super(message);
    }
}
