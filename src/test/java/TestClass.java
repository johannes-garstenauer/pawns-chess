import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.chessboard.Pawn;
import model.lookAheadTree.Node;
import model.player.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestClass {

    @Test
    public void BoardConstructorTest() {

        ChessBoard board = new ChessBoard(0, null);

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
            ChessBoard board2 = new ChessBoard(5, Color.WHITE);
        } catch (IllegalArgumentException ex) {
            System.out.println("board2 caught");
        }

        try {
            ChessBoard board3 = new ChessBoard(2, null);
        } catch (IllegalArgumentException ex) {
            System.out.println("board3 caught");
        }

        try {
            ChessBoard board4 = new ChessBoard(-3, Color.WHITE);
        } catch (IllegalArgumentException ex) {
            System.out.println("board5 caught");
        }


        ChessBoard board5 = new ChessBoard(2, Color.BLACK);
        assert (board5.getHumanColor() == Color.BLACK);
        assert (board5.machine.getColor() == Color.WHITE);
        assert (board5.machine.level == 2);

        ChessBoard board6 = new ChessBoard(3, Color.WHITE);
        assert (board6.getHumanColor() == Color.WHITE);
        assert (board6.machine.getColor() == Color.BLACK);
        assert (board6.machine.level == 3);
    }

    @Test
    public void MoveTest() {

        ChessBoard board = new ChessBoard(1, Color.BLACK);

        int i = 0;
        System.err.println("blackpawns");
        for (Pawn whitePawn : board.blackPawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        board.nextPlayer = Player.HUMAN;

        board = (ChessBoard) board.move(1, 1, 1, 2);

        assert (board != null);
        /*
        board.whitePawns.add(new Pawn(1, 3));

        ChessBoard newBoard = (ChessBoard) board.move(1, 3, 1, 5);

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
        ChessBoard board = new ChessBoard(0, null);

        board.whitePawns.add(new Pawn(1, 7));
        board.move(1, 7, 2, 8);

        assert (board.getNumberOfTiles(board.human) == 9);
        assert (board.getNumberOfTiles(board.machine) == 7);
    }

    @Test
    public void getSlotTest() {
        ChessBoard board = new ChessBoard(0, null);

        board.whitePawns.add(new Pawn(1, 7));
        board.move(1, 7, 2, 8);

        assert (board.getSlot(4, 4) == Color.NONE);
        assert (board.getSlot(1, 1) == Color.WHITE);
        assert (board.getSlot(5, 8) == Color.BLACK);
    }

    @Test
    public void cloneTest() {
        ChessBoard board = new ChessBoard(2, Color.BLACK);

        assert (board.getHumanColor() == Color.BLACK);

        ChessBoard clone = (ChessBoard) board.clone();

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
        ChessBoard board = new ChessBoard(0, null);

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
        ChessBoard board = new ChessBoard(0, null);

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
        ChessBoard board = new ChessBoard(0, null);

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
        ChessBoard board = new ChessBoard(0, null);

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
        ChessBoard board = new ChessBoard(0, null);

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

    //TODO: Dieser Test muss funktionieren! -> -30.0
    @Test
    public void createBoardRatingTest() {
/*
        ChessBoard board = new ChessBoard(1,Color.WHITE);
        Node node = new Node(board,null,0);
        node.createSubTree(1);

        node.setValue();
        System.out.println(node.getValue());
        */
 /*
        ChessBoard board = new ChessBoard(1,Color.WHITE);
        board.move(3,1,3,3);
        Node node = new Node(board,null,0);
        node.createSubTree(1);

        node.setValue();
        System.out.println(node.getValue());

         */
//TODO selbst überschauabres bsp erstelllen und durchrechnen


        ChessBoard board = new ChessBoard(3,Color.WHITE);

        board.whitePawns.clear();
        board.blackPawns.clear();

        board.blackPawns.add(new Pawn(8,7));
        board.blackPawns.add(new Pawn(7,6));
        board.blackPawns.add(new Pawn(6,6));
        board.blackPawns.add(new Pawn(4,6));
        board.blackPawns.add(new Pawn(3,6));
        board.blackPawns.add(new Pawn(1,8));

        board.whitePawns.add(new Pawn(2,6));
        board.whitePawns.add(new Pawn(2,4));
        board.whitePawns.add(new Pawn(4,4));
        board.whitePawns.add(new Pawn(5,1));
        board.whitePawns.add(new Pawn(6,1));
        board.whitePawns.add(new Pawn(7,1));
        board.whitePawns.add(new Pawn(8,1));

        for (Pawn whitePawn : board.whitePawns) {
            whitePawn.hasMoved();
        }

        for (Pawn blackPawn : board.blackPawns) {
            blackPawn.hasMoved();
        }

        for (int i = 5; i < 9; i++) {
            board.getPawn(i,1,Color.WHITE).hasMoved = false;
        }

        board.getPawn(1,8,Color.BLACK).hasMoved = false;
        board.setNextPlayer(Player.MACHINE);

        Node node = new Node(board,null,0);
        node.createSubTree(3);

        for (Node child : node.children) {
            child.setValue();
        }

        Node bestMove = node.getBestMove();

        System.out.println("done");
        System.out.println(bestMove.getValue());
        System.out.println();
        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : bestMove.board.whitePawns) {
            i++;
            System.out.print("Pawn: " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }
        int k = 0;
        System.err.println("blackPawns");
        for (Pawn blackPawn : bestMove.board.blackPawns) {
            k++;
            System.out.print("Pawn: " + k);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }
       // node.setValue();

        //System.out.println(node.getValue());




/*
        //Example for Size = 4
        ChessBoard board = new ChessBoard(1,Color.WHITE);
        board.whitePawns.clear();
        board.blackPawns.clear();

        board.blackPawns.add(new Pawn(1,3));
        board.blackPawns.add(new Pawn(2,2));

        board.whitePawns.add(new Pawn(2,4));
        board.whitePawns.add(new Pawn(3,1));
        board.whitePawns.add(new Pawn(4,2));


        Node node = new Node(board,null,2);
        System.out.println(node.createBoardRating());

 */

        /*
        ChessBoard board = new ChessBoard(0, null);

        board.whitePawns.clear();
        board.blackPawns.clear();

        board.whitePawns.add(new Pawn(1, 1));
        board.whitePawns.add(new Pawn(3, 3));
        board.whitePawns.add(new Pawn(4, 2));

        board.blackPawns.add(new Pawn(3, 2));
        board.blackPawns.add(new Pawn(4, 4));

        Node node = new Node()
        assert (board.createBoardRating() == -8.5);
         */
    }
    @Test
    public void isGameOverTest() {
        //Example for Size = 4
        ChessBoard board = new ChessBoard(0, null);
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

        board.whitePawns.clear();
        board.blackPawns.clear();
        board.whitePawns.add(new Pawn(1,4));
        assert (board.isGameOver());
    }

    @Test
    public void getWinnerTest() {

        //Test for size = 4
        ChessBoard board = new ChessBoard(3, Color.WHITE);

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

        board.whitePawns.clear();
        board.blackPawns.clear();
        board.whitePawns.add(new Pawn(1,1));
        assert (board.getWinner() == Player.HUMAN);
    }

    @Test
    public void getNextPlayerTest() {
        ChessBoard board = new ChessBoard(2, Color.WHITE);
        assert (board.getNextPlayer() == Player.HUMAN);

        /*
        ChessBoard board2 = new ChessBoard(2, Color.BLACK);
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

        board = (ChessBoard) board.move(1, 1, 1, 2);

        System.out.println(board.getNextPlayer());
        assert (board.getNextPlayer() == Player.MACHINE);
    }

    @Test
    public void newMoveTest() {
        //FOR SIZE 8
        ChessBoard board = new ChessBoard(2,Color.WHITE);
        board.whitePawns.clear();
        board.blackPawns.clear();

        Pawn p = new Pawn(1,1);
        board.whitePawns.add(p);
        Board res = board.move(1,1,1,3);
        assert (res != null);

        ((ChessBoard) res).setNextPlayer(Player.HUMAN);
        res = res.move(1,3,1,5);
        assert (res == null);


/*
        ChessBoard board = new ChessBoard(2,Color.WHITE);
        board.whitePawns.clear();
        board.blackPawns.clear();

        board.whitePawns.add(new Pawn(1,1));
        Board res = board.move(1,1,1,3);
        board.setNextPlayer(Player.HUMAN);

        assert (res != null);

        board.whitePawns.clear();
        board.whitePawns.add(new Pawn(1,1));
        board.blackPawns.add(new Pawn(1,2));
        res = board.move(1,1,1,3);
        board.setNextPlayer(Player.HUMAN);

        assert (res == null);

        board.whitePawns.clear();
        board.blackPawns.clear();

        board.whitePawns.add(new Pawn(1,1));
        board.whitePawns.add(new Pawn(1,2));
        res = board.move(1,1,1,3);
        board.setNextPlayer(Player.HUMAN);

        assert (res == null);


        board.whitePawns.clear();
        board.blackPawns.clear();
        Pawn p = new Pawn(1,1);
        board.whitePawns.add(p);

        res = board.move(1,1,1,3);
        assert (res != null);

        board.setNextPlayer(Player.HUMAN);
        res = board.move(1,3,1,5);
        assert (res == null);


        board.whitePawns.clear();
        board.blackPawns.clear();
        board.setNextPlayer(Player.HUMAN);
        Pawn u = new Pawn(1,4);
        board.whitePawns.add(u);

        Tupel temp = board.isLegalMove(Move.DOUBLEFORWARD,u,1,2);

        assert temp.getLegalityOfMove();

 */
/*
        board.whitePawns.clear();
        board.setNextPlayer(Player.HUMAN);
        Pawn l = new Pawn(1,1);
        board.whitePawns.add(l);
        l.hasMoved();
        res = board.move(1,1,1,3);
        assert (res == null);

 */

        /*
        ChessBoard whiteBoard = new ChessBoard(2,Color.WHITE);

        ChessBoard test1 = (ChessBoard) whiteBoard.move(1,1,1,2);
        assert (test1 != null);

        test1.nextPlayer = Player.HUMAN;
        ChessBoard test2 = (ChessBoard) test1.move(2,1,2,3);
        assert (test2 != null);

        test2.nextPlayer = Player.HUMAN;
        ChessBoard test3 = (ChessBoard) test2.move(2,3,2,4);
        assert (test3 == null);

        test2.nextPlayer = Player.HUMAN;
        ChessBoard test4 = (ChessBoard) test2.move(2,3,3,4);
        assert (test4 != null);

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : test4.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        int k = 0;
        System.err.println("blackpawns");
        for (Pawn blackpawn : test4.blackPawns) {
            k++;
            System.out.print("Pawn " + k);
            System.out.println(" Row: " + blackpawn.getRow() + " Col: " + blackpawn.getColumn());
        }

         */

        /*
        assert (test1.getPawn(1,3, Color.WHITE) != null);
        test1.nextPlayer = Player.HUMAN;
        test1.blackPawns.add(new Pawn(2,4));
        ChessBoard test4 = (ChessBoard) test1.move(1,3,2,4);
        assert (test4 != null);
        */

        /*
        // For machine
        ChessBoard blackBoard = new ChessBoard(2, Color.WHITE);

        blackBoard.makeMove(blackBoard.getPawn(1, 4, Color.BLACK),
                new Tupel(true, null), 1, 3);

        blackBoard.makeMove(blackBoard.getPawn(2, 4, Color.BLACK),
                new Tupel(true, null), 2, 2);

        blackBoard.makeMove(blackBoard.getPawn(3, 4, Color.BLACK),
                new Tupel(true, null), 3, 2);

        blackBoard.makeMove(blackBoard.getPawn(3, 2, Color.BLACK),
                new Tupel(true,
                        blackBoard.getPawn(4, 1, Color.WHITE)), 4,
                1);



        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : blackBoard.whitePawns) {
            i++;
            System.out.print("Pawn " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }

        int k = 0;
        System.err.println("blackpawns");
        for (Pawn blackpawn : blackBoard.blackPawns) {
            k++;
            System.out.print("Pawn " + k);
            System.out.println(" Row: " + blackpawn.getRow() + " Col: " + blackpawn.getColumn());
        }

         */

    }

    @Test
    public void createChildrenTest() {

        Node node1 = new Node(new ChessBoard(2, Color.WHITE), null, 0);
        node1.board.blackPawns.clear();
        node1.board.whitePawns.clear();

        /*
        node1.board.whitePawns.add(new Pawn(3, 1));
        node1.board.whitePawns.add(new Pawn(1, 3));
        node1.board.blackPawns.add(new Pawn(2, 4));
         */

        node1.board.whitePawns.add(new Pawn(2, 1));
        node1.board.blackPawns.add(new Pawn(1, 2));
        node1.board.blackPawns.add(new Pawn(3, 2));
        node1.createChildren(Player.HUMAN);

        System.out.println(node1.children.size());
        int a = 0;
        for (Node child : node1.children) {
            System.out.println();
            System.out.println("Child: " + ++a);

            int i = 0;
            System.err.println("whitepawns");
            for (Pawn whitePawn : child.board.whitePawns) {
                i++;
                System.out.print("Pawn: " + i);
                System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
            }
            int k = 0;
            System.err.println("blackPawns");
            for (Pawn blackPawn : child.board.blackPawns) {
                k++;
                System.out.print("Pawn: " + k);
                System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
            }
        }
    /*
        Node node2 = new Node(new ChessBoard(2,Color.WHITE),null);
        node2.createChildren(Player.HUMAN);

    */
    }

    @Test
    public void createSubTreeTest() {
        //For SIZE = 4

        Node node1 = new Node(new ChessBoard(2, Color.WHITE), null,0);
        node1.board.blackPawns.clear();
        node1.board.whitePawns.clear();

        node1.board.whitePawns.add(new Pawn(4, 1));
        Pawn pawn = new Pawn(3, 4);
        pawn.hasMoved();
        assert !pawn.isOpeningMove();
        node1.board.blackPawns.add(pawn);
        node1.board.blackPawns.add(new Pawn(4, 2));

        node1.createSubTree(3);

        System.out.println(node1.children.size());
        int a = 0;
        for (Node child : node1.children) {
            System.out.println();
            System.out.println("Child: " + ++a);

            int i = 0;
            System.err.println("whitepawns");
            for (Pawn whitePawn : child.board.whitePawns) {
                i++;
                System.out.print("Pawn: " + i);
                System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
            }
            int k = 0;
            System.err.println("blackPawns");
            for (Pawn blackPawn : child.board.blackPawns) {
                k++;
                System.out.print("Pawn: " + k);
                System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
            }

            int b = 0;
            for (Node node : child.children) {
                System.out.println();
                System.err.println("ChildsChild: " + ++b);

                int m = 0;
                System.err.println("whitepawns");
                for (Pawn whitePawn : node.board.whitePawns) {
                    m++;
                    System.out.print("Pawn: " + m);
                    System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
                }
                int l = 0;
                System.err.println("blackPawns");
                for (Pawn blackPawn : node.board.blackPawns) {
                    l++;
                    System.out.print("Pawn: " + l);
                    System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
                }

                int u = 0;
                for (Node node2 : node.children) {
                    System.out.println();
                    System.err.println("ChildsChildChild: " + ++u);

                    int f = 0;
                    System.err.println("whitepawns");
                    for (Pawn whitePawn : node2.board.whitePawns) {
                        f++;
                        System.out.print("Pawn: " + f);
                        System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
                    }
                    int p = 0;
                    System.err.println("blackPawns");
                    for (Pawn blackPawn : node2.board.blackPawns) {
                        p++;
                        System.out.print("Pawn: " + p);
                        System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
                    }
                }
            }
        }



    }

    @Test
    public void scoreTestOwnExample() {
        ChessBoard board = new ChessBoard(2, Color.WHITE);

        board.blackPawns.remove(board.getPawn(1,4,Color.BLACK));
        board.whitePawns.remove(board.getPawn(3,1,Color.WHITE));

        board = (ChessBoard) board.move(1,1,1,2);

        Node node = new Node(board,null,0);

        node.createSubTree(2);

        for (Node child : node.children) {
            child.setValue();
        }

        Node bestMove = node.getBestMove();

        System.out.println("done");
        System.out.println(bestMove.getValue());
        System.out.println();
        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : bestMove.board.whitePawns) {
            i++;
            System.out.print("Pawn: " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }
        int k = 0;
        System.err.println("blackPawns");
        for (Pawn blackPawn : bestMove.board.blackPawns) {
            k++;
            System.out.print("Pawn: " + k);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }
    }

    @Test
    public void specialTest() {
        ChessBoard board = new ChessBoard(2, Color.WHITE);

        board.blackPawns.remove(board.getPawn(1,4,Color.BLACK));
        board.whitePawns.remove(board.getPawn(3,1,Color.WHITE));

        board = (ChessBoard) board.move(1,1,1,3);

        //board.whitePawns.remove(board.getPawn(2,1,Color.WHITE));
        //board.whitePawns.add(new Pawn(2,3));
        board.blackPawns.remove(board.getPawn(3,4,Color.BLACK));
        board.blackPawns.add(new Pawn(3,2));

        Node node = new Node(board,null,0);

        System.out.println(node.createBoardRating());

        int i = 0;
        System.err.println("whitepawns");
        for (Pawn whitePawn : board.whitePawns) {
            i++;
            System.out.print("Pawn: " + i);
            System.out.println(" Row: " + whitePawn.getRow() + " Col: " + whitePawn.getColumn());
        }
        int k = 0;
        System.err.println("blackPawns");
        for (Pawn blackPawn : board.blackPawns) {
            k++;
            System.out.print("Pawn: " + k);
            System.out.println(" Row: " + blackPawn.getRow() + " Col: " + blackPawn.getColumn());
        }
    }
}
