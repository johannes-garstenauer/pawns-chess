package view;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.exceptions.IllegalMoveException;
import model.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Constructing an object from this class will provide you with a frame on
 * which you can play a game of pawns chess. Select a pawn -it will be
 * highlighted in blue- and move it to a legal slot. The computer will move
 * automatically.
 */
public class GUI extends JFrame {

    // This value is used to determine the rough size of the frame.
    private final static int FRAME_SIZE = 800;

    // This is the default difficulty level of the machine opponent.
    private static int DEFAULT_DIFFICULTY = 3;

    // This is the default color of the human player.
    private static Color DEFAULT_HUMANCOLOR = Color.WHITE;

    // This is the Board on which the current game is being played.
    private Board gameBoard
            = new ChessBoard(DEFAULT_DIFFICULTY, DEFAULT_HUMANCOLOR);

    // This is the Panel on which the chessboard is being displayed.
    private final JPanel chessBoardPanel = new JPanel(new GridLayout(Board.SIZE,
            Board.SIZE));

    // On this stack the chessboards are stored in chronological order for the
    // purpose of being able to 'undo' a move.
    private final Stack<Board> undoStack = new Stack<>();

    // These labels display the amount of pawns a player possesses.
    private final JLabel humanPawnsNumber
            = new JLabel(String.valueOf(Board.SIZE));
    private final JLabel machinePawnsNumber
            = new JLabel(String.valueOf(Board.SIZE));

    // Collects all the panels contained within the chessboard.
    private final List<ChessSlotPanel> chessSlotPanels = new ArrayList<>();

    // This contains the selected panels that are used to calculate a human
    // move. It should never contain more than two elements.
    private final List<ChessSlotPanel> moveParams = new ArrayList<>();

