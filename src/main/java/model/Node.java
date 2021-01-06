package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node //implements Cloneable
{
    public BoardImpl board;
    public Node parent;
    public List<Node> children = new ArrayList<>();

    //TODO
    double value = Double.MIN_VALUE;

    public Node(BoardImpl board, Node parent) {
        this.board = board;
        this.parent = parent;
    }

    public double getValue() {
        return value;
    }

    //TODO case: spieler muss aussetzen
    public void createSubTree(int level) {
        if (level > 0) {

            /*
            if(!board.hasToSuspendMove(getPlayer())) {
                this.createChildren(getPlayer());
            } else {

                // In case the player has to suspend a move let the other
                // player move
                if(getPlayer() == Player.HUMAN) {
                    this.createChildren();
                }
            }

             */

            createChildren(getPlayer());

            // In case the player has to suspend a move let the other
            // player move.
            if (children.isEmpty()) {
                if(getPlayer() == Player.HUMAN) {
                    board.setNextPlayer(Player.MACHINE);
                    createChildren(Player.MACHINE);
                } else {
                    board.setNextPlayer(Player.HUMAN);
                    createChildren(Player.HUMAN);
                }
            }

            for (Node child : children) {
                child.createSubTree(level - 1);
            }
        }

    }

    public void setValue() {

        if (children.isEmpty()) {

            // Assign value to leaf node
            value = board.createBoardRating();
        } else {

            // Assign values recursively to child nodes
            for (Node child : children) {
                if (child.getValue() == Double.MIN_VALUE) {
                    child.setValue();
                }
            }

            // Assign value to inner node
            if (getPlayer() == Player.HUMAN) {
                value = board.createBoardRating() + getBestMove().getValue();
            } else {
                value = board.createBoardRating() + getWorstMove().getValue();
            }
        }
    }

    /**
     * Returns the node from children which represents the move with the
     * highest board rating.
     *
     * @return
     */
    public Node getBestMove(){

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Double.MIN_VALUE;
        for (Node child: this.children) {
            if (child.getValue() > tempValue) {
                temp = child;
                tempValue = child.getValue();
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else  {
            return temp;
        }
    }

    //TODO: combine with getBestMove
    public Node getWorstMove(){

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Double.MIN_VALUE;
        for (Node child: this.children) {
            if (child.getValue() < tempValue) {
                temp = child;
                tempValue = child.getValue();
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else  {
            return temp;
        }
    }

    public Player getPlayer() {
        return board.getNextPlayer();
    }

    public void createChildren(Player player) {
        //TODO hier use if (!hasToSuspendMove()) ?

        //List of pawns out of whose moves the children nodes will be
        // constructed.
        //TODO: einfach getPlayerPawns methode bauen
        List<Pawn> pawns;
        if (player.getColor() == Color.WHITE) {
            pawns = board.getWhitePawns();
        } else {
            pawns = board.getBlackPawns();
        }


        //TODO hier move und machineMove aufrufen

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
                            System.out.println("Col: " + pawn.getColumn() + " "
                                    + "ColTo: " + colTo);
                            System.out.println("Row: " + pawn.getRow() + " "
                                    + "rowTo: " + rowTo);

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

