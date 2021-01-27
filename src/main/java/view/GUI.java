package view;

import model.chessboard.Board;

import javax.swing.*;
import java.awt.*;


public class GUI {

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Pawns Chess");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        frame.setLayout(new BorderLayout());

        JPanel chessBoard = new JPanel();
        chessBoard.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        //chessBoard.setPreferredSize(new Dimension(400, 400));

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                JToggleButton chessSlot = new JToggleButton();
                //chessSlot.setPreferredSize(new Dimension(10,10));
                chessSlot.setBorderPainted(false);
                if ((i + j) % 2 == 0) {
                    chessSlot.setBackground(Color.GRAY);
                } else {
                    chessSlot.setBackground(Color.WHITE);
                }
                chessBoard.add(chessSlot);
            }
        }

        frame.add(chessBoard, BorderLayout.CENTER);
/*
        JPanel controlPanel = new JPanel();
        GroupLayout layout = new GroupLayout(controlPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        controlPanel.setLayout(layout);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        JToggleButton levelMenu = new JToggleButton("level");
        //TODO passende new Klasse?
        levelMenu.add(new JMenuItem("1"));
        levelMenu.add(new JMenuItem("2"));
        controlPanel.add(levelMenu);

        controlPanel.add(new JButton("undo"));
        controlPanel.add(new JButton("new"));
        controlPanel.add(new JButton("switch"));

        layout.setHorizontalGroup(hGroup);
        frame.add(controlPanel, BorderLayout.SOUTH);

 */
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        String[] levels ={"1", "2", "3", "4"};
        JComboBox levelMenu = new JComboBox(levels);
        controlPanel.add(levelMenu);

        controlPanel.add(new JButton("undo"));
        controlPanel.add(new JButton("new"));
        controlPanel.add(new JButton("switch"));

        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}
