package model.chessboard;

/**
 * An enumeration of colors. In this project they are assigned to players and
 * in extension to slots on the board and pawns.
 */
public enum Color {

    /**
     * Specifies the color of a pawn, slot or player as white. This means the
     * white player has control over the corresponding white pawns.
     */
    WHITE,

    /**
     * Specifies the color of a pawn, slot or a player as white. This means the
     * white player has control over the corresponding white pawns.
     */
    BLACK,

    /**
     * Specifies the color of a pawn, slot or player as none. This is the case
     * if a pawn is not associated with player.
     */
    NONE;

    /**
     * Determines the color opposite to the color which was given.
     *
     * @param color The given color. Has to be either {@code WHITE} or
     *              {@code BLACK} or {@code CNONE}.
     * @return The opposite color of the given color. If the
     * opposite cannot be determined {@code NONE} will be returned.
     */
    public static Color getOppositeColor(Color color) {
        if (color == WHITE) {
            return BLACK;
        } else if (color == BLACK) {
            return WHITE;
        } else {
            return NONE;
        }
    }

    /**
     * Returns a single upper case letter as a string representation of the
     * color.
     * @return Returns single upper case representation of color.
     */
    public String toString() {
        if (this == WHITE) {
            return "W";
        } else if (this == BLACK) {
            return "B";
        } else {
            return " ";
        }
    }
}
