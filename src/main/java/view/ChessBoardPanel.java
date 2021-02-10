package view;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.exceptions.IllegalMoveException;
import model.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Panel on which the chessboard is being displayed.
 */
public class ChessBoardPanel extends JPanel {

    /**
     * Collects all the panels contained within the chessboard.
     */
    ChessSlotPanel[][] slots = new ChessSlotPanel[Board.SIZE][Board.SIZE];

    /**
     * This is the default difficulty level of the machine opponent.
     */
    private static int DEFAULT_DIFFICULTY = 3;

    /**
     * This is the default color of the human player.
     */
    private static Color DEFAULT_HUMANCOLOR = Color.WHITE;

    /**
     * This is the Board on which the current game is being played.
     */
    private Board gameBoard;

    /**
     * This contains the selected panels that are used to calculate a human
     * move. It should never contain more than two elements.
     */
    private final List<ChessSlotPanel> moveParams = new ArrayList<>();

    /**
     * On this thread the machine move is calculated.
     */
    private MachineMoveThread machineMoveThread = new MachineMoveThread();

    /**
     * A thread with the sole purpose of calculating a machine move.
     */
    private class MachineMoveThread extends Thread {

        /**
         * Calculates a machine move and inform the player if the game is over.
         * Automatically makes a move whenever the human cannot. The
         * resulting move is stored in {@code gameBoard} and the {@code
         * chessBoardPanel} will be repainted if successful.
         */
        @Override
        public void run() {

            // Disable the humans possibility to move while the machine has not
            // yet  moved.
            setEnabledOnChessBoardPanels(false);

            if (gameBoard.getNextPlayer() != Player.MACHINE) {

                // Inform the player, that he has to move again.
                JOptionPane.showMessageDialog(null,
                        "Machine cannot move, please move again.");
            } else {
                try {
                    gameBoard = gameBoard.machineMove();
                } catch (IllegalMoveException illegalMoveException) {
                    if (gameBoard.isGameOver()) {
                        announceWinner();
                    }
                } finally {
                    if (gameBoard.isGameOver()) {
                        announceWinner();
                    } else {
                        GUI frame = ((GUI) ChessBoardPanel.this
                                .getTopLevelAncestor());

                        // Update the components about the changes made by the
                        // move.
                        frame.updateAndPaintAmountOfPawns();

                        // Let the event queue handle the repaint.
                        SwingUtilities.invokeLater
                                (ChessBoardPanel.this::updateSlots);

                        // Move again if the human cannot move. This seems
                        // to be not functional due to a flaw in the model.
                        if (gameBoard.getNextPlayer() != Player.HUMAN) {

                            // Re-enable the humans possibility to move then
                            // stop the thread.
                            setEnabledOnChessBoardPanels(true);
                            stopThread();

                            // Inform the player, then move again.
                            JOptionPane.showMessageDialog(null,
                                    "You have to skip a move. "
                                            + "Machine will move again.");
                            MachineMoveThread thread = new MachineMoveThread();
                            thread.run();
                        }

                        // Re-enable the humans possibility to move.
                        setEnabledOnChessBoardPanels(true);
                    }
                }
            }
        }

        /**
         * Stops the running thread and sets it to null.
         */
        @SuppressWarnings("deprecation")
        private void stopThread() {
            if (machineMoveThread != null) {
                machineMoveThread.stop();
            }
            machineMoveThread = null;
        }

    }


    /**
     * Creates the chessboard and its indices. It is a grid-layout containing
     * a panel for each slot on the chessboard. The outer perimeter contains
     * labels  with the slots index row or column.
     *
     * @param FRAME_SIZE The constant for the size of the window frame.
     */
    public ChessBoardPanel(int FRAME_SIZE) {

        this.gameBoard = new ChessBoard(DEFAULT_DIFFICULTY, DEFAULT_HUMANCOLOR);

        // This wrapper employs the flow layout, so that the size of the
        // chessboard within can be adapted when the frame is being resized.
        JPanel chessBoardPanelFlowWrapper = new JPanel(new FlowLayout());

        // This wrapper should fill out the entire frame but leave some space
        // for the control panels.
        chessBoardPanelFlowWrapper.setPreferredSize
                (new Dimension(FRAME_SIZE, (int) (FRAME_SIZE * 0.95)));

        // Fill the chessboard grid with panels. These panels contain the
        // functionality of the moves and take care of the graphics aspect.
        //TODO auslagern?
        for (int row = Board.SIZE + 1; row >= 0; row--) {
            for (int col = 0; col <= Board.SIZE + 1; col++) {

                if (!(row > 0 && row <= Board.SIZE && col > 0
                        && col <= Board.SIZE)) {

                    // If this tile is not within the bounds of the actual,
                    // playable chessboard.
                    if (((col == 0 && row == 0)
                            || (col == Board.SIZE + 1 && row == 0)
                            || (row == Board.SIZE + 1 && col == 0)
                            || (col == Board.SIZE + 1 && row == Board.SIZE + 1))) {

                        // Add empty Label into corners.
                        this.add(new JLabel());
                    } else if (col == 0 || col == Board.SIZE + 1) {
                        addIndex(false, col, row,
                                chessBoardPanelFlowWrapper);
                    } else if ((row == 0 || row == Board.SIZE + 1)) {
                        addIndex(true, col, row,
                                chessBoardPanelFlowWrapper);
                    }
                } else {
                    ChessSlotPanel chessSlot = new ChessSlotPanel(gameBoard,
                            col, row);
                    slots[row][col] = chessSlot;
                    this.add(chessSlot);
                }
            }
        }
        chessBoardPanelFlowWrapper.add(this);
        this.add(chessBoardPanelFlowWrapper, BorderLayout.CENTER);

        // In order to have the chessboard initially in a proper size set the
        // boards preferred size to be a square within the frame.
        int boardSize = (int) chessBoardPanelFlowWrapper.getPreferredSize()
                .getHeight();
        this.setPreferredSize(new Dimension(boardSize, boardSize));

        reactToResize(chessBoardPanelFlowWrapper);
    }