    // On this thread the machine move is calculated.
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
            for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
                chessSlotPanel.getSlotButton().setEnabled(false);
            }

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

                    // Update the components of the changes made by the move.
                    updateAndPaintAmountOfPawns();
                    updateGameBoard();

                    // Let the event queue handle the repaint.
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            chessBoardPanel.repaint();
                        }
                    });

                    // Move again if the human cannot move.
                    if (gameBoard.getNextPlayer() != Player.HUMAN) {
                        //System.err.println("Cannot Move!");
                        //this.start();
                        //TODO: executeMachineMoved didnt work
                        //TODO rekursion klappt hier nicht :/
                    }

                    // Enable the humans possibility to move again.
                    for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
                        chessSlotPanel.getSlotButton().setEnabled(true);
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
        this.setVisible(true);

        // Make sure the {@code machineMoveThread} is killed after the window
        // was closed.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                machineMoveThread.stopThread();
                super.windowClosed(e);
            }
        });
    }

    /**
     * Initializes the chessboard and the indices.
     */
    private void initChessBoardPanel() {

        // Have a wrapper around the chessboard to place the indices around.
        JPanel chessBoardPanelWithIndicesWrapper = new JPanel();
        chessBoardPanelWithIndicesWrapper.setLayout(new BorderLayout());

        // This wrapper should fill out the entire frame but leave some space
        // for the control panels.
        chessBoardPanelWithIndicesWrapper.setPreferredSize(new Dimension
                (FRAME_SIZE, (int) (FRAME_SIZE * 0.95)));

        // This wrapper employs the flow layout, so that the size of the
        // chessboard within can be adapted when the frame is being resized.
        JPanel chessBoardPanelFlowWrapper = new JPanel(new FlowLayout());

        // Fill the chessboard grid with panels. These panels contain the
        // functionality of the moves and take care of the graphics aspect.
        for (int row = 1; row <= Board.SIZE; row++) {
            for (int col = 1; col <= Board.SIZE; col++) {
                ChessSlotPanel chessSlot = new ChessSlotPanel(gameBoard,
                        col, Board.SIZE + 1 - row);
                chessSlotPanels.add(chessSlot);
                chessBoardPanel.add(chessSlot);
            }
        }
        chessBoardPanelFlowWrapper.add(chessBoardPanel);
        chessBoardPanelWithIndicesWrapper.add(chessBoardPanelFlowWrapper,
                BorderLayout.CENTER);
        this.add(chessBoardPanelWithIndicesWrapper, BorderLayout.CENTER);

        // Create panels that contain the indices around the board.
        JPanel verticalIndicesWest
                = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel verticalIndicesEast
                = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel horizontalIndicesNorth
                = new JPanel(new GridLayout(1, Board.SIZE));
        JPanel horizontalIndicesSouth
                = new JPanel(new GridLayout(1, Board.SIZE));

        for (int i = Board.SIZE; i >= 1; i--) {
            verticalIndicesWest.add(new JLabel(String.valueOf(i)),
                    SwingConstants.CENTER);
            verticalIndicesEast.add(new JLabel(String.valueOf(i)),
                    SwingConstants.CENTER);
            horizontalIndicesSouth.add(
                    new JLabel(String.valueOf(Board.SIZE - i + 1)));
            horizontalIndicesNorth.add(
                    new JLabel(String.valueOf(Board.SIZE - i + 1)));
        }

        // The distance needed to properly align the top and bottom indices.
        int sideDist = (int) (chessBoardPanelWithIndicesWrapper
                .getPreferredSize().getWidth() / Board.SIZE);
        horizontalIndicesNorth.setBorder(new EmptyBorder(0, sideDist,
                0, sideDist));
        horizontalIndicesSouth.setBorder(new EmptyBorder(0, sideDist,
                0, sideDist));

        chessBoardPanelWithIndicesWrapper.add(horizontalIndicesNorth,
                BorderLayout.NORTH);
        chessBoardPanelWithIndicesWrapper.add(horizontalIndicesSouth,
                BorderLayout.SOUTH);
        chessBoardPanelWithIndicesWrapper.add(verticalIndicesWest,
                BorderLayout.WEST);
        chessBoardPanelWithIndicesWrapper.add(verticalIndicesEast,
                BorderLayout.EAST);

        // In order to have the chessboard initially in a proper size set the
        // boards preferred size to be a square within the frame.
        int boardSize = (int) chessBoardPanelWithIndicesWrapper
                .getPreferredSize().getHeight() - (int) (horizontalIndicesNorth
                .getPreferredSize().getHeight() * 2);
        chessBoardPanel.setPreferredSize(new Dimension(boardSize, boardSize));

        // Ensure that the chessboard always is a perfect square within the
        // frame. Even when the frame is not.
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

        humanPawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
        humanPawnsNumber.setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel machinePawnsNumberPanel = new JPanel();
        machinePawnsNumberPanel.setBackground(java.awt.Color.BLACK);
        machinePawnsNumber.setBackground(java.awt.Color.WHITE);
        machinePawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
        machinePawnsNumberPanel.add(machinePawnsNumber);
        machinePawnsNumberPanel.setBorder(new EmptyBorder(0, 10, 0, 10));


        // This is the panel that contains the buttons.
        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanelWrapper.add(controlPanel, BorderLayout.CENTER);
        controlPanelWrapper.add(humanPawnsNumber,
                BorderLayout.WEST);
        controlPanelWrapper.add(machinePawnsNumberPanel,
                BorderLayout.EAST);


        controlPanel.add(new JLabel("Levels:"));
        String[] levels = {"1", "2", "3", "4"};
        JComboBox<String> levelMenu = new JComboBox<>(levels);
        levelMenu.setSelectedIndex(DEFAULT_DIFFICULTY - 1);
        gameBoard.setLevel(Integer.parseInt((String) levelMenu.getSelectedItem()));
        levelMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> selectedBox =
                        (JComboBox<String>) e.getSource();
                int selectedLevel =
                        Integer.parseInt((String) selectedBox.getSelectedItem());

                DEFAULT_DIFFICULTY = selectedLevel;
                gameBoard.setLevel(selectedLevel);
            }
        });
        controlPanel.add(levelMenu);

        JButton undoButton = new JButton("Undo");
        undoButton.setMnemonic('U');
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!undoStack.isEmpty()) {
                    machineMoveThread.stopThread();
                    machineMoveThread = new MachineMoveThread();
                    gameBoard = undoStack.pop();
                    updateGameBoard();
                    updateAndPaintAmountOfPawns();
                    moveParams.clear();
                    resetSelectedChessSlotPanels();
                    chessBoardPanel.repaint();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }

            }
        });
        controlPanel.add(undoButton);

        JButton newButton = new JButton("New");
        newButton.setMnemonic('N');
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                constructNewBoard();
            }
        });
        controlPanel.add(newButton);

        JButton switchButton = new JButton("Switch");
        switchButton.setMnemonic('S');
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DEFAULT_HUMANCOLOR = Color.getOppositeColor(DEFAULT_HUMANCOLOR);
                constructNewBoard();
            }
        });
        controlPanel.add(switchButton);

        JButton quitButton = new JButton("Quit");
        quitButton.setMnemonic('Q');
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                JFrame frame = (JFrame) source.getTopLevelAncestor();

                //TODO doppelt gemoppelt weil schon defaultcloseoperation?
                frame.dispose();
            }
        });
        controlPanel.add(quitButton);

        this.add(controlPanelWrapper, BorderLayout.SOUTH);
    }

    private void updateAndPaintAmountOfPawns() {
        humanPawnsNumber.setText
                (String.valueOf(gameBoard.getNumberOfTiles(Player.HUMAN)));
        machinePawnsNumber.setText
                (String.valueOf(gameBoard.getNumberOfTiles(Player.MACHINE)));
        humanPawnsNumber.repaint();
        machinePawnsNumber.repaint();
    }

    private void constructNewBoard() {
        machineMoveThread.stopThread();
        machineMoveThread = new MachineMoveThread();
        resetSelectedChessSlotPanels();
        moveParams.clear();
        undoStack.clear();
        humanPawnsNumber.setText(String.valueOf(Board.SIZE));
        machinePawnsNumber.setText(String.valueOf(Board.SIZE));

        gameBoard = new ChessBoard(DEFAULT_DIFFICULTY, DEFAULT_HUMANCOLOR);

        if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
            //executeMachineMove();
            machineMoveThread = new MachineMoveThread();
            machineMoveThread.start();
        } else {
            updateGameBoard();
            chessBoardPanel.repaint();
        }
    }

    public static void main(String[] args) {
        //TODO why dat
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI();
            }
        });
    }

    private void addMoveParam(ChessSlotPanel moveParam) {
        moveParams.add(moveParam);
    }

    //TODO aussetzen
    public void attemptMove(ChessSlotPanel newestMoveParam) {
        if (gameBoard.isGameOver()) {
            updateGameBoard();
            chessBoardPanel.repaint();
            announceWinner();
        } else {
            if (moveParams.isEmpty()) {
                if (gameBoard.getSlot(newestMoveParam.getCol(),
                        newestMoveParam.getRow()) == gameBoard.getHumanColor()) {
                    addMoveParam(newestMoveParam);

                    newestMoveParam.setSelectedPawn(true);
                    newestMoveParam.repaint();
                }
            } else if (moveParams.size() == 1) {

                // Reselect the pawn to be moved.
                if (gameBoard.getSlot(newestMoveParam.getCol(),
                        newestMoveParam.getRow()) == gameBoard.getHumanColor()) {
                    moveParams.get(0).setSelectedPawn(false);
                    moveParams.get(0).repaint();
                    moveParams.clear();

                    addMoveParam(newestMoveParam);
                    newestMoveParam.setSelectedPawn(true);
                    newestMoveParam.repaint();
                } else {

                    // Chose a slot to move the selected pawn to.
                    addMoveParam(newestMoveParam);

                    //attempt move
                    ChessSlotPanel source = moveParams.get(0);
                    ChessSlotPanel destination = moveParams.get(1);

                    Board newBoard = null;
                    try {
                        newBoard = gameBoard.move(source.getCol(), source.getRow(),
                                destination.getCol(), destination.getRow());
                    } catch (IllegalMoveException illegalMoveException) {
                        Toolkit.getDefaultToolkit().beep();
                    } finally {
                        if (newBoard == null) {
                            moveParams.remove(1);
                            Toolkit.getDefaultToolkit().beep();
                        } else {
                            undoStack.push(gameBoard.clone());
                            gameBoard = newBoard;
                            if (gameBoard.isGameOver()) {
                                announceWinner();
                            } else {

                                // It was a successful move.
                                updateAndPaintAmountOfPawns();
                                updateGameBoard();
                                moveParams.get(0).setSelectedPawn(false);
                                moveParams.clear();
                                chessBoardPanel.repaint();
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

    private void announceWinner() {
        updateAndPaintAmountOfPawns();
        updateGameBoard();
        resetSelectedChessSlotPanels();
        chessBoardPanel.repaint();

        if (gameBoard.getWinner() == Player.HUMAN) {
            JOptionPane.showMessageDialog(null, "You won!");
        } else if (gameBoard.getWinner() == Player.MACHINE) {
            JOptionPane.showMessageDialog(null, "You lost.");
        } else {
            JOptionPane.showMessageDialog(null, "It's a draw.");
        }
        Toolkit.getDefaultToolkit().beep();
    }

    private void updateGameBoard() {
        for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
            chessSlotPanel.setGameBoard(gameBoard);
        }
    }

    private void resetSelectedChessSlotPanels() {
        for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
            chessSlotPanel.setSelectedPawn(false);
        }
    }
}
