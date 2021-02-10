package view;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
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
    private static int DEFAULT_DIFFICULTY = 3;

    /**
     * This is the default color of the human player.
     */
    private static Color DEFAULT_HUMANCOLOR = Color.WHITE;

    /**
     * This is the Board on which the current game is being played.
     */
    private Board gameBoard
            = new ChessBoard(DEFAULT_DIFFICULTY, DEFAULT_HUMANCOLOR);

    /**
     * This is the Panel on which the chessboard is being displayed.
     */
    private ChessBoardPanel chessBoardPanel;

    /**
     * On this stack the chessboards are stored in chronological order for the
     * purpose of being able to 'undo' a move.
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
     * The constructor for the window frame. From here all the inner
     * components are called to be constructed.
     */
    public GUI() {
        super("Pawns Chess");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Initiate the inner components of the frame.
        initControlPanel();
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

    private void initChessBoardPanel() {

        // This wrapper employs the flow layout, so that the size of the
        // chessboard within can be adapted when the frame is being resized.
        JPanel chessBoardPanelFlowWrapper = new JPanel(new FlowLayout());

        // This wrapper should fill out the entire frame but leave some space
        // for the control panels.
        chessBoardPanelFlowWrapper.setPreferredSize
                (new Dimension(FRAME_SIZE, (int) (FRAME_SIZE * 0.95)));

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
     * Initializes the control panel and the buttons contained within it.
     */
    private void initControlPanel() {

        /*
        Create a wrapper around the control panel in order to place the
        pawns numbers easily and neat. Assign a size to ensure that it does not
        take up too much space.
         */
        JPanel controlPanelWrapper = new JPanel(new BorderLayout());
        controlPanelWrapper.setPreferredSize(new Dimension(FRAME_SIZE,
                (int) (FRAME_SIZE * 0.05)));
        this.add(controlPanelWrapper, BorderLayout.SOUTH);

        initPawnNumbers(controlPanelWrapper);

        // This is the panel that contains the buttons.
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanelWrapper.add(controlPanel, BorderLayout.CENTER);

        initLevelsBox(controlPanel);
        initUndoButton(controlPanel);
        initNewButton(controlPanel);
        initSwitchButton(controlPanel);
        initQuitButton(controlPanel);
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
                //TODO invokeLater?
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
        levelMenu.setSelectedIndex(DEFAULT_DIFFICULTY - 1);
        gameBoard.setLevel(Integer.parseInt
                ((String) Objects.requireNonNull(levelMenu.getSelectedItem())));
        levelMenu.addActionListener(e -> {
            JComboBox<String> selectedBox
                    = (JComboBox<String>) e.getSource();
            int selectedLevel
                    = Integer.parseInt((String) Objects.requireNonNull
                    (selectedBox.getSelectedItem()));
            DEFAULT_DIFFICULTY = selectedLevel;
            gameBoard.setLevel(selectedLevel);
        });
        levelMenu.setToolTipText("Set the machine opponents difficulty level.");
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
        quitButton.setToolTipText("Quit this program and close the window.");
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
            DEFAULT_HUMANCOLOR = Color.getOppositeColor(DEFAULT_HUMANCOLOR);
            constructNewBoard();
        });
        switchButton.setToolTipText("Start a new game with switched colours.");
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
     * Update and repaint the amount of pawns in the control panel.
     */
    void updateAndPaintAmountOfPawns() {

        if (DEFAULT_HUMANCOLOR == Color.WHITE) {
            whitePawnsNumber.setText
                    (String.valueOf(gameBoard.getNumberOfTiles(Player.HUMAN)));
            blackPawnsNumber.setText
                    (String.valueOf(gameBoard.getNumberOfTiles(Player.MACHINE)));
        } else {
            whitePawnsNumber.setText
                    (String.valueOf(gameBoard.getNumberOfTiles(Player.MACHINE)));
            blackPawnsNumber.setText
                    (String.valueOf(gameBoard.getNumberOfTiles(Player.HUMAN)));
        }
        SwingUtilities.invokeLater(whitePawnsNumber::repaint);
        SwingUtilities.invokeLater(blackPawnsNumber::repaint);
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
        //TODO
        //setEnabledOnChessBoardPanels(true);
        whitePawnsNumber.setText(String.valueOf(Board.SIZE));
        blackPawnsNumber.setText(String.valueOf(Board.SIZE));
        gameBoard = new ChessBoard(DEFAULT_DIFFICULTY, DEFAULT_HUMANCOLOR);
        chessBoardPanel.updateGameBoard(gameBoard);

        // If the machine can start, make a move. Else repaint immediately.
        if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
            chessBoardPanel.makeMachineMove();
        } else {
            chessBoardPanel.updateSlots();
        }
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

    public void pushOnUndoStack(Board board) {
        undoStack.push(board);
    }
}
