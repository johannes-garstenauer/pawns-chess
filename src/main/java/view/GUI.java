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

    // This is the difficulty level of the machine opponent.
    private static int difficultyLevel = 3;

    // This is the color of the human player.
    private static Color humanColor = Color.WHITE;

    private int amountOfHumanPawns = Board.SIZE;
    private int amountOfMachinePawns = Board.SIZE;

    private Board gameBoard;
    private final JPanel chessBoardPanelWrapper = new JPanel();
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
                        this.run();
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

    /*
    Thread machineMoveThread = new Thread() {
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
                        this.run();
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
    };
     */


    //TODO ist das ein Bruch des Klassengeheimnis?
    private final List<ChessSlotPanel> chessSlotPanels = new ArrayList<>();


    private final List<ChessSlotPanel> moveParams = new ArrayList<>();


    public GUI() {
        super("Pawns Chess");
        constructNewBoard();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(700, 700);

        //TODO das is eig nicht so nice
        this.setResizable(false);

        //TODO das bringt was
        //this.setMinimumSize(new Dimension(700, 700));

        //TODO das bring nix :(
        //this.setMaximumSize(new Dimension(700, 700));

        this.setLayout(new BorderLayout());

        initChessBoardPanel();
        initControlPanel();
        this.setVisible(true);

        //TODO besser? -> thread als runnable?
        // Stops the extra thread when the window is closed.
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                machineMoveThread.stopThread();
            }
        });
    }


    private void initChessBoardPanel() {
        chessBoardPanelWrapper.setLayout(new BorderLayout());
        chessBoardPanel.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        for (int row = 1; row <= Board.SIZE; row++) {
            for (int col = 1; col <= Board.SIZE; col++) {

                //TODO ist diese zuweisung richtig? d.h. z.B. slots links oben
                // ist im panel auch links oben?
                ChessSlotPanel chessSlot = new ChessSlotPanel(gameBoard,
                        col, Board.SIZE + 1 - row);
                chessSlotPanels.add(chessSlot);
                //slots[row - 1][col - 1] = chessSlot;
                chessBoardPanel.add(chessSlot);
            }
        }
        chessBoardPanelWrapper.add(chessBoardPanel, BorderLayout.CENTER);
        this.add(chessBoardPanelWrapper, BorderLayout.CENTER);


        JPanel verticalIndicesWest = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel verticalIndicesEast = new JPanel(new GridLayout(Board.SIZE, 1));
        JPanel horizontalIndicesNorth = new JPanel(new GridLayout(1, Board.SIZE));
        JPanel horizontalIndicesSouth = new JPanel(new GridLayout(1, Board.SIZE));

        //TODO
        // Add spacing to horizontal indices.
        horizontalIndicesNorth.setBorder(new EmptyBorder(0,
                this.getWidth() / 10, 0,
                this.getWidth() / 15));
        horizontalIndicesSouth.setBorder(new EmptyBorder(0, 100,
                0, 0));

        System.out.println(verticalIndicesEast.getWidth());

        for (int i = Board.SIZE; i >= 1; i--) {
            verticalIndicesWest.add(new JLabel(String.valueOf(i)));
            verticalIndicesEast.add(new JLabel(String.valueOf(i)));
            horizontalIndicesSouth.add(new JLabel(String.valueOf(Board.SIZE + 1 - i)));
            horizontalIndicesNorth.add(new JLabel(String.valueOf(Board.SIZE + 1 - i)));
        }
        chessBoardPanelWrapper.add(verticalIndicesWest, BorderLayout.WEST);
        chessBoardPanelWrapper.add(verticalIndicesEast, BorderLayout.EAST);
        chessBoardPanelWrapper.add(horizontalIndicesNorth, BorderLayout.NORTH);
        chessBoardPanelWrapper.add(horizontalIndicesSouth, BorderLayout.SOUTH);

        /*
        chessBoardPanelWrapper.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("fak");
                int min = Math.min(chessBoardPanel.getTopLevelAncestor().getWidth(),
                        chessBoardPanel.getTopLevelAncestor().getHeight());
                chessBoardPanel.setBounds(new Rectangle(min, min));
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
         */

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
                for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
                    machineMoveThread.stopThread();

                    //TODO doppelt gemoppelt weil
                    frame.dispose();
                }
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

        undoStack.clear();
        amountOfMachinePawns = Board.SIZE;
        amountOfHumanPawns = Board.SIZE;

        gameBoard = new ChessBoard(difficultyLevel, humanColor);

        if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
            //executeMachineMove();
            machineMoveThread.run();
        } else {
            updateGameBoard();
            chessBoardPanel.repaint();
        }
    }

    public static void main(String[] args) {
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
                }
            } else if (moveParams.size() == 1) {
                addMoveParam(newestMoveParam);

                //attempt move
                ChessSlotPanel source = moveParams.get(0);
                ChessSlotPanel destination = moveParams.get(1);

                Board newBoard = null;
                try {
                    System.out.println("Move: ");
                    System.out.println("(" + source.getCol() + ", " + source.getRow() + ") -> (" + destination.getCol() + ", " + destination.getRow() + ")");
                    System.out.println();
                    newBoard = gameBoard.move(source.getCol(), source.getRow(),
                            destination.getCol(), destination.getRow());
                } catch (IllegalMoveException illegalMoveException) {
                    Toolkit.getDefaultToolkit().beep();
                } finally {
                    if (newBoard == null) {
                        Toolkit.getDefaultToolkit().beep();
                    } else {
                        undoStack.push(gameBoard.clone());
                        gameBoard = newBoard;
                        if (gameBoard.isGameOver()) {
                            ;
                            announceWinner();
                        } else {
                            updateAmountOfPawns();
                            updateGameBoard();
                            chessBoardPanel.repaint();
                            //executeMachineMove();
                            machineMoveThread.run();
                        }
                    }
                    moveParams.clear();
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


    //TODO AUSSETZEN!!!
    // Falls Mensch aussetzen muss
    //executeMachineMove();

    private void announceWinner() {
        updateAmountOfPawns();
        updateGameBoard();
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
}
