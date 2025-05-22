package com.menumitra.utilityclass;

/**
 * Custom runtime exception class for Menumitra application.
 * This class replaces the checked customException with an unchecked exception.
 */
public class MenumitraException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MenumitraException(String message) {
        super(message);
    }

    public MenumitraException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenumitraException(Throwable cause) {
        super(cause);
    }
} 