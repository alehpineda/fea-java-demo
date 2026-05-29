package util;

// Runtime exception for finite element processing errors.
public class FeaException extends RuntimeException {

    public FeaException(String message) {
        super(message);
    }
}
