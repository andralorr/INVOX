package invox.exception;

public class InsufficientStockException extends InvoxException {

    public InsufficientStockException(String productName, int requested, int available) {
        super("Stoc insuficient pentru '" + productName + "': cerut "
                + requested + ", disponibil " + available + ".");
    }
}
