package model.chessboard;

/**
 * An enumeration of directions. A direction is used in combination with the
 * starting and end positions of a pawns movement on the board. The direction
 * should always be in the direction in which the pawn is facing and never
 * backwards.
 */
public enum Direction {

    /**
     * Move pawn left, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONAL_LEFT,

    /**
     * Move pawn straight ahead one tile.
     */
    FORWARD,

    /**
     * Move pawn straight ahead two tiles.
     */
    DOUBLE_FORWARD,

    /**
     * Move pawn right, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONAL_RIGHT,

    /**
     * An illegal direction.
     */
    ILLEGAL_DIRECTION

}
