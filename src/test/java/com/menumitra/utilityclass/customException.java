package com.menumitra.utilityclass;

public class customException extends Exception
{
    private static final long serialVersionUID = 1L;

    public customException(String message) {
        super(message);
    }

    public customException(String message, Throwable cause) {
        super(message, cause);
    }

    public customException(Throwable cause) {
        super(cause);
    }

}
