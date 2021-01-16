package model.exceptions;

public class IllegalMoveException extends RuntimeException{

    public IllegalMoveException (String message) {
        super(message);
    }

    public IllegalMoveException () {
        super();
    }
}
