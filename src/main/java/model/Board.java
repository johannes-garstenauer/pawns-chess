package model;

/**
 * Interface for a Pawns Chess game, a lite variant of chess. The only tiles are
 * pawns. Winner is who first reaches the opponent's ground line and gains a
 * queen, or in case of a draw who owns more remaining pawns.
 *
 * There are some differences to traditional chess: In case that one player has
 * no option to make a valid move, he must miss a turn. If both players
 * subsequently must miss a turn, the game ends in draw. There is no en passant
 * capture rule.
 *
 * A human plays against the machine. The human's ground line is always row 1,
 * whereas the ground line of the machine is row 8. The human plays from bottom
 * to top, the machine from top to bottom. The user with the white tiles opens
 * the game.
 */
public interface Board extends Cloneable {

    /**
     * The number of columns (files) and rows (ranks) of the game grid.
     * Originally 8. Here, at least 4.
     */
    int SIZE = 4;

    /**
     * Gets the player who should or already has opened the game. As an
     * invariant, this player has always the white tiles.
     *
     * @return The player who makes the initial move.
     */
    Player getOpeningPlayer();

    /**
     * Gets the color of the human player.
     *
     * @return The tile color of the human.
     */
    Color getHumanColor();

    /**
     * Gets the player who is allowed to execute the next move.
     *
     * @return The player who shall make the next move.
     */
    Player getNextPlayer();

    /**
     * Executes a human move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @param colFrom The slot's column number from which the tile of the human
     *        player should be moved.
     * @param rowFrom The slot's row number from which the tile of the human
     *        player should be moved.
     * @param colTo The slot's column number to which the tile of the human
     *        player should be moved.
     * @param rowTo The slot's row number to which the tile of the human player
     *        should be moved.
     * @return A new board with the move executed. If the move is invalid, e.g.,
     *         no legal capture or not a move forward to a free slot, then
     *         {@code null} will be returned.
     * @throws IllegalMoveException If the game is already over, or it is not
     *         the human's turn.
     * @throws IllegalArgumentException If the provided parameters are invalid,
     *         e.g., one of the defined slots is outside the grid.
     */
    Board move(int colFrom, int rowFrom, int colTo, int rowTo);

    /**
     * Executes a machine move.This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @return A new board with the move executed.
     * @throws IllegalMoveException If the game is already over, or it is not
     *         the machine's turn.
     */
    Board machineMove();

    /**
     * Sets the skill level of the machine.
     *
     * @param level The skill of the machine as a number. Must be at least 1.
     */
    void setLevel(int level);

    /**
     * Checks if the game is over. Either one player has won or there is a draw,
     * i.e., no player can perform any further move.
     *
     * @return {@code true} if and only if the game is over.
     */
    boolean isGameOver();

    /**
     * Checks if the game state is won.
     *
     * @return The winner or nobody in case of a draw.
     */
    Player getWinner();

    /**
     * Gets the number of tiles/pawns currently placed on the grid of the
     * provided player.
     *
     * @param player The player for which to count the tiles.
     * @return The number of tiles of {@code player}.
     */
    int getNumberOfTiles(Player player);

    /**
     * Gets the color (black or white) of a pawn in the slot at the specified
     * coordinates. If the slot is empty, then the result is no color (e.g.
     * NONE).
     *
     * @param row The row of the slot in the game grid.
     * @param col The column of the slot in the game grid.
     * @return The slot color.
     */
    Color getSlot(int col, int row);

    /**
     * Deep-copies the board.
     *
     * @return A clone.
     */
    Board clone();

    /**
     * Gets the string representation of this board as row x column matrix. Each
     * slot is represented by one the three chars ' ', 'W', or 'B'. ' ' means
     * that the slot currently contains no tile. 'W' means that it contains a
     * white tile. 'B' means that it contains a black tile. In contrast to the
     * rows, the columns are whitespace separated.
     *
     * @return The string representation of the current Pawns Chess game.
     */
    @Override
    String toString();

}
