package model.chessboard;

/**
 * A public class that symbolizes a pawn on the chessboard. A pawns
 * association with a player is not implemented within this class but within
 * {@code Board} or its implementations.
 */
public class Pawn implements Cloneable {

    /**
     * The column in which the pawn is situated on the board.
     */
    private int column;

    /**
     * The row in which the pawn is situated on the board.
     */
    private int row;

    //TODO entfernen, stattdessen prüfen ob row == startingrow
    /**
     * An indication as to whether this pawn has already been moved. Useful
     * in determining whether a double move can be performed.
     */
    public boolean hasMoved = false;

    /**
     * This is a constructor for the pawn.
     *
     * @param column The column in which the pawn will be placed.
     * @param row    The row in which the pawn will be placed.
     * @throws IllegalArgumentException Might be thrown if given position is
     *                                  illegal e.g. not within the board's
     *                                  confines.
     */
    public Pawn(int column, int row) throws IllegalArgumentException {
        if (column >= 0 && column <= Board.SIZE && row >= 0
                && row <= Board.SIZE) {
            this.column = column;
            this.row = row;
        } else {
            //TODO also catch if there already is a pawn at that position??
            //-> could be complicated
            throw new IllegalArgumentException("The pawns position must be "
                    + "within the board.");
        }
    }

    /**
     * Deep clones an instance of {@code Pawn}.
     *
     * @return Returns a deep clone of a pawn.
     */
    @Override
    public Pawn clone() {
        Pawn copy;

        try {
            copy = (Pawn) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        return copy;
    }

    /**
     * Determines whether on pawn object is equal to another.
     *
     * @param o The pawn that is to be compared to.
     * @return Return {@code true} if the pawns are equal. Otherwise return
     * {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pawn)) {
            return false;
        } else {
            final Pawn other = (Pawn) o;

            //TODO vergleiche mit gettern ode rmit den variablen???
            if (this.getColumn() != other.getColumn()) {
                return false;
            } else if (this.getRow() != other.getRow()) {
                return false;
            } else {
                return this.isOpeningMove() == other.isOpeningMove();
            }
        }
    }

    /**
     * This indicates whether a pawn has already been moved.
     *
     * @return Return {@code true} if the pawns has moved. Otherwise return
     * {@code false}.
     */
    public boolean isOpeningMove() {
        return !hasMoved;
    }

    /**
     * A setter in order to change the {@code hasMoved} property of the pawn.
     */
    public void setHasMoved() {
        hasMoved = true;
    }

    /**
     * A getter for the column in which the pawn is situated on the board.
     *
     * @return The column in which the pawn is situated.
     */
    public int getColumn() {
        return column;
    }

    /**
     * A getter for the row in which the pawn is situated on the board.
     *
     * @return The row in which the pawn is situated.
     */
    public int getRow() {
        return row;
    }

    /**
     * A setter in order to change the {@code column} of the pawn on the board.
     *
     * @param column The column to which the pawn is set on the board.
     * @throws IllegalArgumentException Might be thrown if given position is
     *                                  illegal e.g. not within the board's
     *                                  confines.
     */
    public void setColumn(int column) throws IllegalArgumentException {
        if (column < 1 || column > Board.SIZE) {
            throw new IllegalArgumentException("The column must be on the "
                    + "board.");
        }
        this.column = column;
    }

    /**
     * A setter in order to change the {@code row} of the pawn on the board.
     *
     * @param row The row to which the pawn is set on the board.
     * @throws IllegalArgumentException Might be thrown if given position is
     *                                  illegal e.g. not within the board's
     *                                  confines.
     */
    public void setRow(int row) throws IllegalArgumentException {
        if (row < 1 || row > Board.SIZE) {
            throw new IllegalArgumentException("The row must be on the "
                    + "board.");
        }
        this.row = row;
    }
}
