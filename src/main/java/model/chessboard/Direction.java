package model.chessboard;

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
