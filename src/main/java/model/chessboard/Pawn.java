package model.chessboard;

public class Pawn implements Cloneable{

    private int column;
    private int row;
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

    @Override
    public Pawn clone(){
        Pawn copy;

        try {
            copy = (Pawn) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        return copy;
    }

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

    public boolean isOpeningMove() {
        return !hasMoved;
    }

    public void hasMoved(){
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
