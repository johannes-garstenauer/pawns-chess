package model.chessboard;

import model.exceptions.IllegalMoveException;
import model.lookAheadTree.LookAheadTree;
import model.player.Player;
import model.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChessBoard implements Board, Cloneable {

    public Player human = Player.HUMAN;
    public Player machine = Player.MACHINE;

    public Player nextPlayer;

    public List<Pawn> whitePawns = new ArrayList<>();
    public List<Pawn> blackPawns = new ArrayList<>();

    /**
     * This is the constructor of an implementation of a {@code Board} for
     * pawns chess.
     * The pawns will be initiated at the opposing sides of the board.
     *
     * @param level      The difficulty level of the machine player. This
     *                   determines how many moves the machine player can simulate
     *                   into the future in order to determine his best next move.
     * @param humanColor The color of your pawns. If you are white you will
     *                   start first. If you are black the machine player
     *                   will start the game. Regardless of your color, your
     *                   pawns will start at the bottom of the board.
     * @throws IllegalArgumentException Throws an exception if the level or
     *                                  the {@code humanColor} are not valid.
     */
    public ChessBoard(int level, Color humanColor) throws IllegalArgumentException {

        //TODO: Koordinaten von Move auch in der shell prüfen
        //TODO diese beschränkung in der shell und nicht hier machen
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Only levels one to four are "
                    + "supported.");
        } else if (humanColor == Color.NONE || humanColor == null) {
            throw new IllegalArgumentException("The human player has to have "
                    + "a valid color.");
        } else {
            machine.setLevel(level);
            human.setColor(humanColor);
            machine.setColor(Color.getOppositeColor(humanColor));
            nextPlayer = getOpeningPlayer();
        }
        createInitialPawnPositions();
    }

    @Override
    public Player getOpeningPlayer() {
        if (human.getColor() == Color.WHITE) {
            return human;
        } else {
            return machine;
        }
    }

    /**
     * Initiates the pawns in their starting positions.
     * The pawns of the human player will be placed on the lowest row of the
     * board. The machines pawns on the highest.
     *
     * @throws IllegalStateException The boards pawn lists have to be empty.
     *                               If there are already pawns on the board the
     *                               initial pawn positions should not be
     *                               created.
     */
    private void createInitialPawnPositions() throws IllegalStateException {
        if (!(whitePawns.isEmpty() && blackPawns.isEmpty())) {
            throw new IllegalStateException("The boards pawns have to be "
                    + "uninitialized.");
        }

        int j = SIZE;
        for (int i = 1; i < SIZE + 1; i++) {

            // Assign human players pawns to the bottom row.
            getPawnsList(getHumanColor()).add(new Pawn(i, 1));

            // Assign machine players pawns to the top row.
            getPawnsList(Color.getOppositeColor(getHumanColor()))
                    .add(new Pawn(j, SIZE));
            j--;
        }
    }

    /**
     * Getter for the pawn list associated with the given color.
     *
     * @param listColor The color of the pawns whose list is to be returned.
     * @return The pawns list for the given color.
     * @throws IllegalArgumentException Might be thrown if the given color
     *                                  does not have an associated list of
     *                                  pawns with that color.
     */
    private List<Pawn> getPawnsList(Color listColor)
            throws IllegalArgumentException {
        if (listColor == Color.WHITE) {
            return whitePawns;
        } else if (listColor == Color.BLACK) {
            return blackPawns;
        } else {
            throw new IllegalArgumentException("The given color does not have"
                    + " list of pawns");
        }
    }

    @Override
    public Color getHumanColor() {
        return human.getColor();
    }

    @Override
    public Player getNextPlayer() {
        return nextPlayer;
    }

    /**
     * If the opposing player, to the one which just moved, can make a move
     * he is assigned to be the next player.
     * Otherwise the player who just moved will remain to be assigned as the
     * next player.
     * This method should only be used after a move was executed.
     */
    private void setNextPlayer() {
        if (!hasToSuspendMove(Player.getOppositePlayer(nextPlayer))) {
            nextPlayer = Player.getOppositePlayer(nextPlayer);
        }
    }

    /**
     * Determine the direction and distance of a move.
     *
     * @param colFrom The column starting position.
     * @param rowFrom The row starting position.
     * @param colTo   The column finish position.
     * @param rowTo   The row finish position.
     * @return The direction in which the move occurs.
     * @throws IllegalArgumentException May be thrown if the starting or
     *                                  finish position are not within the
     *                                  board, or if the move has an
     *                                  illegal distance or direction.
     */
    private Direction determineDirection(int colFrom, int rowFrom, int colTo,
                                         int rowTo)
            throws IllegalArgumentException {

        if (colFrom < 1 || colFrom > SIZE || rowFrom < 1 || rowFrom > SIZE
                || colTo < 1 || colTo > SIZE || rowTo < 1 || rowTo > SIZE) {
            throw new IllegalArgumentException("Moves must occur within the "
                    + "board!");
        } else {

            // Distance moved between columns.
            int colDist = colTo - colFrom;

            // Distance moved between rows.
            int rowDist = rowTo - rowFrom;

            //TODO hier keine exceptions?
            if (getPawn(colFrom, rowFrom) == null) {
                throw new IllegalArgumentException("Moves must be executed on "
                        + "existing Pawns.");
            } else if (getNextPlayer() == Player.HUMAN && !(rowDist > 0)) {
                throw new IllegalArgumentException("This move has an illegal "
                        + "direction.");
            } else if (getNextPlayer() == Player.MACHINE && !(rowDist < 0)) {
                throw new IllegalArgumentException("This move has an illegal "
                        + "direction.");
            } else {

                rowDist = Math.abs(rowDist);

                if (colDist == 0 && rowDist == 1) {
                    return Direction.FORWARD;
                } else if (colDist == 0 && rowDist == 2) {
                    return Direction.DOUBLE_FORWARD;
                } else if (colDist == -1 && rowDist == 1) {
                    return Direction.DIAGONAL_LEFT;
                } else if (colDist == 1 && rowDist == 1) {
                    return Direction.DIAGONAL_RIGHT;
                } else {
                    throw new IllegalArgumentException("This move has an "
                            + "illegal distance or direction.");
                }
            }
        }
    }

    /**
     * Determine whether this is a legal move.
     *
     * @param pawn  The pawn which is meant to make the move.
     * @param colTo The column destination of the move.
     * @param rowTo The row destination of the move.
     * @return The boolean value of the legality of the move. Returns false,
     * if the move is in the wrong direction, has a wrong distance, the pawn
     * is in an illegal position or if there is a pawn blocking the move. If
     * it is an attack move, false will be returned if, in addition to the
     * other conditions, there is no pawn to be attacked at the desired
     * destination of the move.
     */
    public boolean isLegalMove(Pawn pawn, int colTo,
                               int rowTo) {

        // Determine whether the move is in a legal direction and whether the
        // pawn is in a legal position or even existing in that position at all.
        Direction direction;
        try {
            direction = determineDirection(pawn.getColumn(),
                    pawn.getRow(), colTo, rowTo);
        } catch (IllegalArgumentException e) {
            return false;
        }

        // List of friendly pawns.
        List<Pawn> friendlyPawns = getPawnsList(getSlot(pawn.getColumn(),
                pawn.getRow()));

        // List of hostile pawns.
        List<Pawn> hostilePawns = getPawnsList(Color.getOppositeColor(
                getSlot(pawn.getColumn(), pawn.getRow())));


        if (direction == Direction.FORWARD
                || direction == Direction.DOUBLE_FORWARD) {

            // Determine whether a friendly or hostile pawn blocks this move.
            if (friendlyPawns.contains(getPawn(colTo, rowTo))
                    || hostilePawns.contains(getPawn(colTo, rowTo))) {

                // Return false if a pawn blocks this move.
                return false;
            } else if (direction == Direction.DOUBLE_FORWARD) {

                // Determine whether the pawn is moving up or down.
                if (nextPlayer == Player.HUMAN) {

                    // Determine whether there is a pawn blocking the double
                    // move.
                    return pawn.isOpeningMove() && getPawn(pawn.getColumn(),
                            pawn.getRow() + 1) == null;
                } else {

                    // Determine whether there is a pawn blocking the double
                    // move.
                    return pawn.isOpeningMove() && getPawn(pawn.getColumn(),
                            pawn.getRow() - 1) == null;
                }

            } else {

                // A legal move forward can occur.
                return true;
            }

        } else {

            // Determine whether a hostile pawn can be attacked.
            return hostilePawns.contains(getPawn(colTo, rowTo));
        }
    }

    @Override
    public Board move(int colFrom, int rowFrom, int colTo, int rowTo) {

        //TODO !!! Abfrage in Shell ob isGameOver

        if (nextPlayer != Player.HUMAN) {
            throw new IllegalMoveException("It is not the human player's "
                    + "turn.");
        } else if (isGameOver()) {
            throw new IllegalMoveException("No more moves can be made as this"
                    + " game is already finished.");
        } else {

            // Board on which the move is executed.
            ChessBoard newBoard = (ChessBoard) this.clone();

            // Pawn that will attempt to make a move.
            Pawn pawnToBeMoved;

            // Determine whether the move has a legal direction and
            // distance. Also check whether the move occurs within the
            // board.
            try {

                // Assign pawn that will attempt to make a move.
                pawnToBeMoved = newBoard.getPawn(colFrom, rowFrom);

                determineDirection(colFrom, rowFrom, colTo, rowTo);
            } catch (IllegalArgumentException ex) {
                throw new IllegalMoveException(ex.getMessage());
            }

            if (isLegalMove(pawnToBeMoved, colTo, rowTo) && newBoard
                    .getPawnsList(getHumanColor()).contains(pawnToBeMoved)) {

                newBoard.makeMove(pawnToBeMoved, colTo, rowTo);
                newBoard.setNextPlayer();
                return newBoard;
            } else {

                // Return null because either the move is not legal or
                return null;
            }
        }

    }

    /**
     * Executes a move on the board.
     *
     * @param pawn  This is the pawn which is to be moved.
     * @param colTo The column to which the pawn is to be moved.
     * @param rowTo The row to which the pawn is to be moved.
     */
    public void makeMove(Pawn pawn, int colTo, int rowTo) {

        //TODO: throw exception? maybe IllegalMoveException?
        if (isLegalMove(pawn, colTo, rowTo)) {

            // Determine whether a hostile pawn is at the finish position of
            // the move.
            if (Color.getOppositeColor(getSlot(colTo, rowTo))
                    == getSlot(pawn.getColumn(), pawn.getRow())) {

                // Remove the pawn which is to be attacked.
                getPawnsList(getSlot(colTo, rowTo)).remove(getPawn(colTo,
                        rowTo));
            }
            /*

            Hier ist keine verletzung des klassengeheimnis ( zumindest nicht
            hier direkt, vielleicht in  getPawnslist)
            Dafür ist es unnötig komoliziert und schwer verständlich
            if (getPawnsList(Color.getOppositeColor(getSlot(colTo, rowTo))).contains(pawn)) {

            }
                        try {
                List<Pawn> friendlyPawns =
                        getPawnsList(Color.getOppositeColor(getSlot(colTo,
                                rowTo)));
            } catch (IllegalArgumentException ex) {

            }

             */
            /*
            if (tuple.getAttackedPawn() != null) {
                if (getWhitePawns().contains(tuple.getAttackedPawn())) {
                    getWhitePawns().remove(tuple.getAttackedPawn());
                } else if (getBlackPawns().contains(tuple.getAttackedPawn())) {
                    getBlackPawns().remove(tuple.getAttackedPawn());
                } else {
                    throw new IllegalArgumentException("There was no pawn to be "
                            + "attacked found.");
                }
            }
             */
            pawn.setColumn(colTo);
            pawn.setRow(rowTo);
            pawn.hasMoved();
        }
    }

    @Override
    public Board machineMove() {

        if (nextPlayer != Player.MACHINE) {
            throw new IllegalMoveException("It is not the machine player's "
                    + "turn.");
        } else if (isGameOver()) {
            throw new IllegalMoveException("No more moves can be made as this"
                    + " game is already finished.");
        } else {

            //Board on which the move is executed.
            ChessBoard newBoard = (ChessBoard) this.clone();

            LookAheadTree lookAheadTree = new LookAheadTree(newBoard);

            newBoard.setNextPlayer();
            return lookAheadTree.getBestMove();
        }
    }

    public double createBoardRating(int depth) {

        // List of humans pawns.
        List<Pawn> humanPawns = getPawnsList(getHumanColor());

        // List of machines pawns.
        List<Pawn> machinePawns =
                getPawnsList(Color.getOppositeColor(getHumanColor()));

        double n = machinePawns.size() - 1.5 * humanPawns.size();

        int machinePawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {
            if (getMachineColor() == Color.WHITE) {

                //If pawns move down to up
                machinePawnsMovedFactor =
                        machinePawnsMovedFactor + amountOfPawnsInRow(i + 1,
                                machinePawns) * i;
            } else {

                //If pawns move up to down
                machinePawnsMovedFactor =
                        machinePawnsMovedFactor +
                                amountOfPawnsInRow((SIZE - 1) - i + 1,
                                        machinePawns) * i;
            }
        }

        int humanPawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {
            if (getHumanColor() == Color.WHITE) {

                //If pawns move down to up
                humanPawnsMovedFactor =
                        humanPawnsMovedFactor + amountOfPawnsInRow(i + 1,
                                humanPawns) * i;
            } else {

                //If pawns move up to down
                humanPawnsMovedFactor =
                        humanPawnsMovedFactor +
                                amountOfPawnsInRow((SIZE - 1) - i + 1,
                                        humanPawns) * i;
            }
        }

        double d = machinePawnsMovedFactor - 1.5 * humanPawnsMovedFactor;

        List<Pawn> threatenedHumanPawns = new ArrayList<>();

        // Create list of all human pawns that are threatened by machine pawns.
        for (Pawn machinePawn : machinePawns) {
            threatenedHumanPawns.addAll(determineThreatenedPawns(machinePawn,
                    getHumanColor()));
        }

        // Remove all those human pawns that are protected by friendly pawns.
        threatenedHumanPawns.removeIf(threatenedHumanPawn
                -> isPawnProtected(threatenedHumanPawn, getHumanColor()));

        // Remove duplicates.
        // Remove duplicates.
        threatenedHumanPawns =
                new ArrayList<>(new HashSet<>(threatenedHumanPawns));

        int amountOfThreatenedHumanPawns = threatenedHumanPawns.size();

        List<Pawn> threatenedMachinePawns = new ArrayList<>();

        // Create list of all machine pawns that are threatened by human pawns.
        for (Pawn humanPawn : humanPawns) {
            threatenedMachinePawns.addAll(determineThreatenedPawns(humanPawn,
                    getMachineColor()));
        }

        // Remove all those machine pawns that are protected by friendly pawns.
        threatenedMachinePawns.removeIf(threatenedMachinePawn
                -> isPawnProtected(threatenedMachinePawn, getMachineColor()));

        // Remove duplicates.
        threatenedMachinePawns =
                new ArrayList<>(new HashSet<>(threatenedMachinePawns));

        int amountOfThreatenedMachinePawns = threatenedMachinePawns.size();

        double c = amountOfThreatenedHumanPawns
                - 1.5 * amountOfThreatenedMachinePawns;

        int amountOfIsolatedMachinePawns = 0;
        for (Pawn machinePawn : machinePawns) {
            if (isPawnIsolated(machinePawn, getMachineColor())) {
                amountOfIsolatedMachinePawns++;
            }
        }

        int amountOfIsolatedHumanPawns = 0;
        for (Pawn humanPawn : humanPawns) {
            if (isPawnIsolated(humanPawn, getHumanColor())) {
                amountOfIsolatedHumanPawns++;
            }
        }

        double i =
                amountOfIsolatedHumanPawns - 1.5 * amountOfIsolatedMachinePawns;

        double v;
        try {

            if (depth != 0 && isGameOver()
            ) {

                // Player which might have won by playing the last move.
                if (getWinner() == Player.HUMAN) {

                    // If the human has won.
                    v = -1.5 * 5000 / depth;
                } else if (getWinner() == Player.MACHINE) {

                    // If the machine has won.
                    v = 5000 / depth;
                } else {

                    // If there is a draw.
                    v = 0;
                }
            } else {

                // Victory value for root.
                v = 0;
            }

        } catch (IllegalCallerException e) {
            v = 0;
        }
/*
        System.out.println();
        System.out.println(n);
        System.out.println(d);
        System.out.println(c);
        System.out.println(i);
        System.out.println(v);
        System.err.println("sum: " + (n + d + c + i + v));
 */
        return n + d + c + i + v;
    }

    /**
     *
     * @param pawn
     * @param pawnColor
     * @return
     */
    public boolean isPawnIsolated(Pawn pawn, Color pawnColor) {

        //Circles around the pawns position
        for (int col = -1; col <= 1; col++) {
            for (int row = -1; row <= 1; row++) {

                Pawn temp = this.getPawn(pawn.getColumn() + col,
                        pawn.getRow() + row, pawnColor);

                if (temp != null && !temp.equals(pawn)) {

                    // If there is another pawn the pawn is not isolated.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines which hostile pawns could be attacked by a given pawn.
     *
     * @param pawn Pawn which is is tested on whether he can attack hostile
     *             pawns.
     * @return List of hostile pawns which are threatened. {@code null} if
     * there are none.
     */
    //TODO param color nicht so schön
    //TODO schöner: isPawn threatened so wie isPawnProtected
    public List<Pawn> determineThreatenedPawns(Pawn pawn,
                                               Color hostileColor) {
        ChessBoard tempBoard = (ChessBoard) this.clone();

        //TODO hat diese Liste dann nicht immer nur ein Element???!
        List<Pawn> threatenedPawns = new ArrayList<>();
/*
        // Diagonal left attack possible.
        if (getSlot(pawn.getColumn() - 1, pawn.getRow() + 1)
                == hostileColor) {
            threatenedPawns.add(getPawn(pawn.getColumn() - 1, pawn.getRow() + 1,
                    hostileColor));
        }

        // Diagonal right attack possible.
        if (getSlot(pawn.getColumn() + 1, pawn.getRow() + 1)
                == hostileColor) {
            threatenedPawns.add(getPawn(pawn.getColumn() + 1, pawn.getRow() + 1,
                    hostileColor));
        }

 */

        Color pawnColor = getSlot(pawn.getColumn(), pawn.getRow());

        if (pawnColor == getHumanColor()) {
            // Pawn is facing north on the board.

            // Add to list if diagonal left attack possible.
            if (getSlot(pawn.getColumn() - 1, pawn.getRow() + 1)
                    == hostileColor) {
                threatenedPawns.add(getPawn(pawn.getColumn() - 1, pawn.getRow() + 1,
                        hostileColor));
            }

            // Add to list if diagonal right attack possible.
            if (getSlot(pawn.getColumn() + 1, pawn.getRow() + 1)
                    == hostileColor) {
                threatenedPawns.add(getPawn(pawn.getColumn() + 1, pawn.getRow() + 1,
                        hostileColor));
            }

            /*
            // Checks if diagonally behind are friendly pawns.
            return getPawn(pawn.getColumn() - 1, pawn.getRow() - 1
                    , pawnColor) != null || getPawn(pawn.getColumn() + 1,
                    pawn.getRow() - 1, pawnColor) != null;

             */
        } else {
            // Pawn is facing south on the board.

            // Add to list if diagonal left attack possible.
            if (getSlot(pawn.getColumn() - 1, pawn.getRow() - 1)
                    == hostileColor) {
                threatenedPawns.add(getPawn(pawn.getColumn() - 1,
                        pawn.getRow() - 1,
                        hostileColor));
            }

            // Add to list if diagonal right attack possible.
            if (getSlot(pawn.getColumn() + 1, pawn.getRow() - 1)
                    == hostileColor) {
                threatenedPawns.add(getPawn(pawn.getColumn() + 1,
                        pawn.getRow() - 1,
                        hostileColor));
            }

/*
            // Checks if diagonally in front (from the perspective of the
            // player) are friendly pawns.
            return getPawn(pawn.getColumn() - 1, pawn.getRow() + 1
                    , pawnColor) != null || getPawn(pawn.getColumn() + 1,
                    pawn.getRow() + 1, pawnColor) != null;

 */
        }


        return threatenedPawns;
    }

    /**
     * Determines if  a pawns of a certain color is protected by one or more
     * friendly pawns.
     *
     * @param
     * @return
     */
    //TODO param color nicht so schön
    public boolean isPawnProtected(Pawn pawn, Color pawnColor) {

        if (pawn == null) {
            throw new IllegalArgumentException("There is no pawn.");
        }

        if (pawnColor == getHumanColor()) {
            // Checks if diagonally behind are friendly pawns.
            return getPawn(pawn.getColumn() - 1, pawn.getRow() - 1
                    , pawnColor) != null || getPawn(pawn.getColumn() + 1,
                    pawn.getRow() - 1, pawnColor) != null;
        } else {
            // Checks if diagonally in front (from the perspective of the
            // player) are friendly pawns.
            return getPawn(pawn.getColumn() - 1, pawn.getRow() + 1
                    , pawnColor) != null || getPawn(pawn.getColumn() + 1,
                    pawn.getRow() + 1, pawnColor) != null;
        }

    }

    /**
     * Returns amount of pawns in a given row.
     *
     * @param row
     * @param pawns
     * @return
     */
    public int amountOfPawnsInRow(int row, List<Pawn> pawns) {
        int amount = 0;

        for (Pawn pawn : pawns) {
            if (pawn.getRow() == row) {
                amount++;
            }
        }

        return amount;
    }

    //TODO: praktisch nur eine code duplikation von Node.createChildren
    public boolean hasToSuspendMove(Player player) {

        //TODO getplayerPawns methode verwenden

        // List of players pawns
        List<Pawn> playerPawns = new ArrayList<>();

        if (player.getColor() == Color.WHITE) {
            playerPawns = whitePawns;
        } else if (player.getColor() == Color.BLACK) {
            playerPawns = blackPawns;
        } else {
            throw new IllegalArgumentException("Please call  this method with "
                    + "player human or machine.");
        }

        // Determine whether any pawn from the players pawns could make a legal
        // move.
        for (Pawn pawn : playerPawns) {
            for (Direction direction : Direction.values()) {
                //Can move at most one column.
                for (int colTo = pawn.getColumn() - 1; colTo
                        <= pawn.getColumn() + 1; colTo++) {
                    //Can move at most two rows.
                    for (int rowTo = pawn.getRow() - 2; rowTo <= pawn.getRow() + 2;
                         rowTo++) {

                        Tuple temp = this.isLegalMove(direction, pawn, colTo,
                                rowTo);

                        // If the move is legal and an actual movement takes
                        // place.
                        if (temp.getLegalityOfMove()
                                && (colTo != pawn.getColumn()
                                || rowTo != pawn.getRow())) {

                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isGameOver() {

        //List of pawns that start in the upper row.
        List<Pawn> upperRowPawns;

        // List pawns that start in the lowest row.
        List<Pawn> lowerRowPawns;

        if (getHumanColor() == Color.WHITE) {
            lowerRowPawns = whitePawns;
            upperRowPawns = blackPawns;
        } else {
            lowerRowPawns = blackPawns;
            upperRowPawns = whitePawns;
        }

        if (whitePawns.isEmpty() || blackPawns.isEmpty()) {
            return true;
        }


        // Determine whether a pawn has reached his final row.
        if (amountOfPawnsInRow(1, upperRowPawns) > 0
                || amountOfPawnsInRow(8, lowerRowPawns) > 0) {
            return true;
        } else {

            /*
            // Determine whether any pawn could make a legal move.
            for (Pawn pawn : lowerRowPawns) {
                for (int colTo = pawn.getColumn() - 1; colTo <=
                        pawn.getColumn() + 1; colTo++) {
                    for (int rowTo = pawn.getRow(); rowTo <=
                            pawn.getRow() + 1; rowTo++) {
                        //TODO: hier isLegalMove stattdessen verwenden
                        if (move(pawn.getColumn(),
                                pawn.getRow(), colTo, rowTo) != null) {
                            return false;
                        }
                    }
                }
            }
            for (Pawn pawn : upperRowPawns) {
                for (int colTo = pawn.getColumn() - 1; colTo <=
                        pawn.getColumn() + 1; colTo++) {
                    for (int rowTo = pawn.getRow(); rowTo <=
                            pawn.getRow() + 1; rowTo++) {
                        if (move(pawn.getColumn(),
                                pawn.getRow(), colTo, rowTo) != null) {
                            return false;
                        }
                    }
                }
            }

             */

            // Determine whether there is a legal move left.
            return hasToSuspendMove(machine) && hasToSuspendMove(human);
        }
    }

    @Override
    public Board clone() {
        ChessBoard copy;

        try {
            copy = (ChessBoard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        List<Pawn> whitePawnsClone = new ArrayList<>();
        List<Pawn> blackPawnsClone = new ArrayList<>();

        for (Pawn whitePawn : whitePawns) {
            whitePawnsClone.add(whitePawn.clone());
        }

        for (Pawn blackPawn : blackPawns) {
            blackPawnsClone.add(blackPawn.clone());
        }

        copy.whitePawns = whitePawnsClone;
        copy.blackPawns = blackPawnsClone;

        return copy;
    }

    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        if (level > 0 && level < 5) {
            machine.setLevel(level);
        } else {
            //TODO keine exception!
            throw new IllegalArgumentException("The level needs to be within "
                    + "the interval 1-4.");
        }
    }

    @Override
    public Player getWinner() {

        //TODO: Code duplizierung

        //List of pawns belonging to machine player.
        List<Pawn> machinePawns;

        // List of pawns belonging to human player.
        List<Pawn> humanPawns;

        if (getHumanColor() == Color.WHITE) {
            humanPawns = whitePawns;
            machinePawns = blackPawns;
        } else {
            humanPawns = blackPawns;
            machinePawns = whitePawns;
        }


        // Determine whether a player has reached the enemies base row.
        if (amountOfPawnsInRow(1, machinePawns) > 0) {
            return machine;
        } else if (amountOfPawnsInRow(SIZE, humanPawns) > 0) {
            return human;
        } else {

            //Determine whether one or both player has no pawns left.
            if (humanPawns.isEmpty() && machinePawns.isEmpty()) {
                return null;
            } else if (humanPawns.isEmpty()) {
                return machine;
            } else if (machinePawns.isEmpty()) {
                return human;
            }

        }
        if (!isGameOver()) {
            //TODO: passende Exception?
            throw new IllegalCallerException("This game is not over - there "
                    + "cannot be a winner or draw.");
        } else {

            // Return null to indicate a draw.
            return null;
        }
    }

    /**
     * Returns a pawn in a specified position. If there is no pawn at that
     * place null will be returned.
     *
     * @param col The column in which to look for the pawn.
     * @param row The row in which to look for the pawn.
     * @return The pawn at the given position
     */
    public Pawn getPawn(int col, int row) throws IllegalArgumentException {

        //TODO defensiv: z.B. eingabe außerhalb von Feld (ne das würd die
        // implementierung von isPawnProtected zerficken) oder color none

        /*
        // List of pawns to search the pawn to be returned in.
        List<Pawn> pawns;

        if (pawnColor == Color.WHITE) {
            pawns = whitePawns;
        } else {
            pawns = blackPawns;
        }

        for (Pawn pawn : pawns) {
            if (pawn.getRow() == row && pawn.getColumn() == col) {
                return pawn;
            }
        }

        // If no pawn was found at that position from that color.
        return null;
         */

        if (col < 1 || col > SIZE || row < 1 || row > SIZE) {
            throw new IllegalArgumentException("This position is not within "
                    + "the board.");
        }

        // Determine whether a white pawn is on this tile.
        for (Pawn whitePawn : whitePawns) {
            if (whitePawn.getColumn() == col && whitePawn.getRow() == row) {
                return whitePawn;
            }
        }

        // Determine whether a black pawn is on this tile.
        for (Pawn blackPawn : blackPawns) {
            if (blackPawn.getColumn() == col && blackPawn.getRow() == row) {
                return blackPawn;
            }
        }

        // If no pawn was found return null value.
        return null;
    }

    @Override
    public Color getSlot(int col, int row) {

        // Determine whether a white pawn is on this tile.
        for (Pawn whitePawn : whitePawns) {
            if (whitePawn.getColumn() == col && whitePawn.getRow() == row) {
                return Color.WHITE;
            }
        }

        // Determine whether a black pawn is on this tile.
        for (Pawn blackPawn : blackPawns) {
            if (blackPawn.getColumn() == col && blackPawn.getRow() == row) {
                return Color.BLACK;
            }
        }

        return Color.NONE;
    }

    @Override
    public int getNumberOfTiles(Player player) {
        if (player.getColor() == Color.WHITE) {
            return whitePawns.size();
        } else {
            return blackPawns.size();
        }
    }

    public List<Pawn> getWhitePawns() {
        return whitePawns;
    }

    public List<Pawn> getBlackPawns() {
        return blackPawns;
    }

    public Color getMachineColor() {
        return machine.getColor();
    }
}
