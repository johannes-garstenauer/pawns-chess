package model.player;

import model.chessboard.Color;

/**
 * An enumeration of players. A player has an associated (through his color)
 * list of pawns on the board.
 */
public enum Player {

    /**
     * The human player.
     */
    HUMAN,

    /**
     * The machine player.
     */
    MACHINE;

    /**
     * The players color.
     */
    private Color color;

    /**
     * The machines difficulty level. It indicates the depth of the
     * look-ahead tree which the machine can use to find its next best move.
     * High values will be very demanding on runtime and resources.
     */
    private int level;

    /**
     * Returns the tile color of this player.
     *
     * @return The tile color of the player.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Setter for the color of the player.
     *
     * @param color The color which to which the player will be assigned.
     * @throws IllegalArgumentException Will be thrown if the color of the
     *                                  player is not {@code Color.WHITE} or
     *                                  {@code Color.BLACK}.
     */
    public void setColor(Color color) throws IllegalArgumentException {
        if (color != Color.BLACK && color != Color.WHITE) {
            throw new IllegalArgumentException("The pawn should either be "
                    + "white or black.");
        }
        this.color = color;
    }

    /**
     * Setter for the difficulty level.
     *
     * @param level The difficulty level to which the machine will be set.
     * @throws IllegalCallerException Will be thrown if it is attempted to
     *                                assign a level to the human.
     */
    public void setLevel(int level) throws IllegalCallerException {
        if (this == HUMAN) {
            throw new IllegalCallerException("The human does not need a "
                    + "difficulty level.");
        }
        this.level = level;
    }

    /**
     * Getter for the machines difficulty level.
     *
     * @return Returns the machines level.
     * @throws IllegalCallerException Will be thrown if it is attempted to
     * get the level of the human.
     */
    public int getLevel() {
        if (this == HUMAN) {
            throw new IllegalCallerException("The human does not have a "
                    + "difficulty level.");
        }
        return level;
    }

    /**
     * Determines the player opposite to the player which was given.
     *
     * @param player The given player. Has to be either {@code Player.HUMAN} or
     *               {@code Color.MACHINE}.
     * @return The opposite player of the given player, if possible.
     * @throws IllegalArgumentException Might be thrown if the given player
     *                                  does not have an opposite player.
     */
    public static Player getOppositePlayer(Player player)
            throws IllegalArgumentException {
        if (player == null) {
            throw new IllegalArgumentException("Cannot determine the opposite"
                    + " of that color.");
        } else {
            if (player == Player.HUMAN) {
                return Player.MACHINE;
            } else {
                return Player.HUMAN;
            }
        }
    }
}
