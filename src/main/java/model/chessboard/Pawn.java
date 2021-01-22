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
     * Return {@code true} if the pawns has moved. Otherwise return
     * {@code false}.
     */
    public boolean isOpeningMove() {
        return !hasMoved;
    }

    public void hasMoved() {
        hasMoved = true;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
