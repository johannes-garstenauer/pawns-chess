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
}
