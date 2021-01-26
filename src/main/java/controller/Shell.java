package controller;

import model.chessboard.Board;
import model.chessboard.ChessBoard;
import model.chessboard.Color;
import model.exceptions.IllegalMoveException;
import model.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Shell with commands in order to interact with a chessboard for pawns chess.
 */
public final class Shell {
    private static final String PROMPT = "pc> ";
    private static final Pattern WHITESPACE_SPLIT = Pattern.compile("\\s+");

    // This is the difficulty level of the machine opponent.
    private static int difficultyLevel = 3;

    // This is the color of the human player.
    private static Color humanColor = Color.WHITE;

    /**
     * Private constructor for the Shell class.
     */
    private Shell() {
    }

    /**
     * Reads from the run window and triggers commands.
     *
     * @param args Program arguments are not used.
     * @throws IOException Possibly caused by readLine().
     */
    public static void main(String[] args) throws IOException {
        Board gameBoard = null;
        boolean quit = false;
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(System.in));

        while (!quit) {
            System.out.print(PROMPT);
            String input = bufferedReader.readLine();

            if (input == null) {
                break;
            }

            String[] tokenParts = WHITESPACE_SPLIT.split(input);

            if (tokenParts.length == 0) {
                printError("There is no command");
            } else {
                if (tokenParts[0].isEmpty()) {

                    // Remove initial whitespaces from the input tokens because
                    // they are most likely accidental.
                    String[] newTokenParts = new String[tokenParts.length - 1];
                    for (int i = 0; i < newTokenParts.length; i++) {
                        newTokenParts[i] = tokenParts[i + 1];
                    }
                    tokenParts = newTokenParts;
                }

                switch (tokenParts[0].toLowerCase().charAt(0)) {
                case 'n':
                    gameBoard = constructNewBoard(tokenParts, gameBoard);
                    break;
                case 'h':
                    printHelp(tokenParts);
                    break;
                case 'l':
                    if (!(gameBoard == null)) {
                        setDifficultyLevel(tokenParts, gameBoard);
                    } else {
                        printError("There is no board/machine opponent to "
                                + "change the level on.\nTry command: 'NEW'.");
                    }
                    break;
                case 'm':
                    if (!(gameBoard == null)) {
                        gameBoard = executeMoves(tokenParts, gameBoard);
                    } else {
                        printError("There is no board to play on. Try command:"
                                + " 'NEW'.");
                    }
                    break;
                case 's':
                    if (!(gameBoard == null)) {
                        gameBoard = executeSwitch(tokenParts, gameBoard);
                    } else {
                        printError("There is no board to switch colors on. "
                                + "Try command: 'NEW'.");
                    }
                    break;
                case 'q':
                    quit = true;
                    break;
                case 'p':
                    if (!(gameBoard == null)) {
                        printBoard(tokenParts, gameBoard);
                    } else {
                        printError("There is no board to print out. Try "
                                + "command: 'NEW'.");
                    }
                    break;
                default:
                    printError("Command unknown. Type \"help\" for further "
                            + "hints.");
                    break;
                }
            }
        }
    }

    /**
     * Starts a new game but switches the humans and machines color.
     *
     * @param tokenParts String array of all arguments.
     * @param gameBoard  The board on which the action is performed.
     */
    private static Board executeSwitch(String[] tokenParts, Board gameBoard) {
        assert gameBoard != null && tokenParts != null;

        if (hasCorrectAmountArguments(tokenParts, 1)) {
            humanColor = Color.getOppositeColor(humanColor);
            return constructNewBoard(tokenParts, gameBoard);
        } else {

            // Return a null-board if the amount of arguments was not correct.
            return null;
        }
    }

    /**
     * Executes the human move first, then the resulting move of the machine
     * opponent. No more moves will be allowed if there is stalemate or a winner
     * between the moves or after. You will be notified if you are not able
     * to perform a move or if the move results in the machine having to
     * suspend. You will also be notified if your move was invalid.
     *
     * @param tokenParts String array of all arguments.
     * @param gameBoard  The board on which the action is performed.
     * @return The Board on which the move was executed
     */
    private static Board executeMoves(String[] tokenParts, Board gameBoard) {
        assert gameBoard != null && tokenParts != null;

        if (hasCorrectAmountArguments(tokenParts, 5)) {

            // The board on which the move will be attempted.
            Board moveBoard = null;

            // Attempt the human move.
            try {
                moveBoard = gameBoard.move(Integer.parseInt(tokenParts[1]),
                        Integer.parseInt(tokenParts[2]),
                        Integer.parseInt(tokenParts[3]),
                        Integer.parseInt(tokenParts[4]));
            } catch (NumberFormatException numberFormatException) {
                printError("The command move contains four coordinates as "
                        + "arguments.\nFor example: 'move 1 3 1 4'.\n"
                        + "This moves the pawn at column one and row "
                        + "three to column one and row four.");
            } catch (IllegalArgumentException illegalArgumentException) {
                printError(illegalArgumentException.getMessage());
            } catch (IllegalMoveException illegalMoveException) {
                if (gameBoard.isGameOver()) {
                    announceWinner(gameBoard);
                    return gameBoard;
                } else {
                    printError(illegalMoveException.getMessage());
                }
            }

            if (moveBoard == null) {

                try {
                    // If the move was invalid an error message will be
                    // displayed and the machine will not be allowed to move.
                    printError("Invalid move from ("
                            + Integer.parseInt(tokenParts[1]) + ", "
                            + Integer.parseInt(tokenParts[2]) + ") to ("
                            + Integer.parseInt(tokenParts[3]) + ", "
                            + Integer.parseInt(tokenParts[4]) + ").");
                } catch (NumberFormatException numberFormatException) {

                    // Do nothing as there already is an error message for
                    // this scenario.
                }
                return gameBoard;
            } else {
                gameBoard = moveBoard;
            }

            // Attempt the machines response move, if the human was succesful
            try {
                gameBoard = gameBoard.machineMove();
                if (gameBoard.isGameOver()) {
                    announceWinner(gameBoard);
                }
            } catch (IllegalMoveException illegalMoveException) {
                if (gameBoard.isGameOver()) {
                    announceWinner(gameBoard);
                } else {
                    System.out.println(illegalMoveException.getMessage());
                }
            }
        }
        return gameBoard;
    }

    /**
     * Determines the winner (or a draw) and returns the results on the
     * console.
     *
     * @param gameBoard The board on which the winner is determined.
     */
    private static void announceWinner(Board gameBoard) {
        assert gameBoard != null;

        if (gameBoard.getWinner() == Player.HUMAN) {
            System.out.println("Congratulations! You won.");
        } else if (gameBoard.getWinner() == Player.MACHINE) {
            System.out.println("Sorry! Machine wins.");
        } else {
            System.out.println("Nobody wins. Draw.");
        }
    }

    /**
     * Prints out the games board on the console in a square. 'W' indicates a
     * white pawn while 'B' indicates a black pawn.
     *
     * @param tokenParts String array of all arguments.
     * @param gameBoard  The board which is printed out.
     */
    private static void printBoard(String[] tokenParts, Board gameBoard) {
        assert gameBoard != null;

        if (hasCorrectAmountArguments(tokenParts, 1)) {
            System.out.println(gameBoard);
        }
    }

    /**
     * Sets the difficulty level of the machine opponent.
     *
     * @param tokenParts String array of all arguments.
     * @param gameBoard  The board on which the action is performed.
     */
    private static void setDifficultyLevel(String[] tokenParts,
                                           Board gameBoard) {
        assert tokenParts != null && gameBoard != null;

        if (hasCorrectAmountArguments(tokenParts, 2)) {

            int newDifficultyLevel = 0;
            try {
                newDifficultyLevel = Integer.parseInt(tokenParts[1]);
            } catch (NumberFormatException numberFormatException) {
                printError("Please enter a valid number to set a new "
                        + "difficulty level.");
            }

            if (newDifficultyLevel < 1 || newDifficultyLevel > 4) {
                printError("The difficulty level must lie between one and "
                        + "four!");
            } else {
                difficultyLevel = newDifficultyLevel;
                gameBoard.setLevel(difficultyLevel);
            }
        }
    }

    /**
     * Creates a new chessboard to play on with the pawns in their initial
     * positions and the same difficulty level and opening player as on the
     * game before. In case of the first game, the human player will start
     * and the difficulty level is 3. If the machine is the opening player it
     * will perform its first move.
     *
     * @param tokenParts String array of all arguments.
     * @param board      The board on which the action is performed.
     * @return The board on which the game will take place.
     */
    private static Board constructNewBoard(String[] tokenParts, Board board) {

        Board gameBoard = board;
        if (hasCorrectAmountArguments(tokenParts, 1)) {
            try {
                gameBoard = new ChessBoard(difficultyLevel, humanColor);
            } catch (IllegalArgumentException exception) {

                // This exception cannot be caused by a faulty user interaction
                // therefore the program is terminated.
                throw new IllegalArgumentException(exception.getMessage());
            }


            System.out.print("New game started. ");

            // The toString() method cannot be used as it would create a
            // coherent result for this use.
            if (humanColor == Color.WHITE) {
                System.out.println("You are white.");
            } else {
                System.out.println("You are black.");
            }

            // If the machine opens the board, it should make the first move.
            if (gameBoard.getOpeningPlayer() == Player.MACHINE) {
                try {
                    gameBoard = gameBoard.machineMove();
                } catch (IllegalMoveException illegalMoveException) {
                    if (gameBoard.isGameOver()) {

                        // It is very unlikely that this happens but
                        // nonetheless possible when working with a very
                        // small board size.
                        announceWinner(gameBoard);
                        return gameBoard;
                    } else {
                        System.out.println(illegalMoveException.getMessage());
                    }
                }
                if (gameBoard.isGameOver()) {
                    announceWinner(gameBoard);
                }
            }
        }
        return gameBoard;
    }

    /**
     * Determines whether the command has the required amount of arguments.
     *
     * @param tokenParts String array of all arguments.
     * @param amount     The required amount of arguments.
     * @return Return {@code true} if the expected amount of arguments is met.
     * Return {@code false} otherwise.
     */
    private static boolean hasCorrectAmountArguments(String[] tokenParts,
                                                     int amount) {
        assert tokenParts != null && amount > 0;

        if (tokenParts.length != amount) {
            printError("The amount of arguments is incorrect.");
        }
        return tokenParts.length == amount;
    }

    /**
     * Prints out an error message.
     *
     * @param message Description of the error message.
     */
    private static void printError(String message) {
        System.out.println("Error! " + message);
    }

    /**
     * Prints a help message containing information about available commands.
     *
     * @param tokenParts String array of all arguments.
     */
    private static void printHelp(String[] tokenParts) {
        if (hasCorrectAmountArguments(tokenParts, 1)) {
            System.out.println("Using this program you can play a game of "
                    + "pawns chess.\n"
                    + "Following commands are available:\n");
            System.out.println("NEW : Creates a new game where you have the "
                    + "same color as before and the\n      machine has the "
                    + "same difficulty as in the last game.\n      The initial "
                    + "values are that the human player is white and the "
                    + "\n      difficulty level is 3.");
            System.out.println("LEVEL <number> : Sets the difficulty level to "
                    + "the given number. \n      Please use values between "
                    + "1 and 4.");
            System.out.println("MOVE <colFrom> <rowFrom> <colTo> <rowTo> : \n"
                    + "     This moves one of your pawns from the given\n     "
                    + "column and row to the given destination column and row."
                    + "\n     The machine will move automatically afterwards."
                    + "\n     If you or the machine have to suspend you will "
                    + "be notified.");
            System.out.println("SWITCH : Starts a new game and switches your "
                    + "and the machines colors.");
            System.out.println("PRINT: Prints out the game board on the "
                    + "console.");
            System.out.println("HELP : Prints out his help message.");
            System.out.println("QUIT : Terminates this program.");
        }
    }
}
