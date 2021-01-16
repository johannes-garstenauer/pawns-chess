package model.player;

import model.chessboard.Color;

public enum Player {
    //TODO: JAVADOC
    //TODO: (Color) gute Idee? -> in diesem Fall default case zuweisungen
    HUMAN(Color.WHITE), MACHINE(Color.BLACK);

    //TODO: gut so? + Javadoc
    private Color color;
    public int level;

    //TODO: Konstruktor gute Idee?
    Player(Color color) {
        this.color = color;
    }

    /**
     *  Returns the tile color of this player.
     *
     * @return Tile color.
     */
    public Color getColor() {
        return color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Determines the player opposite to the player which was given.
     *
     * @param player The given player. Has to be either {@code Player.HUMAN} or
     *              {@code Color.MACHINE}.
     * @return The opposite player of the given player, if possible.
     * @throws IllegalArgumentException Might be thrown if the given player
     *                                  does not have an opposite player.
     */
    public static Player getOppositePlayer(Player player) throws IllegalArgumentException {
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
