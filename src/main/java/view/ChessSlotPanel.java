package view;

import model.chessboard.Board;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessSlotPanel extends JPanel{

    private Board gameBoard;
    private int col;
    private int row;
    private JToggleButton slotButton = new JToggleButton();

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

        //TODO noch bisschen hässlich nach unten verschoben...
        slotButton.setPreferredSize(this.getMaximumSize());

        //TODO: USE THIS TO UNSELECT AFTER MOVE
        //pawnButton.setSelected(false);

        slotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI parentFrame = (GUI) getTopLevelAncestor();

                JToggleButton source = (JToggleButton) e.getSource();

                parentFrame.attemptMove((ChessSlotPanel) source.getParent());
            }
        });

        this.add(slotButton);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

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
        } else if (gameBoard.getSlot(col, row) == model.chessboard.Color.BLACK)
        {
            g2.setColor(Color.BLACK);

            g2.fillOval(10, 20, getWidth() - 20,
                    getHeight() + 40);
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
        } else if (gameBoard.getSlot(col, row) == model.chessboard.Color.BLACK)
        {
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
}
