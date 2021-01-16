package model.lookAheadTree;

import model.chessboard.ChessBoard;
import model.player.Player;

public class LookAheadTree //implements Cloneable
{
    private final Node root;

    public LookAheadTree (ChessBoard board) {
        this.root = new Node(board, null, 0);
    }

    public ChessBoard getBestMove() {
        root.createSubTree(Player.MACHINE.getLevel());
        for (Node child : root.children) {
            child.setValue();
        }
        return root.getBestMove().getBoard();
    }
}
