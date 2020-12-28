import model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestClass {

    @Test
    public void BoardConstructorTest() {

        BoardImpl board = new BoardImpl(0, null);

        System.out.println(board.getHumanColor());
        assert (board.getHumanColor() == Color.WHITE);
        assert (board.machine.getColor() == Color.BLACK);
        assert (board.machine.level == 3);

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : board.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        int j = 0;
        System.err.println("blackpawns");
        for (Pawn blackPawn : board.blackPawns) {
            j++;
            System.out.print("Pawn " + j);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }

        try {
            BoardImpl board2 = new BoardImpl(5, Color.WHITE);
        } catch (IllegalArgumentException ex) {
            System.out.println("board2 caught");
        }

        try {
            BoardImpl board3 = new BoardImpl(2, null);
        } catch (IllegalArgumentException ex) {
            System.out.println("board3 caught");
        }

        try {
            BoardImpl board4 = new BoardImpl(-3, Color.WHITE);
        } catch (IllegalArgumentException ex) {
            System.out.println("board5 caught");
        }


        BoardImpl board5 = new BoardImpl(2, Color.BLACK);
        assert (board5.getHumanColor() == Color.BLACK);
        assert (board5.machine.getColor() == Color.WHITE);
        assert (board5.machine.level == 2);

        BoardImpl board6 = new BoardImpl(3, Color.WHITE);
        assert (board6.getHumanColor() == Color.WHITE);
        assert (board6.machine.getColor() == Color.BLACK);
        assert (board6.machine.level == 3);
    }

    @Test
    public void MoveTest() {

        BoardImpl board = new BoardImpl(1, Color.BLACK);

        int i = 0;
        System.err.println("blackpawns");
        for (Pawn whitePawn : board.blackPawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        board.nextPlayer = Player.HUMAN;

        board = (BoardImpl) board.move(1, 1, 1, 2);

        assert (board != null);
        /*
        board.whitePawns.add(new Pawn(1, 3));

        BoardImpl newBoard = (BoardImpl) board.move(1, 3, 1, 5);

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : board.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        int j = 0;
        System.err.println("blackpawns");
        for (Pawn blackPawn : board.blackPawns) {
            j++;
            System.out.print("Pawn " + j);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }

        int k = 0;
        System.err.println(" new whitepawns");
        for (Pawn whitePawn : newBoard.whitePawns) {
            k++;
            System.out.print("Pawn " + k);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        int l = 0;
        System.err.println("new blackpawns");
        for (Pawn blackPawn : newBoard.blackPawns) {
            l++;
            System.out.print("Pawn " + l);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }

         */
    }

    @Test
    public void NumberOfTilesTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.whitePawns.add(new Pawn(1, 7));
        board.move(1, 7, 2, 8);

        assert (board.getNumberOfTiles(board.human) == 9);
        assert (board.getNumberOfTiles(board.machine) == 7);
    }

    @Test
    public void getSlotTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.whitePawns.add(new Pawn(1, 7));
        board.move(1, 7, 2, 8);

        assert (board.getSlot(4, 4) == Color.NONE);
        assert (board.getSlot(1, 1) == Color.WHITE);
        assert (board.getSlot(5, 8) == Color.BLACK);
    }

    @Test
    public void cloneTest() {
        BoardImpl board = new BoardImpl(2, Color.BLACK);

        assert (board.getHumanColor() == Color.BLACK);

        BoardImpl clone = (BoardImpl) board.clone();

        board.move(1, 1, 1, 3);
        board.move(2, 1, 2, 3);

        clone.setLevel(3);

        assert (board.getHumanColor() == clone.getHumanColor());
        System.out.println("clone: " + clone.machine.level);
        System.out.println("board: " + board.machine.level);
        assert (board.machine.level == clone.machine.level);

        int i = 0;
        System.err.println("clone black");
        for (Pawn blackPawn : clone.blackPawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }

        int j = 0;
        System.err.println("black");
        for (Pawn blackPawn : board.blackPawns) {
            j++;
            System.out.print("Pawn " + j);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }
    }

    @Test
    public void pawnCloneTest() {
        Pawn pawn = new Pawn(1, 1);

        Pawn copy = pawn.clone();

        pawn.hasMoved();

        assert (copy.isOpeningMove() != pawn.isOpeningMove());
    }

    @Test
    public void helperTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.move(1, 1, 1, 2);
        board.move(2, 1, 2, 2);

        assert (board.amountOfPawnsInRow(1, board.whitePawns) == 6);
        assert (board.amountOfPawnsInRow(2, board.whitePawns) == 2);
        assert (board.amountOfPawnsInRow(4, board.whitePawns) == 0);
        assert (board.amountOfPawnsInRow(8, board.whitePawns) == 0);
        assert (board.amountOfPawnsInRow(8, board.blackPawns) == 8);
    }

    @Test
    public void getPawnTest() {
        BoardImpl board = new BoardImpl(0, null);

        Pawn pawn = board.getPawn(1, 1, Color.WHITE);

        board.move(1, 1, 1, 2);

        assert (pawn != null);
        assert (pawn.getRow() == 2 && pawn.getColumn() == 1);

        boolean pawnInWhite = false;
        for (Pawn white : board.whitePawns) {
            if (pawn == white) {
                pawnInWhite = true;
            }
        }
        assert (pawnInWhite);
    }

    @Test
    public void determineThreatenedTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.blackPawns.add(new Pawn(1, 2));
        board.blackPawns.add(new Pawn(3, 2));

        List<Pawn> list = new ArrayList<>();

        list = board.determineThreatenedPawns(board.getPawn(2, 1, Color.WHITE),
                Color.BLACK);

        assert (!list.isEmpty());
        assert (list.size() == 2);
    }

    @Test
    public void isPawnProtectedTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.whitePawns.add(new Pawn(1, 2));
        board.blackPawns.add(new Pawn(4, 4));

        assert (board.isPawnProtected(board.getPawn(1, 2, Color.WHITE),
                Color.WHITE));

        assert (!board.isPawnProtected(board.getPawn(4, 4, Color.BLACK),
                Color.BLACK));
    }

    @Test
    public void pawnEqualsTest() {
        Pawn pawn = new Pawn(1, 1);

        Pawn clone = pawn.clone();

        assert (pawn.equals(clone));

        clone.hasMoved();

        assert (!pawn.equals(clone));
    }

    @Test
    public void isPawnIsolatedTest() {
        BoardImpl board = new BoardImpl(0, null);

        board.move(1, 1, 1, 2);
        assert (!board.isPawnIsolated(board.getPawn(1, 2, Color.WHITE)
                , Color.WHITE));

        board.move(1, 2, 1, 3);
        assert (board.isPawnIsolated(board.getPawn(1, 3, Color.WHITE)
                , Color.WHITE));

        board.move(1, 3, 1, 4);
        board.move(1, 4, 1, 5);
        board.move(1, 5, 1, 6);
        board.move(1, 6, 1, 7);
        assert (board.isPawnIsolated(board.getPawn(1, 7, Color.WHITE)
                , Color.WHITE));

    }

    @Test
    public void createBoardRatingTest() {

        //Example for Size = 4
        BoardImpl board = new BoardImpl(0, null);

        board.whitePawns.clear();
        board.blackPawns.clear();

        board.whitePawns.add(new Pawn(1, 1));
        board.whitePawns.add(new Pawn(3, 3));
        board.whitePawns.add(new Pawn(4, 2));

        board.blackPawns.add(new Pawn(3, 2));
        board.blackPawns.add(new Pawn(4, 4));

        assert (board.createBoardRating() == -8.5);
    }

    @Test
    public void isGameOverTest() {
        //Example for Size = 4
        BoardImpl board = new BoardImpl(0, null);
        assert (!board.isGameOver());

        board.whitePawns.clear();
        board.blackPawns.clear();
        assert (board.isGameOver());


        board.whitePawns.add(new Pawn(1, 4));
        assert (board.isGameOver());

        board.whitePawns.clear();


        Pawn p = new Pawn(2, 1);
        board.whitePawns.add(new Pawn(1, 1));
        board.whitePawns.add(p);
        board.whitePawns.add(new Pawn(3, 3));
        board.blackPawns.add(new Pawn(1, 2));
        board.blackPawns.add(new Pawn(3, 4));

        assert (!board.isGameOver());

        board.whitePawns.remove(p);

        assert (board.isGameOver());

    }

    @Test
    public void getWinnerTest() {

        //Test for size = 4
        BoardImpl board = new BoardImpl(0, null);

        board.whitePawns.clear();
        board.blackPawns.clear();
        assert (board.getWinner() == null);

        board.whitePawns.add(new Pawn(4, 4));

        board.blackPawns.add(new Pawn(1, 2));

        assert (board.getWinner() == Player.HUMAN);
        board.whitePawns.clear();
        board.blackPawns.clear();

        board.blackPawns.add(new Pawn(1, 1));
        assert (board.getWinner() == Player.MACHINE);
    }

    @Test
    public void getNextPlayerTest() {
        BoardImpl board = new BoardImpl(2, Color.WHITE);
        assert (board.getNextPlayer() == Player.HUMAN);

        /*
        BoardImpl board2 = new BoardImpl(2, Color.BLACK);
        assert (board2.getNextPlayer() == Player.MACHINE);

         */

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : board.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }
        int j = 0;
        System.err.println("blackpawns");
        for (Pawn blackPawn : board.blackPawns) {
            j++;
            System.out.print("Pawn " + j);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }

        board = (BoardImpl) board.move(1, 1, 1, 2);

        System.out.println(board.getNextPlayer());
        assert (board.getNextPlayer() == Player.MACHINE);
    }

    @Test
    public void newMoveTest() {
        BoardImpl whiteBoard = new BoardImpl(2,Color.WHITE);

        BoardImpl test1 = (BoardImpl) whiteBoard.move(1,1,1,3);
        assert (test1 != null);

        test1.nextPlayer = Player.HUMAN;
        BoardImpl test2 = (BoardImpl) test1.move(1,3,2,3);
        assert (test2 == null);

        test1.nextPlayer = Player.HUMAN;
        BoardImpl test3 = (BoardImpl) test1.move(1,3,2,4);
        assert (test3 == null);

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : test1.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        assert (test1.getPawn(1,3, Color.WHITE) != null);
        test1.nextPlayer = Player.HUMAN;
        test1.blackPawns.add(new Pawn(2,4));
        BoardImpl test4 = (BoardImpl) test1.move(1,3,2,4);
        assert (test4 != null);

        // For black Pawns
        BoardImpl blackBoard = new BoardImpl(2,Color.BLACK);

        blackBoard.nextPlayer = Player.HUMAN;
        BoardImpl test5 = (BoardImpl) blackBoard.move(1,1,1,3);
        assert (test5 != null);

        test5.nextPlayer = Player.HUMAN;
        BoardImpl test6 = (BoardImpl) test5.move(1,3,2,3);
        assert (test6 == null);

        test5.nextPlayer = Player.HUMAN;
        BoardImpl test7 = (BoardImpl) test5.move(1,3,2,4);
        assert (test7 == null);

        test5.nextPlayer = Player.HUMAN;
        test5.whitePawns.add(new Pawn(2,4));
        BoardImpl test8 = (BoardImpl) test5.move(1,3,2,4);
        assert (test8 != null);
    }

    @Test
    public void createChildrenTest() {

        Node node1 = new Node(new BoardImpl(2,Color.BLACK),null);
        node1.createChildren(Player.MACHINE);

        System.out.println(node1.children.size());
        int a = 0;
        for (Node child : node1.children) {
            System.out.println();
            System.out.println("Child: "+ ++a);

            int i = 0;
            System.err.println("whitepawns");
            for (Pawn whitePawn : child.board.whitePawns) {
                i++;
                System.out.print("Pawn " + i);
                System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
            }
            int k = 0;
            System.err.println("blackPawns");
            for (Pawn blackPawn : child.board.blackPawns) {
                k++;
                System.out.print("Pawn " + k);
                System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
            }
        }

        Node node2 = new Node(new BoardImpl(2,Color.WHITE),null);
        node2.createChildren(Player.HUMAN);
    }

    @Test
    public void cloneTreeTest() {
        BoardImpl board = new BoardImpl(3,Color.WHITE);

        BoardImpl copy = (BoardImpl) board.clone();

        assert (copy != null);
    }
}
