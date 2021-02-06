package view;

import model.chessboard.Board;

import javax.swing.*;
import java.awt.*;

/**
 * This panel represents a tile of a chessboard. It has a button hidden
 * underneath it, which will trigger a determination whether a move can be made.
 */
public class ChessSlotPanel extends JPanel {

    // The model of the chess game.
    private Board gameBoard;

    // The column and row on the chessboard which this tile represents.
    private final int col;
    private final int row;

    // A button hidden underneath the panel.
    private final JButton slotButton = new JButton();

    // Used to color tile cyan if this tiles pawn was selected to be moved.
    private boolean isSelectedPawn = false;

    /**
     * Constructor for a chessboard-slot.
     *
     * @param gameBoard The model of the chessboard.
     * @param col       The column on the chessboard which this tile represents.
     * @param row       The row on the chessboard which this tile represents.
     */
    public ChessSlotPanel(Board gameBoard, int col, int row) {
        super();

        if (gameBoard == null) {
            throw new IllegalArgumentException("The board must not be null.");
        } else if ((col < 1 || col > Board.SIZE) || (row < 1
                || row > Board.SIZE)) {
            throw new IllegalArgumentException("The tiles position must be "
                    + "within the board.");
        }

        this.gameBoard = gameBoard;
        this.col = col;
        this.row = row;

        initSlotButton();
    }

    /**
     * Initiates a button underneath the panel. If pressed it will trigger
     * determinations regarding possible moves.
     */
    private void initSlotButton() {
        slotButton.setContentAreaFilled(false);
        slotButton.setBorderPainted(false);
        slotButton.setPreferredSize(this.getMaximumSize());

        slotButton.addActionListener(e -> {
            GUI parentFrame = (GUI) getTopLevelAncestor();
            JButton source = (JButton) e.getSource();

            parentFrame.attemptMove((ChessSlotPanel) source.getParent());
        });
        this.add(slotButton);
    }

    /**
     * Paints the background so that a chess pattern develops. Also draws a
     * pawn if it is contained in this slot. If this slot was selected it is
     * highlighted  in cyan.
     *
     * @param g A graphics element.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Create chess pattern or highlight.
        if (isSelectedPawn) {
            this.setBackground(Color.CYAN);
        } else if ((row + col) % 2 == 0) {
            this.setBackground(Color.LIGHT_GRAY);
        } else {
            this.setBackground(Color.DARK_GRAY);
        }

        Graphics2D g2 = (Graphics2D) g;

        // Determine the center of the slot for easy calculation of positions.
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (gameBoard.getSlot(col, row) == model.chessboard.Color.WHITE) {

            // Draw outlines of the white pawns head and torso.
            g2.setColor(Color.BLACK);
            g2.drawOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.drawOval(centerX - (getWidth() / 6), centerY
                            - (getHeight() / 3), (getWidth() / 3),
                    getHeight() / 3);

            // Draw white pawns head and torso.
            g2.setColor(Color.WHITE);
            g2.fillOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.fillOval(centerX - (getWidth() / 6), centerY
                            - (getHeight() / 3), getWidth() / 3,
                    getHeight() / 3);

        } else if (gameBoard.getSlot(col, row)
                == model.chessboard.Color.BLACK) {

            // Draw black pawns head and torso
            g2.setColor(Color.BLACK);
            g2.fillOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.fillOval(centerX - (getWidth() / 6), centerY - (getHeight() / 3),
                    getWidth() / 3, getHeight() / 3);
        }
    }

    /**
     * Getter for the column of the slot on the chessboard.
     *
     * @return The column of the slot on the chessboard.
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter for the row of the slot on the chessboard.
     *
     * @return The row of the slot on the chessboard.
     */
    public int getRow() {
        return row;
    }

    /**
     * Updates the Game-Board. This is necessary for the determination of if
     * there is pawn on this tile etc.
     *
     * @param gameBoard The model of a chessboard.
     */
    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Getter for the button hidden underneath the panel.
     *
     * @return The button hidden underneath the panel.
     */
    public JButton getSlotButton() {
        return slotButton;
    }

    /**
     * Notify the panel of whether it was selected if it contains a friendly
     * pawn. This triggers the panel to be highlighted whenever it is redrawn.
     *
     * @param isSelectedPawn {@code true} if the panel was selected. {@code
     * false} if the panel is meant to be unselected.
     */
    public void setSelectedPawn(boolean isSelectedPawn) {
        this.isSelectedPawn = isSelectedPawn;
    }
}
