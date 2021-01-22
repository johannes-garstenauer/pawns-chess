package model.exceptions;

/**
 * An exception to indicate that a move on the board cannot be performed. If
 * the given arguments for a move are illegal consider using
 * {@code IllegalArgumentsException} instead.
 */
public class IllegalMoveException extends RuntimeException{

    /**
     * A constructor for am IllegalMoveException.
     *
     * @param message The message that is contained upon throwing the exception.
     */
    public IllegalMoveException (String message) {
        super(message);
    }

    /**
     * An alternative constructor for this exception without a corresponding
     * error message.
     */
    public IllegalMoveException () {
        super();
    }
}
