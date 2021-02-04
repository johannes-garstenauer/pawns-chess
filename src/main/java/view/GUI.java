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


public class GUI extends JFrame {

    private final static int FRAME_SIZE = 800;

    // This is the difficulty level of the machine opponent.
    private static int difficultyLevel = 3;

    // This is the color of the human player.
    private static Color humanColor = Color.WHITE;

    private int amountOfHumanPawns = Board.SIZE;
    private int amountOfMachinePawns = Board.SIZE;

    private Board gameBoard = new ChessBoard(difficultyLevel, humanColor);
    private final JPanel chessBoardPanelWithIndicesWrapper = new JPanel();
    private final JPanel chessBoardPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();

    private final Stack<Board> undoStack = new Stack<>();

    private JLabel humanPawnsNumber
            = new JLabel(String.valueOf(amountOfHumanPawns));
    private JLabel machinePawnsNumber
            = new JLabel(String.valueOf(amountOfMachinePawns));


    private MachineMoveThread machineMoveThread = new MachineMoveThread();

    private class MachineMoveThread extends Thread {

        @Override
        public void run() {

            // Disable the humans possibility to move while the machine has not yet
            // moved.
            //TODO extract method.
            for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
                chessSlotPanel.getSlotButton().setEnabled(false);
            }

            try {
                gameBoard = gameBoard.machineMove();
            } catch (IllegalMoveException illegalMoveException) {
                if (gameBoard.isGameOver()) {
                    announceWinner();
                } else {
                    //TODO aussetzen
                    //-> mensch kann wieder ziehen
                    // falls mensch aussetzen muss
                }
            } finally {
                if (gameBoard.isGameOver()) {
                    announceWinner();
                } else {
                    updateAmountOfPawns();
                    updateGameBoard();
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

        @SuppressWarnings("deprecation")
        private void stopThread() {
            if (machineMoveThread != null) {
                machineMoveThread.stop();
            }
            machineMoveThread = null;
        }

    }


    //TODO ist das ein Bruch des Klassengeheimnis?
    private final List<ChessSlotPanel> chessSlotPanels = new ArrayList<>();


    private final List<ChessSlotPanel> moveParams = new ArrayList<>();


    public GUI() {
        super("Pawns Chess");
        //constructNewBoard();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //TODO das bringt was
        //this.setMinimumSize(new Dimension(700, 700));

        //TODO das bring nix :(
        //this.setMaximumSize(new Dimension(700, 700));

        this.setLayout(new BorderLayout());

        initControlPanel();
        initChessBoardPanel();
        this.setSize(FRAME_SIZE,
                FRAME_SIZE + controlPanel.getPreferredSize().height);
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                machineMoveThread.stopThread();
                super.windowClosed(e);
            }
        });
    }

    private void initChessBoardPanel() {
        chessBoardPanelWithIndicesWrapper.setLayout(new BorderLayout());
        JPanel chessBoardPanelFlowWrapper = new JPanel(new FlowLayout());

        chessBoardPanel.setLayout(new GridLayout(Board.SIZE,
                Board.SIZE));
        for (int row = 1; row <= Board.SIZE; row++) {
            for (int col = 1; col <= Board.SIZE; col++) {
                ChessSlotPanel chessSlot = new ChessSlotPanel(gameBoard,
                        col, Board.SIZE + 1 - row);
                chessSlotPanels.add(chessSlot);
                chessBoardPanel.add(chessSlot);
            }
        }

        chessBoardPanelFlowWrapper.add(chessBoardPanel);
        chessBoardPanelWithIndicesWrapper.add(chessBoardPanelFlowWrapper, BorderLayout.CENTER);
        this.add(chessBoardPanelWithIndicesWrapper, BorderLayout.CENTER);

        JPanel verticalIndicesWest = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel verticalIndicesEast = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel horizontalIndicesNorth = new JPanel(new GridLayout(1, Board.SIZE));
        JPanel horizontalIndicesSouth = new JPanel(new GridLayout(1, Board.SIZE));

        for (int i = Board.SIZE; i >= 1; i--) {
            verticalIndicesWest.add(new JLabel(String.valueOf(i)));
            verticalIndicesEast.add(new JLabel(String.valueOf(i)));
            horizontalIndicesSouth.add(
                    new JLabel(String.valueOf(Board.SIZE + 1 - i)));
            horizontalIndicesNorth.add(
                    new JLabel(String.valueOf(i)));
        }

        int sideDist =
                verticalIndicesWest.getPreferredSize().width;
        horizontalIndicesNorth.setBorder(new EmptyBorder(0, sideDist,
                0, sideDist));
        horizontalIndicesSouth.setBorder(new EmptyBorder(0, sideDist,
                0, sideDist));

        chessBoardPanelWithIndicesWrapper.add(horizontalIndicesNorth, BorderLayout.NORTH);
        chessBoardPanelWithIndicesWrapper.add(horizontalIndicesSouth, BorderLayout.SOUTH);
        chessBoardPanelWithIndicesWrapper.add(verticalIndicesWest, BorderLayout.WEST);
        chessBoardPanelWithIndicesWrapper.add(verticalIndicesEast, BorderLayout.EAST);

        // In order to have the chessboard initially in a proper size set the
        // boards preferred size to be a square within the frame.
        int min =
                (int) (FRAME_SIZE - (horizontalIndicesSouth.getPreferredSize().height
                        + horizontalIndicesNorth.getPreferredSize().height
                        + 2.5 * controlPanel.getPreferredSize().height));

        int a =
                FRAME_SIZE - (horizontalIndicesSouth.getPreferredSize().height + horizontalIndicesNorth.getPreferredSize().height);
        int b =
                FRAME_SIZE - (verticalIndicesEast.getPreferredSize().height + verticalIndicesWest.getPreferredSize().height);

        int m = Math.min(a, b);
        System.out.println(chessBoardPanelFlowWrapper.getPreferredSize() +
                "flow");
        chessBoardPanel.setPreferredSize(new Dimension(m, m));

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

                int min = Math.min(chessBoardPanelFlowWrapper.getHeight(),
                        chessBoardPanelFlowWrapper.getWidth());
                chessBoardPanel.setPreferredSize(new Dimension(min - 1,
                        min - 1));

                //chessBoardPanel.repaint();
                //chessBoardPanelWithIndicesWrapper.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private void initControlPanel() {
        JPanel controlPanelWrapper = new JPanel(new BorderLayout());

        humanPawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
        humanPawnsNumber.setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel machinePawnsNumberPanel = new JPanel();
        machinePawnsNumberPanel.setBackground(java.awt.Color.BLACK);
        machinePawnsNumber.setBackground(java.awt.Color.WHITE);
        machinePawnsNumber.setFont(new Font("Serif", Font.PLAIN, 28));
        machinePawnsNumberPanel.add(machinePawnsNumber);
        machinePawnsNumberPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        controlPanelWrapper.add(controlPanel, BorderLayout.CENTER);
        controlPanelWrapper.add(humanPawnsNumber,
                BorderLayout.WEST);
        controlPanelWrapper.add(machinePawnsNumberPanel,
                BorderLayout.EAST);

        controlPanel.setLayout(new FlowLayout());

        controlPanel.add(new JLabel("Levels:"));
        String[] levels = {"1", "2", "3", "4"};
        JComboBox<String> levelMenu = new JComboBox<>(levels);
        levelMenu.setSelectedIndex(difficultyLevel - 1);
        gameBoard.setLevel(Integer.parseInt((String) levelMenu.getSelectedItem()));
        levelMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> selectedBox =
                        (JComboBox<String>) e.getSource();
                int selectedLevel =
                        Integer.parseInt((String) selectedBox.getSelectedItem());

                difficultyLevel = selectedLevel;
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
                    updateAmountOfPawns();
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
                humanColor = Color.getOppositeColor(humanColor);
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

    private void updateAmountOfPawns() {
        amountOfHumanPawns = gameBoard.getNumberOfTiles(Player.HUMAN);
        amountOfMachinePawns = gameBoard.getNumberOfTiles(Player.MACHINE);
        humanPawnsNumber.setText(String.valueOf(amountOfHumanPawns));
        machinePawnsNumber.setText(String.valueOf(amountOfMachinePawns));
        humanPawnsNumber.repaint();
        machinePawnsNumber.repaint();
    }

    private void constructNewBoard() {
        machineMoveThread.stopThread();
        machineMoveThread = new MachineMoveThread();
        resetSelectedChessSlotPanels();
        moveParams.clear();
        undoStack.clear();
        amountOfMachinePawns = Board.SIZE;
        amountOfHumanPawns = Board.SIZE;

        gameBoard = new ChessBoard(difficultyLevel, humanColor);

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
                                updateAmountOfPawns();
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

    private void executeMachineMove() {

/*
        // Disable the humans possibility to move while the machine has not yet
        // moved.
        //TODO extract method.
        for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
            chessSlotPanel.getSlotButton().setEnabled(false);
        }

        try {
            gameBoard = gameBoard.machineMove();
        } catch (IllegalMoveException illegalMoveException) {
            if (gameBoard.isGameOver()) {
                announceWinner();
            } else {
                //TODO aussetzen
                //-> mensch kann wieder ziehen
                // falls mensch aussetzen muss
            }
        } finally {
            if (gameBoard.isGameOver()) {
                announceWinner();
            } else {
                updateAmountOfPawns();
                updateGameBoard();
                chessBoardPanel.repaint();

                // Move again if the human cannot move.
                if (gameBoard.getNextPlayer() != Player.HUMAN) {
                    System.err.println("Cannot Move!");
                    executeMachineMove();
                }
                // Enable the humans possibility to move again.
                for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
                    chessSlotPanel.getSlotButton().setEnabled(true);
                }
            }

 */
    }

    private void announceWinner() {
        updateAmountOfPawns();
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