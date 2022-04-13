/**
 * Class to test the ConnectFour methods.
 * If your code does not pass these tests, it is broken.
 *
 * It is possible that your code is broken even if it passes these
 * tests, so make sure the game plays properly, too.
 */
public class ConnectFourTest {

    private static int correctTests = 0;
    private static int totalTests = 0;

    public static void testConstants() {
        // Make sure the constants in ConnectFour.java are as they
        // should be.

        // Board size should be as expected
        countTest(7 == ConnectFour.COLUMNS);
        countTest(6 == ConnectFour.ROWS);

        // Student could change player chars, I guess, but shouldn't
        // break the logic in the process.
        countTest(ConnectFour.COMPUTER != ConnectFour.HUMAN);
        countTest(ConnectFour.HUMAN != ConnectFour.NONE);
        countTest(ConnectFour.NONE != ConnectFour.COMPUTER);
    }

    /** Utility method to generate empty board. */
    public static char[][] emptyBoard() {
        char[][] result =
                new char[ConnectFour.ROWS][ConnectFour.COLUMNS];
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                result[r][c] = ConnectFour.NONE;
            }
        }
        return result;
    }

    public static void testBoardToString() {
        char[][] board = emptyBoard();
        String emptyBoardStr =
                "-------\n" +
                        "-------\n" +
                        "-------\n" +
                        "-------\n" +
                        "-------\n" +
                        "-------\n";
        countTest(ConnectFour.boardToString(board).equals(emptyBoardStr));

        board[0][2] = ConnectFour.HUMAN;
        board[1][2] = ConnectFour.COMPUTER;
        String boardStr =
                "-------\n" +
                        "-------\n" +
                        "-------\n" +
                        "-------\n" +
                        "--O----\n" +
                        "--X----\n";
        countTest(ConnectFour.boardToString(board).equals(boardStr));
    }

    public static void testGetOppositePlayer() {
        countTest(ConnectFour.HUMAN == ConnectFour.getOppositePlayer(ConnectFour.COMPUTER));
        countTest(ConnectFour.COMPUTER == ConnectFour.getOppositePlayer(ConnectFour.HUMAN));
    }

    public static void testDropPiece() {
        char[][] board = emptyBoard();
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);
        countTest(ConnectFour.COMPUTER == board[0][2]);
        countTest(ConnectFour.NONE == board[1][2]);
        countTest(ConnectFour.NONE == board[0][1]);
        countTest(ConnectFour.NONE == board[0][3]);
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        countTest(ConnectFour.HUMAN == board[1][2]);
        countTest(ConnectFour.COMPUTER == board[0][2]);
        countTest(ConnectFour.NONE == board[3][2]);
        countTest(ConnectFour.boardToString(board).equals("-------\n" +
                "-------\n" +
                "-------\n" +
                "-------\n" +
                "--X----\n" +
                "--O----\n"));
    }

    public static void testIsLegalMove() {
        char[][] board = emptyBoard();
        countTest(!ConnectFour.isLegalMove(board, -1));
        countTest(!ConnectFour.isLegalMove(board, 7));
        countTest(ConnectFour.isLegalMove(board, 6));
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);
        countTest(ConnectFour.isLegalMove(board, 2));
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        countTest(!ConnectFour.isLegalMove(board, 2));
    }

    public static void testIsBoardFull() {
        char[][] board = emptyBoard();
        for (int r = 0; r < ConnectFour.ROWS; r++) {
            for (int c = 0; c < ConnectFour.COLUMNS; c++) {
                countTest(!ConnectFour.isBoardFull(board));
                ConnectFour.dropPiece(board, c, ConnectFour.COMPUTER);
            }
        }
        countTest(ConnectFour.isBoardFull(board));
    }

    public static void testHorizontalWinner() {
        char[][] board = emptyBoard();
        countTest(ConnectFour.NONE == ConnectFour.findWinner(board));
        for (int c = 3; c < 7; c++) {
            ConnectFour.dropPiece(board, c, ConnectFour.COMPUTER);
        }
        countTest(ConnectFour.COMPUTER == ConnectFour.findWinner(board));
        countTest(ConnectFour.COMPUTER == ConnectFour.findLocalWinner(board, 0, 3, 0, 1));
        countTest(ConnectFour.COMPUTER == ConnectFour.findLocalWinner(board, 0, 6, 0, -1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 0, 2, 0, 1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 0, 4, 0, 1));
    }

    public static void testVerticalWinner() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 4; i++) {
            ConnectFour.dropPiece(board, 6, ConnectFour.HUMAN);
        }
        countTest(ConnectFour.HUMAN == ConnectFour.findWinner(board));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 0, 6, 1, 0));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 1, 6, 1, 0));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 3, 6, -1, 0));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 4, 6, -1, 0));
    }

    public static void testVerticalWinner2() {
        char[][] board = emptyBoard();
        ConnectFour.dropPiece(board, 3, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 3, ConnectFour.COMPUTER);
        for (int i = 0; i < 4; i++) {
            countTest(ConnectFour.NONE == ConnectFour.findWinner(board));
            ConnectFour.dropPiece(board, 3, ConnectFour.HUMAN);
        }
        countTest(ConnectFour.HUMAN == ConnectFour.findWinner(board));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 2, 3, 1, 0));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 1, 3, 1, 0));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 5, 3, -1, 0));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 4, 3, -1, 0));
    }


    public static void testDiagonalWinner1() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                ConnectFour.dropPiece(board, i, ConnectFour.HUMAN);
            }
            ConnectFour.dropPiece(board, i, ConnectFour.COMPUTER);
        }
        countTest(ConnectFour.COMPUTER == ConnectFour.findWinner(board));
        countTest(ConnectFour.COMPUTER == ConnectFour.findLocalWinner(board, 0, 0, 1, 1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 1, 1, 1, 1));
        countTest(ConnectFour.COMPUTER == ConnectFour.findLocalWinner(board, 3, 3, -1, -1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 4, 4, -1, -1));    }

    public static void testDiagonalWinner2() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                ConnectFour.dropPiece(board, 5 - i, ConnectFour.COMPUTER);
            }
            ConnectFour.dropPiece(board, 5 - i, ConnectFour.HUMAN);
        }
        countTest(ConnectFour.HUMAN == ConnectFour.findWinner(board));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 0, 5, 1, -1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 1, 4, 1, -1));
        countTest(ConnectFour.HUMAN == ConnectFour.findLocalWinner(board, 3, 2, -1, 1));
        countTest(ConnectFour.NONE == ConnectFour.findLocalWinner(board, 4, 1, -1, 1));
    }

    public static void testUndoDrop() {
        char[][] board = emptyBoard();
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        ConnectFour.undoDrop(board, 2);
        countTest(ConnectFour.COMPUTER == board[0][2]);
        countTest(ConnectFour.NONE == board[1][2]);
    }

    public static void testMax0() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 3; i++) {
            ConnectFour.dropPiece(board, i, ConnectFour.COMPUTER);
            ConnectFour.dropPiece(board,
                    ConnectFour.COLUMNS - 1 - i, ConnectFour.HUMAN);
        }
        countTest(0 == ConnectFour.maxScoreForComputer(board, 0, 0));
        ConnectFour.dropPiece(board, 3, ConnectFour.COMPUTER);
        countTest(10 == ConnectFour.maxScoreForComputer(board, 0, 0));
        ConnectFour.undoDrop(board, 3);
        ConnectFour.dropPiece(board, 3, ConnectFour.HUMAN);
        countTest(-10 == ConnectFour.maxScoreForComputer(board, 0, 0));
    }

    public static void testMax1() {
        char[][] board = emptyBoard();
        // Computer cannot win in one move
        countTest(0 == ConnectFour.maxScoreForComputer(board, 1, 0));
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        // Now computer can win in one move
        countTest(10 == ConnectFour.maxScoreForComputer(board, 1, 0));
    }

    public static void testMax2() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 3; i++) {
            ConnectFour.dropPiece(board, i + 2, ConnectFour.HUMAN);
        }
        // Computer cannot prevent human from winning
        countTest(-10 == ConnectFour.maxScoreForComputer(board, 2, 0));
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        // Now computer can win in one move
        countTest(10 == ConnectFour.maxScoreForComputer(board, 2, 0));
    }

    public static void testMin3() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 2; i++) {
            ConnectFour.dropPiece(board, i + 2, ConnectFour.HUMAN);
        }
        // Human can win in three moves
        countTest(-10 == ConnectFour.minScoreForHuman(board, 3, 0));
    }

    public static void testBestMoveForComputer() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 3; i++) {
            ConnectFour.dropPiece(board, 5, ConnectFour.HUMAN);
        }
        // Computer has to block
        countTest(5 == ConnectFour.bestMoveForComputer(board, 3));
    }

    public static void testBestMoveFullColumn() {
        char[][] board = emptyBoard();
        for (int i = 0; i < 3; i++) {
            ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        }
        for (int i = 0; i < 3; i++) {
            ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        }
        // Computer can't block, since column is full
        countTest(2 != ConnectFour.bestMoveForComputer(board, 3));
    }

    public static void testDeepSearch() {
        char[][] board = emptyBoard();
        ConnectFour.dropPiece(board, 0, ConnectFour.HUMAN);
        ConnectFour.dropPiece(board, 1, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 2, ConnectFour.HUMAN);
        ConnectFour.dropPiece(board, 5, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 6, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 0, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 1, ConnectFour.COMPUTER);
        ConnectFour.dropPiece(board, 2, ConnectFour.COMPUTER);

        countTest(ConnectFour.boardToString(board).equals("-------\n" +
                "-------\n" +
                "-------\n" +
                "-------\n" +
                "OOO----\n" +
                "XOX--OO\n"));


        // Computer 4 forces human to block at 3, but then computer wins at 3
        // OR Computer 3 forces human to block at 3 or 4, computer still wins
        countTest(4 == ConnectFour.bestMoveForComputer(board, 2) ||
                3 == ConnectFour.bestMoveForComputer(board, 2));
        // This sequence can't be seen in a shallow search
        countTest(4 != ConnectFour.bestMoveForComputer(board, 1) &&
                3 != ConnectFour.bestMoveForComputer(board, 1));
    }

    private static void clearCounts() {
        correctTests = 0;
        totalTests = 0;
    }

    private static void countTest(boolean correct) {
        if(correct) {
            correctTests++;
        } else {
            // Uncomment next line for a trace of which test failed.
            //new Exception("Failed Test").printStackTrace();
        }
        totalTests++;
    }

    public static void main(String[] args) {
        clearCounts();
        testConstants();
        testBoardToString();
        testGetOppositePlayer();
        testDropPiece();
        testIsLegalMove();
        testIsBoardFull();
        testHorizontalWinner();
        testVerticalWinner();
        testVerticalWinner2();
        testDiagonalWinner1();
        testDiagonalWinner2();
        testUndoDrop();
        testMax0();
        testMax1();
        testMax2();
        testMin3();
        testBestMoveForComputer();
        testBestMoveFullColumn();
        testDeepSearch();

        System.out.println("Passed " + correctTests + " out of " + totalTests + " tests.");
    }
}