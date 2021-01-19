package model.chessboard;

import model.exceptions.IllegalMoveException;
import model.lookAheadTree.Node;
import model.player.Player;

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
                //TODO nicht nextplayer sondern currentplayer??
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
        List<Pawn> friendlyPawns = getPawnsList(getColor(pawn));

        // List of hostile pawns.
        List<Pawn> hostilePawns = getPawnsList(Color.getOppositeColor(
                getColor(pawn)));


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
        if (isLegalMove(pawn, colTo, rowTo) //&& !isGameOver()
         ) {

            // Determine whether a hostile pawn is at the finish position of
            // the move.
            if (Color.getOppositeColor(getSlot(colTo, rowTo))
                    == getColor(pawn)) {

                // Remove the pawn which is to be attacked.
                getPawnsList(getSlot(colTo, rowTo)).remove(getPawn(colTo,
                        rowTo));
            }
            pawn.setColumn(colTo);
            pawn.setRow(rowTo);
            pawn.hasMoved();
        }
    }


    //TODO in hilfsmethoden aufteilen
    //TODO defensiv auf depth reagieren
    public double createBoardRating(int depth) {

        // List of humans pawns.
        List<Pawn> humanPawns = getPawnsList(getHumanColor());

        // List of machines pawns.
        List<Pawn> machinePawns =
                getPawnsList(Color.getOppositeColor(getHumanColor()));

        double n = machinePawns.size() - 1.5 * humanPawns.size();

        int machinePawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {

            machinePawnsMovedFactor =
                    machinePawnsMovedFactor +
                            amountOfPawnsInRow(SIZE - i,
                                    machinePawns) * i;
        }


        int humanPawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {

            humanPawnsMovedFactor =
                    humanPawnsMovedFactor + amountOfPawnsInRow(i + 1,
                            humanPawns) * i;
        }

        double d = machinePawnsMovedFactor - 1.5 * humanPawnsMovedFactor;

        List<Pawn> threatenedHumanPawns = new ArrayList<>();

        // Create list of all human pawns that are threatened by machine pawns.
        for (Pawn machinePawn : machinePawns) {
            threatenedHumanPawns.addAll(determineThreatenedPawns(machinePawn));
        }

        // Remove all those human pawns that are protected by friendly pawns.
        threatenedHumanPawns.removeIf(this::isPawnProtected);

        // Remove duplicates.
        threatenedHumanPawns =
                new ArrayList<>(new HashSet<>(threatenedHumanPawns));

        int amountOfThreatenedHumanPawns = threatenedHumanPawns.size();

        List<Pawn> threatenedMachinePawns = new ArrayList<>();

        // Create list of all machine pawns that are threatened by human pawns.
        for (Pawn humanPawn : humanPawns) {
            threatenedMachinePawns.addAll(determineThreatenedPawns(humanPawn));
        }

        // Remove all those machine pawns that are protected by friendly pawns.
        threatenedMachinePawns.removeIf(this::isPawnProtected);

        // Remove duplicates.
        threatenedMachinePawns =
                new ArrayList<>(new HashSet<>(threatenedMachinePawns));

        int amountOfThreatenedMachinePawns = threatenedMachinePawns.size();

        double c = amountOfThreatenedHumanPawns
                - 1.5 * amountOfThreatenedMachinePawns;

        int amountOfIsolatedMachinePawns = 0;
        for (Pawn machinePawn : machinePawns) {
            if (isPawnIsolated(machinePawn)) {
                amountOfIsolatedMachinePawns++;
            }
        }

        int amountOfIsolatedHumanPawns = 0;
        for (Pawn humanPawn : humanPawns) {
            if (isPawnIsolated(humanPawn)) {
                amountOfIsolatedHumanPawns++;
            }
        }

        double i =
                amountOfIsolatedHumanPawns - 1.5 * amountOfIsolatedMachinePawns;

        double v;
        if (depth != 0 && isGameOver()) {

            // Player which might have won by playing the last move.
            if (getWinner() == Player.HUMAN) {

                // If the human has won.
                v = -1.5 * 5000 / (double) depth;
            } else if (getWinner() == Player.MACHINE) {

                // If the machine has won.
                //TODO is cast necessary?
                v = 5000 / (double) depth;
            } else {

                // If there is a draw.
                v = 0;
            }
        } else {

            // Victory value for root.
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

    //TODO: defensiv auf pawn reagieren?

    /**
     * Determines whether a pawn has neighbors within a one tile radius from
     * the same player.
     *
     * @param pawn The pawn which is examined for neighbors.
     * @return Return {@code true} if the pawn does not have neighbors from
     * the same player. Otherwise return {@code false}.
     */
    private boolean isPawnIsolated(Pawn pawn) {

        //Circles around the pawns position
        for (int col = -1; col <= 1; col++) {
            for (int row = -1; row <= 1; row++) {

                // The pawn that be neighboring and from the same player.
                Pawn possibleNeighborPawn = null;
                try {
                    possibleNeighborPawn = getPawn(pawn.getColumn() + col,
                            pawn.getRow() + row);
                } catch (IllegalArgumentException exception) {
                    // Do nothing.
                }

                //TODO getColor exc catchen?
                if (possibleNeighborPawn != null
                        && !possibleNeighborPawn.equals(pawn)
                        && getColor(possibleNeighborPawn)
                        == getColor(pawn)) {

                    // If there is another pawn from the same player the pawn
                    // is not isolated.
                    return false;
                }
            }
        }
        return true;
    }

    //TODO: defensiv auf pawn reagieren?

    /**
     * Determines which hostile pawns could be attacked by the given pawn.
     *
     * @param pawn Pawn which is is tested on whether he can attack hostile
     *             pawns.
     * @return List of hostile pawns which are threatened. {@code null} if
     * there are none.
     * @throws IllegalArgumentException May be thrown if the pawn does not
     *                                  belong to one of the players.
     */
    private List<Pawn> determineThreatenedPawns(Pawn pawn) {

        //TODO ist das überhaupt notwendig?
        // ja -> in unserem kontext nur so sinnvoll
        //nein -> wieso sollte es nicht in einem anderen kontext verwendet
        // werden dürfen? -> wieder entfernen
        if (!(whitePawns.contains(pawn) || blackPawns.contains(pawn))) {
            throw new IllegalArgumentException("The pawn needs to be on "
                    + "the board and assigned to one of the players.");
        }

        List<Pawn> threatenedPawns = new ArrayList<>();

        //TODO hier exc catchen? -> sonst gleich in if-Bed ziehen
        Color pawnColor = getColor(pawn);

        int flag = 1; // Pawn is facing north
        if (pawnColor != getHumanColor()) { // Pawn is facing south;
            flag = -1;
        }

        Pawn attackedPawn;
        try {

            attackedPawn = getPawn(pawn.getColumn() - 1,
                    pawn.getRow() + flag);

            // Add to list if diagonal left attack possible.
            if (attackedPawn != null
                    && Color.getOppositeColor(pawnColor) == getColor(attackedPawn)) {
                threatenedPawns.add(attackedPawn);
            }

            attackedPawn = getPawn(pawn.getColumn() + 1,
                    pawn.getRow() + flag);

            // Add to list if diagonal right attack possible.
            if (attackedPawn != null
                    && Color.getOppositeColor(pawnColor) == getColor(attackedPawn)) {
                threatenedPawns.add(attackedPawn);
            }

        } catch (IllegalArgumentException exception) {
            // Do nothing.
        }


        return threatenedPawns;
    }

    /**
     * Determines if a pawn is protected by one or more friendly pawns.
     *
     * @param pawn The pawn that is examined.
     * @return Return {@code true} if the pawn has protection. Otherwise
     * return {@code false}.
     * @throws IllegalArgumentException May be thrown if the pawn does not
     *                                  belong to one of the players.
     */
    public boolean isPawnProtected(Pawn pawn)
            throws IllegalArgumentException {

        if (!(whitePawns.contains(pawn) || blackPawns.contains(pawn))) {
            throw new IllegalArgumentException("The pawn needs to be on "
                    + "the board and assigned to one of the players.");
        }

        int flag = 1; // Pawn is facing north.
        if (getColor(pawn) == getHumanColor()) {
            flag = -1; // Pawn is facing south.
        }

        // The pawn that would be protecting from the left side.
        Pawn leftBehind = null;
        try {
            leftBehind = getPawn(pawn.getColumn() - 1,
                    pawn.getRow() + flag);
        } catch (IllegalArgumentException exception) {
            // Do nothing.
        }

        // The pawn that would be protecting from the right side.
        Pawn rightBehind = null;
        try {
            rightBehind = getPawn(pawn.getColumn() + 1,
                    pawn.getRow() + flag);
        } catch (IllegalArgumentException exception) {
            // Do nothing.
        }

        // Determine whether the pawn is protected either from the left or
        // the right.
        return rightBehind != null || leftBehind != null;
    }

    /**
     * Returns the amount of pawns in a given row.
     *
     * @param row   The row that is examined.
     * @param pawns The list from which the pawns are being looked for in the
     *              given row.
     * @return The amount of pawns from the given list in the given row.
     * @throws IllegalArgumentException Might be thrown if the row is not on
     *                                  the board.
     */
    public int amountOfPawnsInRow(int row, List<Pawn> pawns) {

        if (row < 1 || row > SIZE) {
            throw new IllegalArgumentException("The row must be within the "
                    + "limits of the board.");
        }

        int amount = 0;
        for (Pawn pawn : pawns) {
            if (pawn.getRow() == row) {
                amount++;
            }
        }
        return amount;
    }

    @Override
    public boolean isGameOver() {

        //List of pawns that start in the upper row.
        List<Pawn> upperRowPawns
                = getPawnsList(Color.getOppositeColor(getHumanColor()));

        // List pawns that start in the lowest row.
        List<Pawn> lowerRowPawns = getPawnsList(getHumanColor());


        if (whitePawns.isEmpty() || blackPawns.isEmpty()) {
            return true;
        }

        // Determine whether a pawn has reached his final row.
        if (amountOfPawnsInRow(1, upperRowPawns) > 0
                || amountOfPawnsInRow(8, lowerRowPawns) > 0) {
            return true;
        } else {

            // Determine whether there is a legal move left.
            return possibleMoves(machine).isEmpty()
                    && possibleMoves(human).isEmpty();
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

            // Board on which the move is executed.
            ChessBoard newBoard = (ChessBoard) this.clone();

            // Create a look-ahead tree with which the best next move for the
            // machine will be determined.
            Node<ChessBoard> root = new Node<>(newBoard, null, 0);
            newBoard.constructTree(root, machine.getLevel());
            newBoard.assignValues(root);

            return (Board) root.getBestChild();
        }
    }

    public void constructTree(Node<ChessBoard> node, int level) {
        if (level > 0) {

            // Create child nodes for node.
            for (ChessBoard possibleMove :
                    possibleMoves(nextPlayer)) {

                Node<ChessBoard> child =
                        new Node<>(possibleMove, node, level - 1);

                node.addChild(child);
                possibleMove.constructTree(child, level - 1);
            }
            /*
            // In case the player has to suspend a move let the other
            // player move.
            //TODO ich glaube mein neuse setnextplayer macht das überflüßßig?
            if (possibleMoves(nextPlayer).isEmpty() &&!isGameOver()) {
                if (!board.isGameOver()) {
                    if (getNextPlayer() == Player.HUMAN) {
                        board.setNextPlayer(Player.MACHINE);
                        createChildren(Player.MACHINE);
                    } else {
                        board.setNextPlayer(Player.HUMAN);
                        createChildren(Player.HUMAN);
                    }
             */
        }
    }


    public void assignValues(Node<ChessBoard> node) {

        if (node.getChildren().isEmpty()) {

            // Assign value to leaf.
            node.setValue(node.getContent().createBoardRating(node.getHeight()));
        } else {

            // Recursively assign values to children.
            for (Node child : node.getChildren()) {
                assignValues(child);
            }

            // Assign value to inner node.
            if (getNextPlayer() == Player.HUMAN) {
                node.setValue(node.getContent().createBoardRating(node.getHeight())
                        + node.getWorstChild().getValue());
            } else {
                node.setValue(node.getContent().createBoardRating(node.getHeight())
                        + node.getBestChild().getValue());
            }
        }
    }

    //TODO create method bool canMove(player) which checks if possileMoves
    // .isempty
    // TODO use flag!
    //TODO möglicherweise attribut nextplayer entfernbar?
    public List<ChessBoard> possibleMoves(Player player) {

        // List of pawns out of whose moves the children nodes will be
        // constructed.
        List<Pawn> pawns = getPawnsList(player.getColor());

        // List of possibleMoves by the given player.
        List<ChessBoard> possibleMoves = new ArrayList<>();

        //TODO USSEEEEEE MEEEE
        int flag = 1;
        if (player == Player.MACHINE) {
            flag = -1;
        }

        // Create child nodes for each possible move by each pawn.
        for (Pawn pawn : pawns) {

            //Can move at most one column.
            for (int colTo = pawn.getColumn() - 1; colTo
                    <= pawn.getColumn() + 1; colTo++) {

                //Can move at most two rows.
                for (int rowTo = pawn.getRow() - 2; rowTo <= pawn.getRow() + 2;
                     rowTo++) {

                    // If the move is legal and an actual movement takes place.
                    if (isLegalMove(pawn, colTo, rowTo)
                            && ((colTo != pawn.getColumn()
                            || rowTo != pawn.getRow()))
                            //&& !isGameOver()
                            ) {
                        //TODO verletzung klassengeheimnis entfernen
                        // durch flag

                        ChessBoard boardClone = (ChessBoard) this.clone();
                        //TODO getPawn verletzung klassengeheimnis?? aber wiesonst
                        boardClone.makeMove(boardClone.getPawn(pawn.getColumn(),
                                pawn.getRow()), colTo, rowTo);
                        boardClone.setNextPlayer();
                        possibleMoves.add(boardClone);
                    }
                }
            }
        }
        return possibleMoves;
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
        if (level > 0) {
            machine.setLevel(level);
        } else {
            throw new IllegalArgumentException("The level must be at least 1.");
        }
    }

    @Override
    public Player getWinner() throws IllegalCallerException {

        if (!isGameOver()) {
            throw new IllegalCallerException("This game is not over - there "
                    + "cannot be a winner or draw.");
        } else {
            //List of pawns belonging to machine player.
            List<Pawn> machinePawns = getPawnsList(
                    Color.getOppositeColor(getHumanColor()));

            // List of pawns belonging to human player.
            List<Pawn> humanPawns = getPawnsList(getHumanColor());

            // Determine whether a player has reached the enemies base row.
            if (amountOfPawnsInRow(1, machinePawns) > 0) {
                return machine;
            } else if (amountOfPawnsInRow(SIZE, humanPawns) > 0) {
                return human;
            } else {

                //Determine whether one or both player has no pawns left.
                if (humanPawns.isEmpty() && machinePawns.isEmpty()) {

                    // Return null to indicate a draw.
                    return null;
                } else if (humanPawns.isEmpty()) {
                    return machine;
                } else if (machinePawns.isEmpty()) {
                    return human;
                } else {

                    // Return null to indicate a draw.
                    return null;
                }
            }
        }
    }

    /**
     * Returns a pawn in a specified position. If there is no pawn at that
     * place null will be returned.
     *
     * @param col The column in which to look for the pawn.
     * @param row The row in which to look for the pawn.
     * @return The pawn at the given position
     * @throws IllegalArgumentException May be thrown if the pawn is within
     *                                  the board's limits.
     */
    public Pawn getPawn(int col, int row) throws IllegalArgumentException {

        //TODO defensiv: z.B. eingabe außerhalb von Feld (ne das würd die
        // implementierung von isPawnProtected zerficken) oder color none

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

    //TODO inheritDoc um throws erweitern
    @Override
    public Color getSlot(int col, int row) throws IllegalArgumentException {

        Pawn pawn;
        try {
            pawn = getPawn(col, row);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("The slot must be within the "
                    + "limits of the board.");
        }

        return getColor(pawn);
    }

    /**
     * Determines the color of a given pawn.
     *
     * @param pawn The pawn that is examined.
     * @return The color of the pawn. If the pawn is not assigned to any of
     * the two players {@code Color.NONE} will be returned.
     */
    private Color getColor(Pawn pawn) {

        if (whitePawns.contains(pawn)) {
            return Color.WHITE;
        } else if (blackPawns.contains(pawn)) {
            return Color.BLACK;
        } else {
            return Color.NONE;
        }
    }

    @Override
    public int getNumberOfTiles(Player player) {
        if (player.getColor() == Color.WHITE) {
            return whitePawns.size();
        } else {
            return blackPawns.size();
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
        if (!possibleMoves(Player.getOppositePlayer(nextPlayer)).isEmpty()) {
            nextPlayer = Player.getOppositePlayer(nextPlayer);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int row = SIZE; row >= 1; row--) {
            for (int col = 1; col <= SIZE; col++) {
                stringBuilder.append(getSlot(col,row).toString()).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
