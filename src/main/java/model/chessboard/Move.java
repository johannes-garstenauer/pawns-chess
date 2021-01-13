package model.chessboard;

public enum Move {

    /**
     * Move pawn left, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONALLEFT,

    /**
     * Move pawn straight ahead one tile.
     */
    FORWARD,

    /**
     * Move pawn straight ahead two tiles.
     */
    DOUBLEFORWARD,

    /**
     * Move pawn right, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONALRIGHT

}
