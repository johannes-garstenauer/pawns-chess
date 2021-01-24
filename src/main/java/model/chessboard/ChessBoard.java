package model.chessboard;

import model.exceptions.IllegalMoveException;
import model.lookAheadTree.Node;
import model.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * An implementation of the {@code Board} interface.
 */
public class ChessBoard implements Board, Cloneable {

    /**
     * The player who can move next.
     */
    public Player nextPlayer;

    /**
     * The pawns of the player with the white color.
     */
    public List<Pawn> whitePawns = new ArrayList<>();

    /**
     * The pawns of the player with the black color.
     */
    public List<Pawn> blackPawns = new ArrayList<>();

    /**
     * This is the constructor of an implementation of a {@code Board} for
     * pawns chess.
     * The pawns will be initiated at the opposing sides of the board.
     *
     * @param level      The difficulty level of the machine player. This
     *                   determines how many moves the machine player can
     *                   simulate into the future in order to determine his
     *                   best next move.
     * @param humanColor The color of your pawns. If you are white you will
     *                   start first. If you are black the machine player
     *                   will start the game. Regardless of your color, your
     *                   pawns will start at the bottom of the board.
     * @throws IllegalArgumentException Throws an exception if the level or
     *                                  the humanColor are not valid.
     */
    public ChessBoard(int level, Color humanColor)
            throws IllegalArgumentException {

        if (humanColor == null || humanColor == Color.NONE) {
            throw new IllegalArgumentException("The human player has to have "
                    + "a valid color.");
        } else if (level < 1) {
            throw new IllegalArgumentException("The machine's level must be "
                    + "positive and greater than zero.");
        } else {
            Player.MACHINE.setLevel(level);
            Player.HUMAN.setColor(humanColor);
            Player.MACHINE.setColor(Color.getOppositeColor(humanColor));
            nextPlayer = getOpeningPlayer();
        }
        createInitialPawnPositions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getOpeningPlayer() {
        if (getHumanColor() == Color.WHITE) {
            return Player.HUMAN;
        } else {
            return Player.MACHINE;
        }
    }

    /**
     * Initiates the pawns in their starting positions.
     * The pawns of the human player will be placed on the lowest row of the
     * board. The machines pawns on the highest. The pawns are assigned to
     * their respective lists of pawns ( {@code whitePawns} or {@code
     * blackPawns}) in a left to right order from the pawns perspective.
     *
     * @throws IllegalStateException The boards pawn lists have to be empty.
     *                               If there are already pawns on the board the
     *                               initial pawn positions should not be
     *                               created.
     */
    private void createInitialPawnPositions() {
        assert whitePawns.isEmpty() && blackPawns.isEmpty();

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
     * @return The direction in which the move occurs or
     * {@code Direction.ILLEGAL_DIRECTION} if the direction of the move is
     * illegal.
     */
    private Direction determineDirection(int colFrom, int rowFrom, int colTo,
                                         int rowTo) {

        // Make sure the move occurs within the boards limits.
        assert (colFrom >= 1 && colFrom <= SIZE && rowFrom >= 1
                && rowFrom <= SIZE && colTo >= 1 && colTo <= SIZE
                && rowTo >= 1 && rowTo <= SIZE);

        // Make sure the move is executed on an existing pawn.
        assert (getPawn(colFrom, rowFrom) != null);

        // Distance moved between columns.
        int colDist = colTo - colFrom;

        // Distance moved between rows.
        int rowDist = rowTo - rowFrom;

        // Make sure that the move is not in the wrong the direction.
        if ((getNextPlayer() == Player.HUMAN && !(rowDist > 0))
                || (getNextPlayer() == Player.MACHINE && !(rowDist < 0))) {
            return Direction.ILLEGAL_DIRECTION;
        } else {

            // Determining the absolute distance between rows is now possible as
            // we now know that the direction is correct.
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

                // The move is not possible.
                return Direction.ILLEGAL_DIRECTION;
            }
        }
    }


    /**
     * Determine whether a given move is legal.
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
    private boolean isLegalMove(Pawn pawn, int colTo,
                                int rowTo) {

        assert pawn != null;
        if (!(colTo >= 1 && colTo <= SIZE && rowTo >= 1 && rowTo <= SIZE)) {
            return false;
        } else {

            Direction direction = determineDirection(pawn.getColumn(),
                    pawn.getRow(), colTo, rowTo);

            // Determine whether the move has a legal direction.
            if (direction == Direction.ILLEGAL_DIRECTION) {
                return false;
            } else {

                // List of friendly pawns.
                List<Pawn> friendlyPawns = getPawnsList(getColor(pawn));

                // List of hostile pawns.
                List<Pawn> hostilePawns = getPawnsList(Color.getOppositeColor(
                        getColor(pawn)));


                if (direction == Direction.FORWARD
                        || direction == Direction.DOUBLE_FORWARD) {

                    // Determine whether a pawn blocks this move.
                    if (friendlyPawns.contains(getPawn(colTo, rowTo))
                            || hostilePawns.contains(getPawn(colTo, rowTo))) {

                        // Return false if a pawn blocks this move.
                        return false;
                    } else if (direction == Direction.DOUBLE_FORWARD) {

                        // Determine whether the pawn is moving up or down.
                        if (nextPlayer == Player.HUMAN) {

                            // Determine whether there is a pawn blocking the
                            // double move.
                            return pawn.isOpeningMove()
                                    && getPawn(pawn.getColumn(),
                                    pawn.getRow() + 1) == null;
                        } else {

                            // Determine whether there is a pawn blocking the
                            // double move.
                            return pawn.isOpeningMove()
                                    && getPawn(pawn.getColumn(),
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
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int colFrom, int rowFrom, int colTo, int rowTo)
            throws IllegalMoveException, IllegalArgumentException {

        if (nextPlayer != Player.HUMAN) {
            throw new IllegalMoveException("It is not the human player's "
                    + "turn.");
        } else if (isGameOver()) {
            throw new IllegalMoveException("No more moves can be made as this"
                    + " game is already finished.");
        } else if (hasToSuspend(Player.HUMAN)) {
            throw new IllegalMoveException("You must miss a turn.");
        } else if (colFrom < 1 || colFrom > SIZE || rowFrom < 1
                || rowFrom > SIZE || colTo < 1 || colTo > SIZE || rowTo < 1
                || rowTo > SIZE) {
            throw new IllegalArgumentException("The move must occur within "
                    + "the board!");
        } else if (getPawn(colFrom, rowFrom) == null) {
            throw new IllegalArgumentException("There is no pawn to be moved "
                    + "at the given position.");
        } else if (!this.getPawnsList(getHumanColor())
                .contains(getPawn(colFrom, rowFrom))) {
            throw new IllegalArgumentException("The pawn at the given "
                    + "position is hostile and can therefore not be moved!");
        } else {

            // Board on which the move is executed.
            ChessBoard newBoard = (ChessBoard) this.clone();

            // Pawn that will attempt to make a move.
            Pawn pawnToBeMoved = newBoard.getPawn(colFrom, rowFrom);

            if (isLegalMove(pawnToBeMoved, colTo, rowTo)) {
                newBoard.makeMove(pawnToBeMoved, colTo, rowTo);

                if (!hasToSuspend(Player.HUMAN)) {
                    newBoard.nextPlayer = Player.MACHINE;
                }
                return newBoard;
            } else {

                // Return null because the move is not legal.
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
    private void makeMove(Pawn pawn, int colTo, int rowTo) {
        assert pawn != null;

        if (isLegalMove(pawn, colTo, rowTo)) {

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
            pawn.setHasMoved();
        }
    }

    /**
     * Creates a value for a Chessboard by inspecting the positions of the
     * pawns. A high return value indicates a board favorable to the machine
     * player.
     *
     * @param depth The depth which this board has within the look-ahead-tree
     *              {@code Node} that determines the machines next move.
     * @return The value of the board indicating if its pawns are in a
     * favorable position.
     */
    private double createBoardRating(int depth) {
        assert depth >= 0;

        // List of humans pawns.
        List<Pawn> humanPawns = getPawnsList(getHumanColor());

        // List of machines pawns.
        List<Pawn> machinePawns =
                getPawnsList(Color.getOppositeColor(getHumanColor()));

        double n = calculateValueN(humanPawns, machinePawns);

        double d = calculateValueD(humanPawns, machinePawns);

        double c = calculateValueC(humanPawns, machinePawns);

        double i = calculateValueI(humanPawns, machinePawns);

        double v = calculateValueV(humanPawns, machinePawns, depth);
        return n + d + c + i + v;
    }

    /**
     * Returns a value which indicates whether the amount of pawns for each
     * player is in favor of the machine player.
     *
     * @param humanPawns   The list of pawns that belong to the human player.
     * @param machinePawns The list of pawns that belong to the machine player.
     * @return The value regarding the amount of pawns.
     */
    private double calculateValueN(List<Pawn> humanPawns,
                                   List<Pawn> machinePawns) {
        assert humanPawns != null && machinePawns != null;

        return machinePawns.size() - 1.5 * humanPawns.size();
    }

    /**
     * Returns a value which indicates whether the distance travelled by
     * all pawns is favourable for the machine player.
     *
     * @param humanPawns   The list of pawns that belong to the human player.
     * @param machinePawns The list of pawns that belong to the machine player.
     * @return The value regarding the distance travelled by pawns.
     */
    private double calculateValueD(List<Pawn> humanPawns,
                                   List<Pawn> machinePawns) {
        assert humanPawns != null && machinePawns != null;

        int machinePawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {

            machinePawnsMovedFactor = machinePawnsMovedFactor
                    + amountOfPawnsInRow(SIZE - i,
                    machinePawns) * i;
        }


        int humanPawnsMovedFactor = 0;
        for (int i = 0; i < SIZE; i++) {

            humanPawnsMovedFactor =
                    humanPawnsMovedFactor + amountOfPawnsInRow(i + 1,
                            humanPawns) * i;
        }

        return machinePawnsMovedFactor - 1.5 * humanPawnsMovedFactor;
    }

    /**
     * Returns a value which indicates whether the amount of pawns that are
     * threatened but not protected is favourable to the machine player-
     *
     * @param humanPawns   The list of pawns that belong to the human player.
     * @param machinePawns The list of pawns that belong to the machine player.
     * @return The value regarding the amount of threatened pawns.
     */
    private double calculateValueC(List<Pawn> humanPawns,
                                   List<Pawn> machinePawns) {
        assert humanPawns != null && machinePawns != null;

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

        return amountOfThreatenedHumanPawns
                - 1.5 * amountOfThreatenedMachinePawns;
    }

    /**
     * Returns a value which indicates whether the amount of pawns that are
     * isolated from other friendly pawns is favourable to the machine player.
     *
     * @param humanPawns   The list of pawns that belong to the human player.
     * @param machinePawns The list of pawns that belong to the machine player.
     * @return The value regarding the amount of isolated pawns.
     */
    private double calculateValueI(List<Pawn> humanPawns,
                                   List<Pawn> machinePawns) {
        assert humanPawns != null && machinePawns != null;

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

        return amountOfIsolatedHumanPawns - 1.5 * amountOfIsolatedMachinePawns;
    }

    /**
     * Returns a value which indicates whether the the winner of the board is
     * the machine or the human in relationship with how many moves it would
     * take the player to reach this win. This will always return zero if
     * nobody has won this board.
     *
     * @param humanPawns   The list of pawns that belong to the human player.
     * @param machinePawns The list of pawns that belong to the machine player.
     * @param depth        The depth of the given board in the look-ahead tree
     *                     that determines the machines next move.
     * @return The value regarding the winner of the board.
     */
    private double calculateValueV(List<Pawn> humanPawns,
                                   List<Pawn> machinePawns, int depth) {
        assert humanPawns != null && machinePawns != null;

        double v;
        if (isGameOver() && depth != 0) {

            // Player which might have won by playing the last move.
            if (getWinner() == Player.HUMAN) {

                // Case for if the human has won.
                return -1.5 * 5000 / (double) depth;
            } else if (getWinner() == Player.MACHINE) {

                // Case for if the machine has won.
                return 5000 / (double) depth;
            } else {

                // Case for ig there is a draw.
                return 0;
            }
        } else {

            // Victory value for the root or if the game has not yet ended.
            return 0;
        }
    }

    /**
     * Determines whether a pawn has neighbors within a one tile radius from
     * the same player.
     *
     * @param pawn The pawn which is examined for neighbors.
     * @return Return {@code true} if the pawn does not have neighbors from
     * the same player. Otherwise return {@code false}.
     */
    private boolean isPawnIsolated(Pawn pawn) {

        assert pawn != null;
        assert whitePawns.contains(pawn) || blackPawns.contains(pawn);

        //Circles around the pawns position
        for (int col = -1; col <= 1; col++) {
            for (int row = -1; row <= 1; row++) {

                // The pawn that is neighboring and from the same player.
                Pawn possibleNeighborPawn;

                possibleNeighborPawn = getPawn(pawn.getColumn() + col,
                        pawn.getRow() + row);

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

        // If there is no other pawn from the same player the pawn is isolated.
        return true;
    }

    /**
     * Determines which hostile pawns could be attacked by the given pawn.
     *
     * @param pawn Pawn which is is tested on whether he can attack hostile
     *             pawns.
     * @return List of hostile pawns which are threatened. {@code null} if
     * there are none.
     */
    private List<Pawn> determineThreatenedPawns(Pawn pawn) {

        assert pawn != null;
        assert whitePawns.contains(pawn) || blackPawns.contains(pawn);

        List<Pawn> threatenedPawns = new ArrayList<>();
        Color pawnColor = getColor(pawn);

        int flag; // Indicator for the direction in which the pawn is facing.
        if (pawnColor != getHumanColor()) {
            flag = -1; // Pawn is facing south;
        } else {
            flag = 1; // Pawn is facing north
        }

        Pawn attackedPawn = getPawn(pawn.getColumn() - 1,
                pawn.getRow() + flag);

        // Add to list if diagonal left attack possible.
        if (attackedPawn != null
                && Color.getOppositeColor(pawnColor)
                == getColor(attackedPawn)) {
            threatenedPawns.add(attackedPawn);
        }

        attackedPawn = getPawn(pawn.getColumn() + 1,
                pawn.getRow() + flag);

        // Add to list if diagonal right attack possible.
        if (attackedPawn != null
                && Color.getOppositeColor(pawnColor)
                == getColor(attackedPawn)) {
            threatenedPawns.add(attackedPawn);
        }
        return threatenedPawns;
    }

    /**
     * Determines if a pawn is protected by one or more friendly pawns.
     *
     * @param pawn The pawn that is examined.
     * @return Return {@code true} if the pawn has protection. Otherwise
     * return {@code false}.
     */
    private boolean isPawnProtected(Pawn pawn) {

        assert pawn != null;
        assert whitePawns.contains(pawn) || blackPawns.contains(pawn);

        int flag; // Indicator for the direction in which the pawn is facing.
        if (getColor(pawn) != getHumanColor()) {
            flag = -1; // Pawn is facing south;
        } else {
            flag = 1; // Pawn is facing north
        }

        // The pawn that would be protecting from the left side.
        Pawn leftBehind = getPawn(pawn.getColumn() - 1,
                pawn.getRow() - flag);

        // The pawn that would be protecting from the right side.
        Pawn rightBehind = getPawn(pawn.getColumn() + 1,
                pawn.getRow() - flag);

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
     */
    private int amountOfPawnsInRow(int row, List<Pawn> pawns) {

        assert row >= 1 && row <= SIZE;

        int amount = 0;
        for (Pawn pawn : pawns) {
            if (pawn.getRow() == row) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {

        //List of pawns that start in the upper row.
        List<Pawn> upperRowPawns
                = getPawnsList(Color.getOppositeColor(getHumanColor()));

        // List pawns that start in the lowest row.
        List<Pawn> lowerRowPawns = getPawnsList(getHumanColor());

        // Determine if one player has run out of pawns.
        if (upperRowPawns.isEmpty() || lowerRowPawns.isEmpty()) {
            return true;
        }

        // Determine whether a pawn has reached his final row.
        if (amountOfPawnsInRow(1, upperRowPawns) > 0
                || amountOfPawnsInRow(8, lowerRowPawns) > 0) {
            return true;
        } else {

            // Determine whether there is a legal move left.
            return hasToSuspend(Player.MACHINE) && hasToSuspend(Player.HUMAN);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() throws IllegalMoveException {

        if (nextPlayer != Player.MACHINE) {
            throw new IllegalMoveException("It is not the machine player's "
                    + "turn.");
        } else if (isGameOver()) {
            throw new IllegalMoveException("No more moves can be made as this"
                    + " game is already finished.");
        } else if (hasToSuspend(Player.MACHINE)) {
            throw new IllegalMoveException("Machine must miss a turn.");
        } else {

            // Board on which the move is executed.
            ChessBoard newBoard = (ChessBoard) this.clone();

            // Create a look-ahead tree with which the best next move for the
            // machine will be determined.
            Node<ChessBoard> root = new Node<>(newBoard, null, 0);
            newBoard.constructTree(root, Player.MACHINE.getLevel());
            newBoard.assignValues(root);

            //TODO
            //System.out.println(root.getChildWithHighestValue().getValue());

            //TODO wird nextPlayer schon durch possibleMoves richtig gesetzt?
            return root.getMaxChild().getContent();
        }
    }

    /**
     * Recursively constructs a sub tree of node with possible moves.
     *
     * @param node  The root of the sub tree.
     * @param level The difficulty level of the machine if the node is the
     *              actual root. Otherwise it is the machine level subtracted
     *              by the depth in the tree.
     */
    private void constructTree(Node<ChessBoard> node, int level) {
        assert node != null;
        if (level > 0) {

            // Create child nodes for node.
            for (ChessBoard possibleMove : possibleMoves(nextPlayer)) {
                Node<ChessBoard> child =
                        new Node<>(possibleMove, node, level - 1);

                node.addChild(child);
                possibleMove.constructTree(child, level - 1);
            }
        }
    }

    /**
     * Recursivel assigns values to the boards within the tree represented by
     * the {@code node}. A min-max algorithm is employed.
     *
     * @param node The node representing the root of the tree for which the
     *             values are assigned.
     */
    private void assignValues(Node<ChessBoard> node) {

        assert node != null;

        //TODO Die ganze node.getContent sache ist unschön aber notwendig?
        if (node.getChildren().isEmpty()) {

            // Assign a value to a leaf node.
            node.setValue(node.getContent()
                    .createBoardRating(node.getHeight()));
        } else {

            // Recursively assign values to children.
            for (Node<ChessBoard> child : node.getChildren()) {

                if (child.getValue() == Integer.MIN_VALUE) {

                    // If the child does not have a calculated value.
                    child.getContent().assignValues(child);
                }

            }

            // Assign a value to an inner node.
            if (getNextPlayer() == Player.HUMAN) {
                node.setValue(node.getContent()
                        .createBoardRating(node.getHeight())
                        + node.getMinChild().getValue());
            } else {
                node.setValue(node.getContent()
                        .createBoardRating(node.getHeight())
                        + node.getMaxChild().getValue());
            }
        }
    }

    /**
     * Determines whether a player has to suspend his next move.
     *
     * @param player The player whose pawns are examined.
     * @return Return {@code true} if the player has to suspend. Return
     * {@code false} otherwise.
     */
    private boolean hasToSuspend(Player player) {
        return possibleMoves(player).isEmpty();
    }

    /**
     * Determines all the moves that a player can make in a left to right
     * order (a normal move forward comes before a double move forward).
     *
     * @param player The player whose pawns are examined.
     * @return A list of boards where each board has a possible move executed
     * on itself.
     */
    private List<ChessBoard> possibleMoves(Player player) {

        // Assign parameter player as next player in order to accurately
        // determine the legality of possible moves.
        //TODO doch irgendwie unschön
        Player actualNextPlayer = nextPlayer;
        nextPlayer = player;

        // List of pawns out of whose moves the children nodes will be
        // constructed.
        List<Pawn> pawns = getPawnsList(player.getColor());

        // List of possibleMoves by the given player.
        List<ChessBoard> possibleMoves = new ArrayList<>();

        int flag; // Indicator for the direction in which the pawn is facing.
        if (player == Player.MACHINE) {
            flag = -1; // Pawn is facing south;
        } else {
            flag = 1; // Pawn is facing north
        }

        for (Pawn pawn : pawns) {
            for (Direction direction : Direction.values()) {
                if (direction != Direction.ILLEGAL_DIRECTION) {
                    int colTo;
                    int rowTo;
                    if (direction == Direction.FORWARD) {
                        colTo = pawn.getColumn();
                        rowTo = pawn.getRow() + flag;
                    } else if (direction == Direction.DOUBLE_FORWARD) {
                        colTo = pawn.getColumn();
                        rowTo = pawn.getRow() + 2 * flag;
                    } else if (direction == Direction.DIAGONAL_LEFT) {
                        colTo = pawn.getColumn() - flag;
                        rowTo = pawn.getRow() + flag;
                    } else {
                        colTo = pawn.getColumn() + flag;
                        rowTo = pawn.getRow() + flag;
                    }

                    // Determine if the move is legal and an actual movement
                    // takes place.
                    if (isLegalMove(pawn, colTo, rowTo)) {

                        ChessBoard boardClone = (ChessBoard) this.clone();

                        //TODO getPawn verletzung klassengeheimnis??
                        // aber wiesonst
                        boardClone.makeMove(boardClone.getPawn(pawn.getColumn(),
                                pawn.getRow()), colTo, rowTo);

                        boardClone.nextPlayer
                                = Player.getOppositePlayer(player);
                        possibleMoves.add(boardClone);
                    }
                }
            }
        }

        // Reassign the actual next player to be the next player again;
        nextPlayer = actualNextPlayer;
        return possibleMoves;
    }

    /**
     * Deep clones an instance of {@code ChessBoard}.
     *
     * @return Returns a deep clone of a board.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        if (level > 0) {
            Player.MACHINE.setLevel(level);
        } else {
            throw new IllegalArgumentException("The level must be at least 1.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() throws IllegalCallerException {

        if (!isGameOver()) {
            throw new IllegalCallerException("This game is not over - there "
                    + "cannot be a winner or draw.");
        } else {
            //List of pawns belonging to machine player.
            List<Pawn> machinePawns = getPawnsList(
                    Color.getOppositeColor(getHumanColor()));

            //TODO STALEMATE -> player with more pawns wins.

            // List of pawns belonging to human player.
            List<Pawn> humanPawns = getPawnsList(getHumanColor());

            // Determine whether a player has reached the enemies base row.
            if (amountOfPawnsInRow(1, machinePawns) > 0) {
                return Player.MACHINE;
            } else if (amountOfPawnsInRow(SIZE, humanPawns) > 0) {
                return Player.HUMAN;
            } else {

                //Determine whether one or both player has no pawns left.
                if (humanPawns.isEmpty() && machinePawns.isEmpty()) {

                    // Return null to indicate a draw.
                    return null;
                } else if (humanPawns.isEmpty()) {
                    return Player.MACHINE;
                } else if (machinePawns.isEmpty()) {
                    return Player.HUMAN;
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
     * @return The pawn at the given position or {@code null} if there is no
     * pawn at that position or if the position is not within the board.
     */
    private Pawn getPawn(int col, int row) {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getSlot(int col, int row) throws IllegalArgumentException {
        if (col < 1 || col > SIZE || row < 1 || row > SIZE) {
            throw new IllegalArgumentException("The given coordinates must be "
                    + "within the board.");
        }
        return getColor(getPawn(col, row));
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

    /**
     * {@inheritDoc}
     */
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
     */
    private List<Pawn> getPawnsList(Color listColor) {
        assert listColor != null;
        if (listColor == Color.WHITE) {
            return whitePawns;
        } else {
            return blackPawns;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getHumanColor() {
        return Player.HUMAN.getColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getNextPlayer() {
        return nextPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int row = SIZE; row >= 1; row--) {
            for (int col = 1; col <= SIZE; col++) {
                stringBuilder.append(getSlot(col, row).toString());
                if (col != SIZE) {
                    stringBuilder.append(" ");
                }
            }
            if (row != 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
