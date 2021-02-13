package view;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.player.Player;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JButton;
import javax.swing.JComboBox;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


import java.util.Objects;
import java.util.Stack;

/**
 * Constructing an object from this class will provide you with a frame on
 * which you can play a game of pawns chess. Select a pawn -it will be
 * highlighted in blue- and move it to a legal slot. The computer will move
 * automatically.
 */
public class GUI extends JFrame {

    /**
     * This value is used to determine the rough size of the frame.
     */
    private final static int FRAME_SIZE = 800;

    /**
     * This is the default difficulty level of the machine opponent.
     */
    private static int defaultDifficulty = 3;

    /**
     * This is the default color of the human player.
     */
    private static Color defaultHumanColor = Color.WHITE;

    /**
     * This is the Board on which the current game is being played.
     */
    private Board gameBoard
            = new ChessBoard(defaultDifficulty, defaultHumanColor);

    /**
     * This is the Panel on which the chessboard is being displayed.
     */
    private ChessBoardPanel chessBoardPanel;

    private final ControlPanel controlPanel;


    /**
     * The constructor for the window frame. From here all the inner
     * components are called to be constructed.
     */
    public GUI() {
        super("Pawns Chess");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Initiate the inner components of the frame.
        this.controlPanel = new ControlPanel();
        initChessBoardPanel();

        // Adapt the initial frame size in order to have a clean starting
        // window without cutoffs or gaps.
        this.setSize((int) (FRAME_SIZE * 0.95), (int) (FRAME_SIZE * 1.05));
        this.setMinimumSize(new Dimension((int) (this.getSize().width * 0.6),
                (int) (this.getSize().height * 0.6)));
        this.setVisible(true);

        // Make sure the {@code machineMoveThread} is killed after the window
        // was closed.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                chessBoardPanel.haltMachineMoveThread();
                super.windowClosed(e);
            }
        });
    }

    /**
     * Initiate the chessboard on which the actual game is displayed an
     * played from.
     */
    private void initChessBoardPanel() {

        // This wrapper employs the flow-layout, so that the size of the
        // chessboard within can be adapted when the frame is being resized.
        JPanel chessBoardPanelFlowWrapper = new JPanel(new FlowLayout());

        // This wrapper should fill out the entire frame but leave some space
        // for the control panels.
        chessBoardPanelFlowWrapper.setPreferredSize(new Dimension(FRAME_SIZE,
                (int) (FRAME_SIZE * 0.95)));
        this.chessBoardPanel
                = new ChessBoardPanel(gameBoard, chessBoardPanelFlowWrapper);
        chessBoardPanelFlowWrapper.add(chessBoardPanel);
        this.add(chessBoardPanelFlowWrapper, BorderLayout.CENTER);
        chessBoardPanel.updateSlots();
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

                chessBoardPanel.setPreferredSize(new Dimension(min - 1,
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
     * An inner class to represent the panel in the south of the window which
     * contains all necessary elements and buttons to control the games
     * functions.
     */
    private class ControlPanel extends JPanel {

        /**
         * On this stack the chessboards are stored in chronological order for
         * the purpose of being able to 'undo' a move.
         */
        private final Stack<Board> undoStack = new Stack<>();

        /**
         * These labels display the amount of pawns a player possesses.
         */
        private final JLabel whitePawnsNumber
                = new JLabel(String.valueOf(Board.SIZE));
        private final JLabel blackPawnsNumber
                = new JLabel(String.valueOf(Board.SIZE));

        /**
         * Initializes the control panel and the buttons contained within it.
         */
        ControlPanel() {

            /*
            Create a wrapper around the control panel in order to place the
            pawns numbers easily and neat. Assign a size to ensure that it does
            not take up too much space.
            */
            JPanel controlPanelWrapper = new JPanel(new BorderLayout());
            controlPanelWrapper.setPreferredSize(new Dimension(FRAME_SIZE,
                    (int) (FRAME_SIZE * 0.05)));
            GUI.this.add(controlPanelWrapper, BorderLayout.SOUTH);
            initPawnNumbers(controlPanelWrapper);
            this.setLayout(new FlowLayout());
            controlPanelWrapper.add(this, BorderLayout.CENTER);

            initLevelsBox(this);
            initUndoButton(this);
            initNewButton(this);
            initSwitchButton(this);
            initQuitButton(this);
        }

        /**
         * Add the undo button in order provide the ability to undo the last
         * move.
         *
         * @param controlPanel The panel containing the control elements.
         */
        private void initUndoButton(JPanel controlPanel) {
            assert controlPanel != null;

            JButton undoButton = new JButton("Undo");
            undoButton.setMnemonic('U');
            undoButton.addActionListener(e -> {
                if (!undoStack.isEmpty()) {

                    // The thread should be stopped as its machine move will
                    // no longer be required.
                    chessBoardPanel.haltMachineMoveThread();

                    // Reset and update the GUI, afterwards paint.
                    gameBoard = undoStack.pop();
                    chessBoardPanel.updateGameBoard(gameBoard);
                    chessBoardPanel.clearMoveParams();
                    chessBoardPanel.updateSlots();
                    updateAndPaintAmountOfPawns();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            });
            undoButton.setToolTipText("Undo your last move.");
            controlPanel.add(undoButton);
        }

        /**
         * Add the levels combobox. Levels are updated immediately, but not
         * while the machine is moving.
         *
         * @param controlPanel The panel containing the control elements.
         */
        private void initLevelsBox(JPanel controlPanel) {
            assert controlPanel != null;

            controlPanel.add(new JLabel("Levels:"));
            String[] levels = {"1", "2", "3", "4"};
            JComboBox<String> levelMenu = new JComboBox<>(levels);
            levelMenu.setSelectedIndex(defaultDifficulty - 1);
            gameBoard.setLevel(Integer.parseInt((String) Objects
                    .requireNonNull(levelMenu.getSelectedItem())));
            levelMenu.addActionListener(e -> {
                JComboBox selectedBox = (JComboBox) e.getSource();
                int selectedLevel
                        = Integer.parseInt((String) Objects
                        .requireNonNull(selectedBox.getSelectedItem()));
                defaultDifficulty = selectedLevel;
                gameBoard.setLevel(selectedLevel);
            });
            levelMenu.setToolTipText("Set the machine opponents difficulty "
                    + "level.");
            controlPanel.add(levelMenu);
        }

        /**
         * Add a button to allow the creation of a new game.
         *
         * @param controlPanel The panel containing the control elements.
         */
        private void initNewButton(JPanel controlPanel) {
            assert controlPanel != null;

            JButton newButton = new JButton("New");
            newButton.setMnemonic('N');
            newButton.addActionListener(e -> constructNewBoard());
            newButton.setToolTipText("Create a new game.");
            controlPanel.add(newButton);
        }

        /**
         * Add a button for a clean quit to the program.
         *
         * @param controlPanel The panel containing the control elements.
         */
        private void initQuitButton(JPanel controlPanel) {
            assert controlPanel != null;

            JButton quitButton = new JButton("Quit");
            quitButton.setMnemonic('Q');
            quitButton.addActionListener(e -> {
                JButton source = (JButton) e.getSource();
                JFrame frame = (JFrame) source.getTopLevelAncestor();
                frame.dispose();
            });
            quitButton.setToolTipText("Quit this program and close the "
                    + "window.");
            controlPanel.add(quitButton);
        }

        /**
         * Add a button to allow the creation of a new game with switched
         * colors.
         *
         * @param controlPanel The panel containing the control elements.
         */
        private void initSwitchButton(JPanel controlPanel) {
            assert controlPanel != null;

            JButton switchButton = new JButton("Switch");
            switchButton.setMnemonic('S');
            switchButton.addActionListener(e -> {
                defaultHumanColor = Color.getOppositeColor(defaultHumanColor);
                constructNewBoard();
            });
            switchButton.setToolTipText("Start a new game with switched "
                    + "colours.");
            controlPanel.add(switchButton);
        }

        /**
         * Initiate the pawns numbers in the bottom left and right corners.
         * They indicate how many pawns each player has remaining.
         *
         * @param controlPanelWrapper A wrapper around the {@code controlPanel}.
         */
        private void initPawnNumbers(JPanel controlPanelWrapper) {
            assert controlPanelWrapper != null;

            // Place the human pawns number.
            whitePawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
            whitePawnsNumber.setBorder(new EmptyBorder(0, 10, 0,
                    10));

            // Place the machine pawns number with a black background.
            JPanel machinePawnsNumberPanel = new JPanel();
            machinePawnsNumberPanel.setBackground(java.awt.Color.BLACK);
            blackPawnsNumber.setForeground(java.awt.Color.WHITE);
            blackPawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
            machinePawnsNumberPanel.add(blackPawnsNumber);
            machinePawnsNumberPanel.setBorder(new EmptyBorder(0, 10,
                    0, 10));

            controlPanelWrapper.add(whitePawnsNumber, BorderLayout.WEST);
            controlPanelWrapper.add(machinePawnsNumberPanel, BorderLayout.EAST);
            whitePawnsNumber.setToolTipText("The amount of white pawns on the "
                    + "board.");
            blackPawnsNumber.setToolTipText("The amount of black pawns on the "
                    + "board.");
        }

        /**
         * Starts a new game. Resets the classes attributes, then repaints.
         */
        private void constructNewBoard() {

            // Kill the current thread as its results will not be needed.
            chessBoardPanel.haltMachineMoveThread();

            // Reset the classes attributes.
            chessBoardPanel.clearMoveParams();
            undoStack.clear();
            chessBoardPanel.setEnabledOnChessBoardPanels(true);
            whitePawnsNumber.setText(String.valueOf(Board.SIZE));
            blackPawnsNumber.setText(String.valueOf(Board.SIZE));
            gameBoard = new ChessBoard(defaultDifficulty, defaultHumanColor);
            chessBoardPanel.updateGameBoard(gameBoard);
            chessBoardPanel.updateSlots();

            // If the machine can start, make a move.
            if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
                chessBoardPanel.makeMachineMove();
            }
        }

        /**
         * Assigns a text to the number in the left bottom corner which
         * represents the amount of white pawns. The label will also be
         * repainted by the event queue.
         *
         * @param text The text which is set as the labels text. This should
         *             be the string of an integer number.
         */
        private void setWhitePawnsNumberText(String text) {
            whitePawnsNumber.setText(text);
            SwingUtilities.invokeLater(whitePawnsNumber::repaint);
        }

        /**
         * Assigns a text to the number in the right bottom corner which
         * represents the amount of black pawns. The label will also be
         * repainted by the event queue.
         *
         * @param text The text which is set as the labels text. This should
         *             be the string of an integer number.
         */
        private void setBlackPawnsNumberText(String text) {
            blackPawnsNumber.setText(text);
            SwingUtilities.invokeLater(blackPawnsNumber::repaint);
        }
    }

    /**
     * Update and repaint the amount of pawns in the control panel.
     */
    void updateAndPaintAmountOfPawns() {
        if (gameBoard == null) {
            throw new IllegalArgumentException("The board must represent a "
                    + "legal game state.");
        } else {
            if (defaultHumanColor == Color.WHITE) {
                controlPanel.setWhitePawnsNumberText(String.valueOf(gameBoard
                        .getNumberOfTiles(Player.HUMAN)));
                controlPanel.setBlackPawnsNumberText(String.valueOf(gameBoard
                        .getNumberOfTiles(Player.MACHINE)));
            } else {
                controlPanel.setWhitePawnsNumberText(String.valueOf(gameBoard
                        .getNumberOfTiles(Player.MACHINE)));
                controlPanel.setBlackPawnsNumberText(String.valueOf(gameBoard
                        .getNumberOfTiles(Player.HUMAN)));
            }
        }
    }

    /**
     * Updates the model of the current game-state.
     *
     * @param gameBoard The current game-board.
     */
    public void updateGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Calls on the event handler to create a new {@code GUI}. Therefore
     * opening the frame and starting a new game.
     *
     * @param args String array of arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }

    /**
     * Pushes a board on the undo-stack.
     *
     * @param board The board to be pushed on the undo-stack.
     */
    public void pushOnUndoStack(Board board) {
        if (gameBoard == null) {
            throw new IllegalArgumentException("The board must represent a "
                    + "legal game state.");
        } else {
            controlPanel.undoStack.push(board);
        }
    }
}