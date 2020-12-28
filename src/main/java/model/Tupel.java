package model;

//TODO: Rename
//contains
public class Tupel {
    private boolean legalityOfMove;

    // Null if there is no pawn to be attacked
    private Pawn attackedPawn;


    public Tupel(boolean legalityOfMove, Pawn attackedPawn) {
        this.legalityOfMove = legalityOfMove;
        this.attackedPawn = attackedPawn;
    }

    public boolean isLegalMove() {
        return legalityOfMove;
    }

    public Pawn getAttackedPawn() {
        return attackedPawn;
    }
}