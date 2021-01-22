package model.chessboard;

public enum Color {

    /**
     * Specifies the color of a pawn or a player as white. This means the white
     * player has control over the corresponding white pawns.
     */
    WHITE,

    /**
     * Specifies the color of a pawn or a player as white. This means the white
     * player has control over the corresponding white pawns.
     */
    BLACK;

    //TODO funzt das at code linken im java doc?

    /**
     * Determines the color opposite to the color which was given.
     *
     * @param color The given color. Has to be either {@code Color.WHITE} or
     *              {@code Color.BLACK} or {@code Color.NONE}.
     * @return The opposite color of the given color, if possible. If the
     * color is {@code Color.NONE} the same color will be returned.
     */
    public static Color getOppositeColor(Color color) {
        if (color == Color.WHITE) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    public String toString() {
        if (this == WHITE) {
            return "W";
        } else if (this == BLACK) {
            return "B";
        } else return " ";
    }
}
