package model.lookAheadTree;

import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.chessboard.Direction;
import model.chessboard.Pawn;
import model.player.Player;
import model.Tuple;

import java.util.*;

public class Node 
{
    public ChessBoard board;
    public Node parent;
    public List<Node> children = new ArrayList<>();
    int depth;

    //TODO
    double value = Integer.MIN_VALUE;

    public Node(ChessBoard board, Node parent, int depth) {
        this.board = board;
        this.parent = parent;
        this.depth = depth;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public double getValue() {
        return value;
    }


    //TODO case: spieler muss aussetzen
    public void createSubTree(int level) {
        if (level > 0) {
            createChildren(getNextPlayer());

            // In case the player has to suspend a move let the other
            // player move.
            if (children.isEmpty()) {

                if (!board.isGameOver()) {
                    if (getNextPlayer() == Player.HUMAN) {
                        board.setNextPlayer(Player.MACHINE);
                        createChildren(Player.MACHINE);
                    } else {
                        board.setNextPlayer(Player.HUMAN);
                        createChildren(Player.HUMAN);
                    }
                } else {

                    // Break recursion if the game is over.
                    return;
                }
            }

            for (Node child : children) {
                child.createSubTree(level - 1);
            }
        }
    }

    public void setValue() {

        if (children.isEmpty()) {

            // Assign value to leaf.
            value = createBoardRating();
        } else {

            // Assign values to children.
            for (Node child : children) {
                if (child.getValue() == Integer.MIN_VALUE) {
                    child.setValue();
                }
            }

            /*
            // Assign value to root
            -> eigentlich braucht root keinen Value
            if (depth == 0) {
               value = getBestMove().getValue();
            } else {
             */

            // Assign value to inner node.
            //TODO worst und best vertauscht??
            if (getNextPlayer() == Player.HUMAN) {
                value = createBoardRating() + getWorstMove().getValue();
            } else {
                value = createBoardRating() + getBestMove().getValue();
            }
        }

        /*
        if (children.isEmpty()) {

            // Assign value to leaf node
            value = createBoardRating();
        } else {

            // Assign values recursively to child nodes
            for (Node child : children) {
                if (child.getValue() == Double.MIN_VALUE) {
                    child.setValue();
                }
            }

            // Assign value to inner node
            if (getNextPlayer() == Player.HUMAN) {
                value = createBoardRating() + getBestMove().getValue();
            } else {
                value = createBoardRating() + getWorstMove().getValue();
            }
        }

         */
    }

    /**
     * Returns the node from children which represents the move with the
     * highest board rating.
     *
     * @return
     */
    public Node getBestMove() {

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MIN_VALUE;
        for (Node child : children) {
            //TODO put this calculation in if clause
            int doubleComp = Double.compare(child.getValue(), tempValue);
            if (doubleComp > 0) {
                temp = child;
                tempValue = child.getValue();
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }

    //TODO: combine with getBestMove
    public Node getWorstMove() {

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MAX_VALUE;
        for (Node child : this.children) {
            int doubleComp = Double.compare(child.getValue(), tempValue);
            if (doubleComp < 0) {
                temp = child;
                tempValue = child.getValue();
            }
        }

        if (temp == null) {
            //TODO: right exc??
            throw new IllegalCallerException("This tree has no children!");
        } else {
            return temp;
        }
    }

    public Player getNextPlayer() {
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
            for (Direction direction : Direction.values()) {

                //Can move at most one column.
                for (int colTo = pawn.getColumn() - 1; colTo
                        <= pawn.getColumn() + 1; colTo++) {

                    //Can move at most two rows.
                    for (int rowTo = pawn.getRow() - 2; rowTo <= pawn.getRow() + 2;
                         rowTo++) {
                        Tuple temp = board.isLegalMove(direction, pawn, colTo,
                                rowTo);

                        // If the move is legal and an actual movement takes
                        // place.
                        if (temp.getLegalityOfMove()
                                && (colTo != pawn.getColumn()
                                || rowTo != pawn.getRow())) {

                            ChessBoard boardClone = (ChessBoard) board.clone();
                            boardClone.makeMove(boardClone.getPawn(pawn.getColumn(), pawn.getRow(), player.getColor()),
                                    temp, colTo,
                                    rowTo);
                            children.add(new Node(boardClone,
                                    this, this.depth + 1));
                        }
                    }
                }
            }
        }
    }
}

