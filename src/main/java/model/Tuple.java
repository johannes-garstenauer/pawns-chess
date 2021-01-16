package model;

import model.chessboard.Pawn;

//TODO: Rename
//contains
public class Tuple {
    private boolean legalityOfMove;

    // Null if there is no pawn to be attacked
    private Pawn attackedPawn;


    public Tuple(boolean legalityOfMove, Pawn attackedPawn) {
        this.legalityOfMove = legalityOfMove;
        this.attackedPawn = attackedPawn;
    }

    public boolean getLegalityOfMove() {
        return legalityOfMove;
    }

    public Pawn getAttackedPawn() {
        return attackedPawn;
    }
}