    /**
     * Ensure that the chessboard always is a perfect square within the
     * frame. Even when the frame is not.
     *
     * @param chessBoardPanelFlowWrapper The lowest-level wrapper around the
     *                                   {@code chessBoardPanel}.
     */
    private void reactToResize(JPanel chessBoardPanelFlowWrapper) {
        assert chessBoardPanelFlowWrapper != null;

        this.addComponentListener(new ComponentListener() {

            /**
             * Resize the chessboard to a square when the frame is being
             * resized in order to make sure, that it is always squared and
             * never cut off.
             *
             * @param e The event which was called.
             */
            @Override
            public void componentResized(ComponentEvent e) {
                int min = Math.min(chessBoardPanelFlowWrapper.getHeight(),
                        chessBoardPanelFlowWrapper.getWidth());
                ((ChessBoardPanel) e.getSource())
                        .setPreferredSize(new Dimension(min - 1,
                                min - 1));
            }

            /**
             * This method is needed for the interface but not implemented.
             *
             * @param e The event which was called.
             */
            @Override
            public void componentMoved(ComponentEvent e) {
            }

            /**
             * This method is needed for the interface but not implemented.
             *
             * @param e The event which was called.
             */
            @Override
            public void componentShown(ComponentEvent e) {
            }

            /**
             * This method is needed for the interface but not implemented.
             *
             * @param e The event which was called.
             */
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }


    /**
     * Adds an index to the chessboards edge for an easier overview on the
     * chessboard.
     *
     * @param horizontal                 {@code true} if an index is to be added
     *                                   to the top or bottom of the board.
     *                                   {@code false} if an index is to be
     *                                   added to the sides of the board.
     * @param col                        The column position of the index.
     * @param row                        The row position of the index.
     * @param chessBoardPanelFlowWrapper The wrapper around the chessboard
     *                                   panel.
     */
    private void addIndex(boolean horizontal, int col, int row,
                          JPanel chessBoardPanelFlowWrapper) {
        assert chessBoardPanelFlowWrapper != null;
        assert col == 0 || col == Board.SIZE + 1
                || row == 0 || row == Board.SIZE + 1;

        //TODO rename
        int w = (int) (chessBoardPanelFlowWrapper.getPreferredSize().getWidth())
                / (Board.SIZE + 2);

        JLabel index = new JLabel();

        if (horizontal) {

            // Add indices to top and bottom.
            index.setText(String.valueOf(col));

            // Fit the index with some distance to the chessboards
            // edge.
            index.setBorder(new EmptyBorder(0, w / 2
                    - index.getPreferredSize().width, 0,
                    0));
        } else {

            // Add indices to sides.
            index.setText(String.valueOf(row));

            // Fit the index with some distance to the chessboards
            // side.
            index.setBorder(new EmptyBorder(0, w / 2
                    - index.getPreferredSize().width, 0, w / 2
                    - index.getPreferredSize().width));
        }
        this.add(index);
    }

    /**
     * Attempts to make a move or graphically selects the selected slot,
     * depending on the behaviour of the player. If the given slot is illegal
     * the user will be informed by a beep. If it can be ignored it will
     * assuming a mis-click by the user.
     *
     * @param newestMoveParam The slot on the chessboard that was selected by
     *                        the player.
     */
    public void attemptMove(ChessSlotPanel newestMoveParam) {
        if (newestMoveParam == null) {
            throw new IllegalArgumentException("No move can be attempted with"
                    + " this slot.");
        } else {
            if (gameBoard.isGameOver()) {
                announceWinner();
            } else {

                // If the human selects a friendly pawn highlight it and remember
                // it internally.
                if (moveParams.isEmpty()) {
                    if (gameBoard.getSlot(newestMoveParam.getCol(),
                            newestMoveParam.getRow())
                            == gameBoard.getHumanColor()) {

                        moveParams.add(newestMoveParam);
                        newestMoveParam.setSelectedPawn(true);
                        newestMoveParam.repaint();
                    }
                } else if (moveParams.size() == 1) {

                    // See if the player wants to change the pawn he has selected.
                    if (gameBoard.getSlot(newestMoveParam.getCol(),
                            newestMoveParam.getRow())
                            == gameBoard.getHumanColor()) {

                        moveParams.get(0).setSelectedPawn(false);
                        moveParams.get(0).repaint();
                        moveParams.clear();

                        moveParams.add(newestMoveParam);
                        newestMoveParam.setSelectedPawn(true);
                        newestMoveParam.repaint();
                    } else {

                        // Determine the destination and source of the move.
                        moveParams.add(newestMoveParam);
                        ChessSlotPanel source = moveParams.get(0);
                        ChessSlotPanel destination = moveParams.get(1);

                        Board newBoard = null;
                        try {

                            // Let the model attempt to perform a move.
                            newBoard = gameBoard.move(source.getCol(),
                                    source.getRow(), destination.getCol(),
                                    destination.getRow());
                        } catch (IllegalMoveException illegalMoveException) {

                            // Notify the user if his move was illegal.
                            Toolkit.getDefaultToolkit().beep();
                        } finally {
                            if (newBoard == null) {

                                // Notify the user if his move was illegal.
                                moveParams.remove(1);
                                Toolkit.getDefaultToolkit().beep();
                            } else {
                                GUI frame = ((GUI) this.getTopLevelAncestor());

                                // Remember the last move.
                                frame.pushOnUndoStack(gameBoard.clone());
                                gameBoard = newBoard;
                                if (gameBoard.isGameOver()) {
                                    announceWinner();
                                } else {

                                    // Update and repaint if the move was
                                    // successful.
                                    frame.updateAndPaintAmountOfPawns();
                                    moveParams.get(0).setSelectedPawn(false);
                                    moveParams.clear();
                                    //TODO hier invokeLater?
                                    updateSlots();

                                    // Let the machine perform its move.
                                    machineMoveThread = new MachineMoveThread();
                                    machineMoveThread.start();
                                }
                            }
                        }
                    }
                } else {
                    throw new IllegalStateException("The move params are in an "
                            + "illegal state!");
                }
            }
        }
    }

    /**
     * Displays the winner to the user. Restricts all further movements.
     */
    private void announceWinner() {
        assert (gameBoard.isGameOver());

        GUI frame = ((GUI) this.getTopLevelAncestor());

        // Update and repaint the board.
        frame.updateAndPaintAmountOfPawns();
        updateSlots();
        SwingUtilities.invokeLater(this::updateSlots);

        if (gameBoard.getWinner() == Player.HUMAN) {
            JOptionPane.showMessageDialog(null,
                    "You won!");
        } else if (gameBoard.getWinner() == Player.MACHINE) {
            JOptionPane.showMessageDialog(null,
                    "You lost.");
        } else {
            JOptionPane.showMessageDialog(null,
                    "It's a draw.");
        }

        // Re-enable the buttons.
        setEnabledOnChessBoardPanels(true);

        // Kill the machine move.
        if (machineMoveThread.isAlive()) {
            machineMoveThread.stopThread();
        }
    }

    /**
     * Dis- or Enable the humans possibility to move on the board.
     *
     * @param enabled Enables the humans possibility to move on the board if
     *                {@code true}. Disables it if {@code false}.
     */
    private void setEnabledOnChessBoardPanels(boolean enabled) {
        for (int row = 0; row <= Board.SIZE; row++) {
            for (int col = 0; col <= Board.SIZE; col++) {
                slots[row][col].setSlotButtonEnabled(enabled);
            }
        }
    }


    /**
     * Updates the slots about a movement of pawns and repaints them if
     * necessary (if a change was detected). Also unselect slots that have
     * been selected.
     */
    public void updateSlots() {
        for (int row = 0; row <= Board.SIZE; row++) {
            for (int col = 0; col <= Board.SIZE; col++) {
                if (gameBoard.getSlot(col, row)
                        != slots[row][col].getPawnColor()) {
                    slots[row][col].repaint();
                } else if (slots[row][col].isSelectedPawn()) {
                    slots[row][col].setSelectedPawn(false);
                    slots[row][col].repaint();
                }
            }
        }
    }

    public void setDefaultDifficulty(int difficulty) {
        DEFAULT_DIFFICULTY = difficulty;
    }

    public void setDefaultHumanColor(model.chessboard.Color defaultHumanColor) {
        DEFAULT_HUMANCOLOR = defaultHumanColor;
    }

}
