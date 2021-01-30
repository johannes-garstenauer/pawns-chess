package view;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.exceptions.IllegalMoveException;
import model.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private Stack<Board> undoStack = new Stack<>();

    //TODO ist das ein Bruch des Klassengeheimnis?
    private final List<ChessSlotPanel> chessSlotPanels = new ArrayList<>();


    private final List<ChessSlotPanel> moveParams = new ArrayList<>();


    public GUI() {
        super("Pawns Chess");
        constructNewBoard();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(700, 700);
        this.setLayout(new BorderLayout());

        initChessBoardPanel();
        initControlPanel();
        this.setVisible(true);
    }


    private void initChessBoardPanel() {
        chessBoardPanelWrapper.setLayout(new BorderLayout());

        ChessSlotPanel temp = null;
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
                temp = chessSlot;
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
                this.getWidth() / 15, 0,
                this.getWidth() / 15));
        horizontalIndicesSouth.setBorder(new EmptyBorder(0, 30,
                0 , 30));

        System.out.println(verticalIndicesEast.getWidth());

        for (int i = Board.SIZE; i >= 1 ; i--) {
            verticalIndicesWest.add(new JLabel(String.valueOf(i)));
            verticalIndicesEast.add(new JLabel(String.valueOf(i)));
            horizontalIndicesSouth.add(new JLabel(String.valueOf(Board.SIZE + 1 - i)));
            horizontalIndicesNorth.add(new JLabel(String.valueOf(Board.SIZE + 1 - i)));
        }
        chessBoardPanelWrapper.add(verticalIndicesWest, BorderLayout.WEST);
        chessBoardPanelWrapper.add(verticalIndicesEast, BorderLayout.EAST);
        chessBoardPanelWrapper.add(horizontalIndicesNorth, BorderLayout.NORTH);
        chessBoardPanelWrapper.add(horizontalIndicesSouth, BorderLayout.SOUTH);

    }

    private void initControlPanel() {
        controlPanel.setLayout(new FlowLayout());

        JLabel humanPawnsNumber =
                new JLabel(String.valueOf(amountOfHumanPawns));
        controlPanel.add(humanPawnsNumber);

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
                    gameBoard = undoStack.pop();
                    updateGameBoard();
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

        JLabel machinePawnsNumber =
                new JLabel(String.valueOf(amountOfMachinePawns));
        controlPanel.add(machinePawnsNumber);

        this.add(controlPanel, BorderLayout.SOUTH);
    }

    private void updateAmountOfPawns() {
        amountOfHumanPawns = gameBoard.getNumberOfTiles(Player.HUMAN);
        amountOfMachinePawns = gameBoard.getNumberOfTiles(Player.MACHINE);
    }
    private void constructNewBoard() {
        undoStack.clear();
        amountOfMachinePawns = Board.SIZE;
        amountOfHumanPawns = Board.SIZE;

        gameBoard = new ChessBoard(difficultyLevel, humanColor);

        if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
            executeMachineMove();
        } else {
            updateGameBoard();
            chessBoardPanel.repaint();
        }
    }

    public static void main(String[] args) {
        //invokeLater:
        new GUI();
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
                        updateAmountOfPawns();
                        undoStack.push(gameBoard.clone());
                        gameBoard = newBoard;
                        if (gameBoard.isGameOver()) {
                            updateGameBoard();
                            chessBoardPanel.repaint();
                            announceWinner();
                        } else {
                            updateGameBoard();
                            chessBoardPanel.repaint();
                            executeMachineMove();
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
            }
            updateAmountOfPawns();
            updateGameBoard();
            chessBoardPanel.repaint();
        }

        //TODO AUSSETZEN!!!
        // Falls Mensch aussetzen muss
        //executeMachineMove();
    }

    private void announceWinner() {
        if (gameBoard.getWinner() == Player.HUMAN) {
            JOptionPane.showMessageDialog(null, "You won!");
        } else if (gameBoard.getWinner() == Player.MACHINE) {
            JOptionPane.showMessageDialog(null, "You lost.");
        } else {
            JOptionPane.showMessageDialog(null, "It's a draw.");
        }
    }

    private void updateGameBoard() {
        for (ChessSlotPanel chessSlotPanel : chessSlotPanels) {
            chessSlotPanel.setGameBoard(gameBoard);
        }
    }
}
