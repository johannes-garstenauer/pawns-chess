package model;

import java.util.ArrayList;
import java.util.List;

public class Node //implements Cloneable
{
    public BoardImpl board;
    public Node parent;
    public List<Node> children = new ArrayList<>();
    int depth;

    //TODO
    double value = Double.MIN_VALUE;

    public Node(BoardImpl board, Node parent, int depth) {
        this.board = board;
        this.parent = parent;
        this.depth = depth;
    }

    public double getValue() {
        return value;
    }

    public double createBoardRating() {

        // List of humans pawns.
        List<Pawn> humanPawns;

        // List of machines pawns.
        List<Pawn> machinePawns;

        if (board.getHumanColor() == Color.WHITE) {
            humanPawns = board.whitePawns;
            machinePawns = board.blackPawns;
        } else {
            humanPawns = board.blackPawns;
            machinePawns = board.whitePawns;
        }

        double n = machinePawns.size() - 1.5 * humanPawns.size();

        int machinePawnsMovedFactor = 0;
        for (int i = 0; i < board.SIZE; i++) {
            if (board.getMachineColor() == Color.WHITE) {

                //If pawns move down to up
                machinePawnsMovedFactor =
                        machinePawnsMovedFactor + board.amountOfPawnsInRow(i + 1,
                                machinePawns) * i;
            } else {

                //If pawns move up to down
                machinePawnsMovedFactor =
                        machinePawnsMovedFactor +
                                board.amountOfPawnsInRow((board.SIZE - 1) - i + 1,
                                        machinePawns) * i;
            }
        }

        int humanPawnsMovedFactor = 0;
        for (int i = 0; i < board.SIZE; i++) {
            if (board.getHumanColor() == Color.WHITE) {

                //If pawns move down to up
                humanPawnsMovedFactor =
                        humanPawnsMovedFactor + board.amountOfPawnsInRow(i + 1,
                                humanPawns) * i;
            } else {

                //If pawns move up to down
                humanPawnsMovedFactor =
                        humanPawnsMovedFactor +
                                board.amountOfPawnsInRow((board.SIZE - 1) - i + 1,
                                        humanPawns) * i;
            }
        }

        double d = machinePawnsMovedFactor - 1.5 * humanPawnsMovedFactor;

        List<Pawn> threatenedMachinePawns = new ArrayList<>();

        // Create list of all machine pawns that are threatened by human pawns.
        for (Pawn humanPawn : humanPawns) {
            threatenedMachinePawns.addAll(board.determineThreatenedPawns(humanPawn,
                    board.getMachineColor()));
        }

        // Remove all those machine pawns that are protected by friendly pawns.
        threatenedMachinePawns.removeIf(threatenedMachinePawn
                -> board.isPawnProtected(threatenedMachinePawn, board.getMachineColor()));

        int amountOfThreatenedMachinePawns = threatenedMachinePawns.size();


        List<Pawn> threatenedHumanPawns = new ArrayList<>();

        // Create list of all human pawns that are threatened by machine pawns.
        for (Pawn machinePawn : machinePawns) {
            threatenedHumanPawns.addAll(board.determineThreatenedPawns(machinePawn,
                    board.getHumanColor()));
        }

        // Remove all those human pawns that are protected by friendly pawns.
        threatenedHumanPawns.removeIf(threatenedHumanPawn
                -> board.isPawnProtected(threatenedHumanPawn, board.getHumanColor()));

        int amountOfThreatenedHumanPawns = threatenedHumanPawns.size();

        System.err.println("human"+amountOfThreatenedHumanPawns);
        System.err.println("machine"+amountOfThreatenedMachinePawns);
        double c = amountOfThreatenedHumanPawns
                - 1.5 * amountOfThreatenedMachinePawns;

        int amountOfIsolatedMachinePawns = 0;
        for (Pawn machinePawn : machinePawns) {
            if (board.isPawnIsolated(machinePawn, board.getMachineColor())) {
                amountOfIsolatedMachinePawns++;
            }
        }

        int amountOfIsolatedHumanPawns = 0;
        for (Pawn humanPawn : humanPawns) {
            if (board.isPawnIsolated(humanPawn, board.getHumanColor())) {
                amountOfIsolatedHumanPawns++;
            }
        }

        double i =
                amountOfIsolatedHumanPawns - 1.5 * amountOfIsolatedMachinePawns;

        double v;
        try {

            // Player which might have won by playing the last move.
            //Player currentPlayer = parent.getNextPlayer();

            if (board.getWinner() == Player.HUMAN) {
                v = -1.5 * 5000/depth;
            } else {
                //TODO: what
                v = 5000 / depth;
            }

        } catch (IllegalCallerException e) {
            v = 0;
        }

        System.out.println(n);
        System.out.println(d);
        System.out.println(c);
        System.out.println(i);
        System.out.println(v);
        return n + d + c + i + v;
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
                }
            } else {

                // Break recursion if the game is over.
                return;
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

            for (Node child : children) {
                if (child.getValue() == Double.MIN_VALUE) {
                    child.setValue();
                }
            }

            //Assign value to root
            if (depth == 0) {
               value = getBestMove().getValue();
            } else {

                // Assign value to inner node.
                //TODO worst und best vertauscht??
                if (getNextPlayer() == Player.HUMAN) {
                    value = createBoardRating() + getWorstMove().getValue();
                } else {
                    value = createBoardRating() + getBestMove().getValue();
                }
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
    public Node getBestMove(){

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MIN_VALUE;
        for (Node child: children) {
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
        } else  {
            return temp;
        }
    }

    //TODO: combine with getBestMove
    public Node getWorstMove(){

        // Temporarily chosen Node representing the best Move
        Node temp = null;

        // Value of the chosen Node
        double tempValue = Integer.MAX_VALUE;
        for (Node child: this.children) {
            int doubleComp = Double.compare(child.getValue(), tempValue);
            if (doubleComp < 0) {
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

                            BoardImpl boardClone = (BoardImpl) board.clone();
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

