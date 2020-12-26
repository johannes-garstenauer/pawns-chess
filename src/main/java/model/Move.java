package model;

public enum Move {

    /**
     * Move pawn straight ahead one tile.
     */
    FORWARD,

    /**
     * Move pawn straight ahead two tiles.
     */
    DOUBLEFORWARD,

    /**
     * Move pawn left, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONALLEFT,

    /**
     * Move pawn right, then forward from the pawns viewpoint toward his
     * finish row.
     */
    DIAGONALRIGT

}
