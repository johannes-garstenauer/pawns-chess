package model;

import java.util.ArrayList;
import java.util.List;

public class BoardImpl implements Board, Cloneable {

    //TODO: wo wird dem spieler seine farbe zugewiesen?
    public Player human = Player.HUMAN;
    public Player machine = Player.MACHINE;

    public Player nextPlayer;

    public List<Pawn> whitePawns = new ArrayList<>();
    public List<Pawn> blackPawns = new ArrayList<>();

    //public LookAheadTree lookAheadTree;

    //TODO wieso hier kein throws Illegalarg nötig?
    public BoardImpl(int level, Color humanColor) {

        // If this is a new game, no parameters (das is kake)
        // Else same parameters as in old game
        //TODO: hier defensiver
        //TODO: stattdessen boolean wert newGame? an konstruktor übergeben
        //TODO -> wäre schöner
        if (level == 0 && humanColor == null) {
            machine.setLevel(3);
            nextPlayer = human;
        } else if (!(level >= 1 && level < 5 && humanColor != null)) {
            throw new IllegalArgumentException("Incoherent Arguments");
        } else {
            machine.setLevel(level);
            human.setColor(humanColor);

            if (humanColor == Color.WHITE) {
                machine.setColor(Color.BLACK);
                nextPlayer = human;
            } else {
                machine.setColor(Color.WHITE);
                nextPlayer = machine;
            }
        }

        createInitialPawnPositions();

        //this.lookAheadTree = new LookAheadTree(this);
        //lookAheadTree.getRoot().constructTree(level);
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
     */
    private void createInitialPawnPositions() {
        //TODO: Defensiv: sind die listen leer?
        if (human.getColor() == Color.BLACK) {
            for (int i = 1; i < SIZE + 1; i++) {
                blackPawns.add(new Pawn(i, 1));
                whitePawns.add(new Pawn(i, SIZE));
            }
        } else {
            for (int i = 1; i < SIZE + 1; i++) {
                blackPawns.add(new Pawn(i, SIZE));
                whitePawns.add(new Pawn(i, 1));
            }
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

    public void makeMove(Pawn pawn, Tupel tupel, int colTo, int rowTo) {
        //TODO: hier übergabe von nur attackedPawn statt tupel
        if (tupel.getAttackedPawn() != null) {
            if (getWhitePawns().contains(tupel.getAttackedPawn())) {
                getWhitePawns().remove(tupel.getAttackedPawn());
            } else if (getBlackPawns().contains(tupel.getAttackedPawn())) {
                getBlackPawns().remove(tupel.getAttackedPawn());
            } else {
                throw new IllegalArgumentException("There was no pawn to be "
                        + "attacked found.");
            }
        }

        pawn.setColumn(colTo);
        pawn.setRow(rowTo);
        pawn.hasMoved();

        //TODO: vorläufig bis createChildren in Node move und machineMove
        // aufruft
        // oder auch nicht vorläufig

        if(getNextPlayer() == Player.HUMAN) {
            setNextPlayer(Player.MACHINE);
        } else {
            setNextPlayer(Player.HUMAN);
        }
    }

    /**
     * Returns pawn to be attacked and if it is a legal move
     *
     * @param move
     * @param pawn
     * @param colTo
     * @param rowTo
     * @return
     */
    public Tupel isLegalMove(Move move, Pawn pawn, int colTo,
                             int rowTo) {

        //TODO: Überprüfung ob currentplayer aussetzen muss

        try {
            if (determineMove(pawn.getColumn(), pawn.getRow(), colTo, rowTo) != move) {
                return new Tupel(false, null);
            }
        } catch (IllegalArgumentException e) {
            return new Tupel(false, null);
        }

        //TODO diese Code duplizierung auslagern.

        // List of friendly pawns.
        List<Pawn> friendlyPawns;

        // List of hostile pawns.
        List<Pawn> hostilePawns;

        if (getSlot(pawn.getColumn(), pawn.getRow()) == Color.WHITE) {
            friendlyPawns = whitePawns;
            hostilePawns = blackPawns;
        } else {
            friendlyPawns = blackPawns;
            hostilePawns = whitePawns;
        }


        if (move == Move.FORWARD || move == Move.DOUBLEFORWARD) {

            //TODO: replace fors with getPawn oder getSlot
            //TODO: doubleforward kann pawns überspringt

            // Determine whether a friendly pawn blocks this move.
            for (Pawn friendlyPawn : friendlyPawns) {
                if (colTo == friendlyPawn.getColumn()
                        && rowTo == friendlyPawn.getRow()) {
                    return new Tupel(false, null);
                }
            }
            // Determine whether a hostile pawn blocks this move.
            for (Pawn hostilePawn : hostilePawns) {
                if (colTo == hostilePawn.getColumn()
                        && rowTo == hostilePawn.getRow()) {
                    return new Tupel(false, null);
                }
            }

            /*
            // Determine whether this pawn can make a double move forward.
            if (move != Move.DOUBLEFORWARD || pawn.isOpeningMove()) {
                return new Tupel(true, null);
            }
             */

            if (move == Move.DOUBLEFORWARD) {

                if (pawn.isOpeningMove()) {

                    // Determine whether the pawn is moving up or down
                    if (pawn.getRow() > rowTo) {

                        // Determine whether there is a pawn blocking the double move
                        if (getSlot(pawn.getColumn(), pawn.getRow() - 1) != Color.NONE) {
                            return new Tupel(false, null);
                        } else {
                            return new Tupel(true, null);
                        }
                    } else {

                        // Determine whether there is a pawn blocking the double move
                        if (getSlot(pawn.getColumn(), pawn.getRow() + 1) != Color.NONE) {
                            return new Tupel(false, null);
                        } else {
                            return new Tupel(true, null);
                        }
                    }
                } else {
                    return new Tupel(false, null);
                }

            } else {

                // Result for a normal move forward
                return new Tupel(true, null);
            }

        } else if (move == Move.DIAGONALLEFT || move == Move.DIAGONALRIGHT) {

            //TODO: consolidate for loops -> all in one with variables being
            // assigned
            //TODO: replace fors with getPawn

            // Determine whether a hostile pawn can be attacked.
            for (Pawn hostilePawn : hostilePawns) {
                if (colTo == hostilePawn.getColumn()
                        && rowTo == hostilePawn.getRow()) {

                    return new Tupel(true, hostilePawn);
                }
            }
        }
        return new Tupel(false, null);
    }

    @Override
    public Board move(int colFrom, int rowFrom, int colTo, int rowTo) {

        //TODO FUnktioniert nur für Spieler Pawns evtl aber als hilfsmethode
        // für machinemove gebraucht...

        //TODO !!! Abfrage in Shell ob isGameOver

        //TODO this?? gud? -> combine with if unten
        if (nextPlayer != human) {
            throw new IllegalCallerException("its not your turn duuude");
        }

        // Assign move and determine whether it is a legal move.
        //TODO: diese funktionalität gehört eig in isLegalMove -> Move mit
        // rowDist und colDist erzeugen zur vereinfachung
        Move move;
        try {
            move = determineMove(colFrom, rowFrom, colTo, rowTo);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        //Board on which the move is executed.
        BoardImpl newBoard = (BoardImpl) this.clone();

        Pawn pawnToBeMoved = newBoard.getPawn(colFrom, rowFrom,
                getHumanColor());

        Tupel temp = isLegalMove(move, pawnToBeMoved, colTo, rowTo);

        if (temp.getLegalityOfMove() && pawnToBeMoved != null) {
            newBoard.makeMove(pawnToBeMoved, temp, colTo, rowTo);
            //newBoard.setNextPlayer(machine);

            /*
            // Construct new LookAheadTree
            newBoard.lookAheadTree.getRoot().constructTree(machine.getLevel());
             */

            return newBoard;
        } else {
            //TODO: oder IllegalMoveExc?
            return null;
        }

    }


    //TODO: determineMove und isLegalMove kombinieren?
    private Move determineMove(int colFrom, int rowFrom, int colTo,
                               int rowTo) throws IllegalArgumentException {

        //TODO: IllegalArgument durch IllegalMove ersetzen.
        //Todo: throws??
        if (colFrom < 1 || colFrom > SIZE || rowFrom < 1 || rowFrom > SIZE
                || colTo < 1 || colTo > SIZE || rowTo < 1 || rowTo > SIZE) {
            throw new IllegalArgumentException("Moves must occur within the "
                    + "board!");
        }

        // Distance moved between columns.
        int colDist = colTo - colFrom;

        // Distance moved between rows.
        int rowDist = rowTo - rowFrom;

        // Color of the pawn.
        Color pawnColor = getSlot(colFrom, rowFrom);

        if (pawnColor == Color.NONE) {
            throw new IllegalArgumentException("Moves must be executed on "
                    + "existing Pawns.");
        } else if (pawnColor == Color.WHITE && !(rowDist > 0)){
            throw new IllegalArgumentException("This move has an illegal "
                    + "direction.");
        } else if (pawnColor == Color.BLACK && !(rowDist < 0)){
            throw new IllegalArgumentException("This move has an illegal "
                    + "direction.");
        }

        rowDist = Math.abs(rowDist);

        if (colDist == 0 && rowDist == 1) {
            return Move.FORWARD;
        } else if (colDist == 0 && rowDist == 2) {
            return Move.DOUBLEFORWARD;
        } else if (colDist == -1 && rowDist == 1) {
            return Move.DIAGONALLEFT;
        } else if (colDist == 1 && rowDist == 1) {
            return Move.DIAGONALRIGHT;
        } else {
            throw new IllegalArgumentException("This move has an illegal "
                    + "distance or direction.");
        }
    }

    @Override
    public Board machineMove() {
        if (nextPlayer != machine) {
            throw new IllegalCallerException("its not your turn duuude");
        }

        //Board on which the move is executed.
        BoardImpl newBoard = (BoardImpl) this.clone();

        LookAheadTree lookAheadTree = new LookAheadTree(this);

        // Construct new LookAheadTree
        lookAheadTree.constructTree(machine.getLevel());

        //nextPlayer = human;
        return null;
    }


    /**
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

    Color getMachineColor() {
        return machine.getColor();
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
        BoardImpl tempBoard = (BoardImpl) this.clone();

        List<Pawn> threatenedPawns = new ArrayList<>();

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

        // Checks if diagonally behind are friendly pawns.
        return getPawn(pawn.getColumn() - 1, pawn.getRow() - 1
                , pawnColor) != null || getPawn(pawn.getColumn() + 1,
                pawn.getRow() - 1, pawnColor) != null;

    }

    //TODO param color nicht so schön
    public Pawn getPawn(int col, int row, Color pawnColor) {

        //TODO defensiv: z.B. eingabe außerhalb von Feld (ne das würd die
        // implementierung von isPawnProtected zerficken) oder color none

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

    @Override
    public void setLevel(int level) throws IllegalArgumentException {
        if (level > 0 && level < 5) {
            machine.setLevel(level);
        } else {
            throw new IllegalArgumentException("The level needs to be within "
                    + "the interval 1-4.");
        }
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
            for (Move move : Move.values()) {
                //Can move at most one column.
                for (int colTo = pawn.getColumn() - 1; colTo
                        <= pawn.getColumn() + 1; colTo++) {
                    //Can move at most two rows.
                    for (int rowTo = pawn.getRow() - 2; rowTo <= pawn.getRow() + 2;
                         rowTo++) {

                        Tupel temp = this.isLegalMove(move, pawn, colTo,
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


    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
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

        } if (!isGameOver()) {
            //TODO: passende Exception?
            throw new IllegalCallerException("This game is not over - there "
                    + "cannot be a winner or draw.");
        } else {

            // Return null to indicate a draw.
            return null;
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
    public Board clone() {
        BoardImpl copy;

        try {
            copy = (BoardImpl) super.clone();
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
        //copy.lookAheadTree = lookAheadTree.clone();

        // Clone of enums human and machine not required or legal due to
        // singleton quality of enums.

        return copy;
    }

    public List<Pawn> getWhitePawns() {
        return whitePawns;
    }

    public List<Pawn> getBlackPawns() {
        return blackPawns;
    }
}
