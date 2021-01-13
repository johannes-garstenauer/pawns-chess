package model.lookAheadTree;

import model.chessboard.BoardImpl;
import model.player.Player;

public class LookAheadTree //implements Cloneable
{
    private final Node root;

    public LookAheadTree (BoardImpl board) {
        this.root = new Node(board, null, 0);
    }

    public BoardImpl getBestMove() {
        root.createSubTree(Player.MACHINE.getLevel());
        for (Node child : root.children) {
            child.setValue();
        }
        return root.getBestMove().getBoard();
    }
}
