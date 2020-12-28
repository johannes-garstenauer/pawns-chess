package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node implements Cloneable {
    public BoardImpl board;
    public Node parent;
    public List<Node> children = new ArrayList<>();

    //TODO
    int value;

    public Node(BoardImpl board, Node parent) {
        this.board = board;
        this.parent = parent;
    }

    public void constructTree(int level) {
        if (level > 0) {
            this.createChildren(this.getPlayer());

            for (Node child : children) {
                child.constructTree(level - 1);
            }
        }
    }

    public Player getPlayer() {
        return board.getNextPlayer();
    }

    @Override
    public Node clone() {
        Node copy;

        try {
            copy = (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        board = (BoardImpl) board.clone();
        parent = parent.clone();

        List<Node> childrenCopy = new ArrayList<>();

        for (Node child : children) {
            childrenCopy.add(child.clone());
        }
        copy.children = childrenCopy;

        return copy;
    }

    public void createChildren(Player player) {

        //List of pawns out of whose moves the children nodes will be
        // constructed.
        List<Pawn> pawns;
        if (player.getColor() == Color.WHITE) {
            pawns = board.getWhitePawns();
        } else {
            pawns = board.getBlackPawns();
        }


        //TODO: ecs caused by islegalmove -> does it change white or blackpawns?

        // Create child nodes for each possible move by each pawn.
        for (Pawn pawn : pawns) {
            for (Move move : Move.values()) {
                for (int colTo = pawn.getColumn() - 1; colTo
                        < pawn.getColumn() + 1; colTo++) {
                    for (int rowTo = pawn.getRow(); rowTo < pawn.getRow() + 1;
                         rowTo++) {
                        Tupel temp = board.isLegalMove(move, pawn, colTo,
                                rowTo);
                        if (temp.isLegalMove()) {
                            BoardImpl boardClone = (BoardImpl) board.clone();
                            boardClone.makeMove(pawn, temp, colTo, rowTo);
                            children.add(new Node(boardClone,
                                    this));
                        }
                    }
                }
            }
        }
    }
}

