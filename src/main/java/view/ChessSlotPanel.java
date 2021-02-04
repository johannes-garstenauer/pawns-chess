package view;

import model.chessboard.Board;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessSlotPanel extends JPanel {

    private Board gameBoard;
    private int col;
    private int row;
    private JButton slotButton = new JButton();

    // Used to color blue if this tiles pawn was selected to be moved.
    private boolean isSelectedPawn = false;

    public ChessSlotPanel(Board gameBoard, int col, int row) {
        super();
        this.gameBoard = gameBoard;
        this.col = col;
        this.row = row;

        initSlotButton();
    }

    public void initSlotButton() {
        slotButton.setContentAreaFilled(false);
        slotButton.setBorderPainted(false);

        //TODO noch bisschen hässlich nach unten verschoben... -> minimum size?
        slotButton.setPreferredSize(this.getMaximumSize());

        //TODO: USE THIS TO UNSELECT AFTER MOVE
        //pawnButton.setSelected(false);

        slotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI parentFrame = (GUI) getTopLevelAncestor();

                JButton source = (JButton) e.getSource();

                parentFrame.attemptMove((ChessSlotPanel) source.getParent());
            }
        });

        this.add(slotButton);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (isSelectedPawn) {
            this.setBackground(Color.CYAN);
        } else if ((row + col) % 2 == 0) {
            this.setBackground(Color.LIGHT_GRAY);
        } else {
            this.setBackground(Color.DARK_GRAY);
        }

        Graphics2D g2 = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (gameBoard.getSlot(col, row) == model.chessboard.Color.WHITE) {

            // Draw outlines of the pawns head and torso
            g2.setColor(Color.BLACK);
            g2.drawOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.drawOval(centerX - (getWidth() / 6), centerY - (getHeight() / 3),
                    (getWidth() / 3), getHeight() / 3);

            // Draw pawns head and torso
            g2.setColor(Color.WHITE);
            g2.fillOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.fillOval(centerX - (getWidth() / 6), centerY - (getHeight() / 3),
                    getWidth() / 3, getHeight() / 3);

        } else if (gameBoard.getSlot(col, row) == model.chessboard.Color.BLACK) {

            // Draw pawns head and torso
            g2.setColor(Color.BLACK);
            g2.fillOval((int) (getWidth() * (0.17)), (int) (centerY * 0.93),
                    (int) (getWidth() * 0.67), getHeight());
            g2.fillOval(centerX - (getWidth() / 6), centerY - (getHeight() / 3),
                    getWidth() / 3, getHeight() / 3);
        }
    }

    //TODO: prakotmat sagt paintComponent klappen tuts aber mit paint hmm
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        System.out.println("paintComponent");
        if ((row + col) % 2 == 0) {
            this.setBackground(Color.LIGHT_GRAY);
        } else {
            this.setBackground(Color.DARK_GRAY);
        }

        Graphics2D g2 = (Graphics2D) g;
        if (gameBoard.getSlot(col, row) == model.chessboard.Color.WHITE) {
            g2.setColor(Color.WHITE);

            g2.fillOval(10, 20, getWidth() - 20,
                    getHeight() + 40);
        } else if (gameBoard.getSlot(col, row) == model.chessboard.Color.BLACK) {
            g2.setColor(Color.BLACK);

            g2.fillOval(10, 20, getWidth() - 20,
                    getHeight() + 40);
        }
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public JButton getSlotButton() {
        return slotButton;
    }

    public void setSelectedPawn(boolean isSelectedPawn) {
        this.isSelectedPawn = isSelectedPawn;
    }
}
