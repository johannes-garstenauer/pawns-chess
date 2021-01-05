package model;

import java.util.ArrayList;
import java.util.List;

public class Node //implements Cloneable
         {
    public BoardImpl board;
    public Node parent;
    public List<Node> children = new ArrayList<>();

    //TODO
    double value; //= board.createBoardRating();

    public Node(BoardImpl board, Node parent) {
        this.board = board;
        this.parent = parent;
    }

    public void constructTree(int level) {
        if (level > 0) {
            this.createChildren(getPlayer());

            for (Node child : children) {
                child.constructTree(level - 1);
            }
        }
    }

    public Player getPlayer() {
        return board.getNextPlayer();
    }

    /*
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
     */

    public void createChildren(Player player) {

        //List of pawns out of whose moves the children nodes will be
        // constructed.
        //TODO: einfach getPlayerPawns methode bauen
        List<Pawn> pawns;
        if (player.getColor() == Color.WHITE) {
            pawns = board.getWhitePawns();
        } else {
            pawns = board.getBlackPawns();
        }


        // Create child nodes for each possible move by each pawn.
        for (Pawn pawn : pawns) {
            for (Move move : Move.values()) {
                //Can move at most one column.
                for (int colTo = pawn.getColumn() - 1; colTo
                        <= pawn.getColumn() + 1; colTo++) {
                    //Can move at most two rows.
                    for (int rowTo = pawn.getRow() - 2; rowTo <= pawn.getRow() + 2;
                         rowTo++) {
                        Tupel temp = board.isLegalMove(move, pawn, colTo,
                                rowTo);
                        // If the move is legal and an actual movement takes
                        // place.
                        if (temp.getLegalityOfMove()
                                && (colTo != pawn.getColumn()
                                || rowTo != pawn.getRow())) {

                            System.out.println(move);
                            System.out.println("Col: " +pawn.getColumn()+" "
                                    + "ColTo: "+colTo);
                            System.out.println("Row: " +pawn.getRow() + " "
                                    + "rowTo: "+rowTo);

                            BoardImpl boardClone = (BoardImpl) board.clone();
                            boardClone.makeMove(boardClone.getPawn(pawn.getColumn(), pawn.getRow(), player.getColor()),
                                    temp, colTo,
                                    rowTo);
                            children.add(new Node(boardClone,
                                    this));
                        }
                    }
                }
            }
        }
    }
}

