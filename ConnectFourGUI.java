import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This is the graphical user interface for the Connect 4 game.
 *
 * The code in this class assumes that methods in ConnectFour.java
 * are working.
 */
public class ConnectFourGUI {

    public static final int ROWS = ConnectFour.ROWS;
    public static final int COLUMNS = ConnectFour.COLUMNS;

    public static final char COMPUTER = ConnectFour.COMPUTER;
    public static final char HUMAN = ConnectFour.HUMAN;
    public static final char NONE = ConnectFour.NONE;

    public static final Color COMPUTER_COLOR = Color.BLACK;
    public static final Color HUMAN_COLOR = Color.RED;
    public static final Color NONE_COLOR = Color.WHITE;
    public static final Color BOARD_COLOR = Color.YELLOW;
    public static final Color UNKNOWN = Color.GREEN;

    /**
     * Construct and display GUI for a Connect Four game.
     * @param board The game board
     * @param firstPlayer The char of player who will start.
     * @param depth How far ahead will computer player look?
     */
    public static void showGUI(final char[][] board,
                               final char firstPlayer,
                               final int depth) {
        // For thread safety, invoke GUI code on event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ConnectFourGUI gui =
                        new ConnectFourGUI(board, firstPlayer, depth);
                gui.startGame();
            }
        });
    }

    /**
     * Class used to draw the game board.
     */
    private class BoardPanel extends JPanel {

        public BoardPanel() {
            setBackground(BOARD_COLOR);
        }

        public int getRowHeight() {
            return getHeight() / ConnectFour.ROWS;
        }

        public int getColumnWidth() {
            return getWidth() / ConnectFour.COLUMNS;
        }

        @Override
        public void paintComponent( Graphics g ) {
            super.paintComponent(g);

            int rowHeight = getRowHeight();
            int colWidth = getColumnWidth();
            int rowOffset = rowHeight / 8;
            int colOffset = colWidth / 8;

            for(int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLUMNS; col++) {
                    // Flipping columns vertically so row 0 will be at bottom.
                    char piece = board[ROWS-row-1][col];
                    Color color =
                            piece == HUMAN ? HUMAN_COLOR :
                                    piece == COMPUTER ? COMPUTER_COLOR :
                                            piece == NONE ? NONE_COLOR :
                                                    UNKNOWN;
                    g.setColor(color);
                    g.fillOval( col * colWidth + colOffset,
                            row * rowHeight + rowOffset,
                            colWidth - 2*colOffset,
                            rowHeight - 2*rowOffset);
                }
            }
        }
    }

    /** Array for the game board. */
    private char[][] board;
    /** Which player's turn is it? */
    private char currentPlayer;
    /** How many moves ahead will the computer player search? */
    private int depth;

    // GUI components
    private final JFrame boardFrame;
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;

    /**
     * Handles a mouse click on the board.
     * @param x X position of the click
     * @param y Y position of the click
     */
    private void doMouseClick(int x, int y) {
        int column = x / boardPanel.getColumnWidth();

        if( currentPlayer == HUMAN ) {
            if( ConnectFour.isLegalMove(board, column) ) {
                dropPiece(column);
            } else {
                System.out.println("Human attempted illegal move at column " + column);
            }
        } else {
            System.out.println("Ignoring click on computer's turn");
        }

    }

    /**
     * Drop a piece for the current player in the given column and
     * update for next player's turn.
     * @param column Column where piece should be dropped.
     */
    private void dropPiece(int column) {
        ConnectFour.dropPiece(board,  column, currentPlayer);
        currentPlayer = ConnectFour.getOppositePlayer(currentPlayer);
        boardFrame.repaint();

        checkForWin();
    }

    /**
     * Have the computer make a move.
     */
    private void computerTurn() {
        if(currentPlayer == COMPUTER) {
            // Computer player may take a while so use worker thread
            // to think in the background instead of causing the GUI
            // to lock up.
            SwingWorker<Integer, ?> worker = new SwingWorker<Integer, Object>() {
                @Override
                public Integer doInBackground() {
                    // Make a copy of board for computer to play with
                    // so we won't refresh the GUI with the computer's
                    // thoughts.
                    char[][] boardCopy = new char[ROWS][COLUMNS];
                    for(int i = 0; i < board.length; i++) {
                        boardCopy[i] = java.util.Arrays.copyOf(board[i], board[i].length);
                    }
                    return ConnectFour.bestMoveForComputer(boardCopy, depth);
                }

                @Override
                protected void done() {
                    try {
                        int column = get();
                        if( ConnectFour.isLegalMove(board, column) ) {
                            dropPiece(column);
                        } else {
                            // Shouldn't happen if ConnectFour methods
                            // are valid, but complain just in case.
                            System.out.println("Computer attempted illegal move at column " + column);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        } else {
            System.out.println("Ignoring attempted computer play on human's turn.");
        }
    }

    /**
     * See if anyone has won and announce it if they have.
     */
    private void checkForWin() {
        statusLabel.setText("Checking for win...");

        // Checking for win may take some time, so use background
        // thread to keep GUI from locking up.
        SwingWorker<Character, ?> worker = new SwingWorker<Character, Object>() {
            @Override
            public Character doInBackground() {
                return ConnectFour.findWinner(board);
            }

            @Override
            protected void done() {
                Character winner = null;
                try {
                    winner = get();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                boolean gameOver = true;
                if( winner.equals(HUMAN) ) {
                    statusLabel.setText("Game over: Human Wins!");
                    JOptionPane.showMessageDialog ( null, "Human Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE );
                } else if( winner.equals(COMPUTER) ) {
                    statusLabel.setText("Game over: Computer Wins!");
                    JOptionPane.showMessageDialog ( null, "Computer Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE );
                } else if (ConnectFour.isBoardFull(board)) {
                    statusLabel.setText("Game over: Draw");
                    JOptionPane.showMessageDialog ( null, "Draw Game!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE );
                } else {
                    gameOver = false;
                    updateStatusLabel();
                }

                if(gameOver) System.exit(0);
                else if(currentPlayer == COMPUTER) computerTurn();
            }
        };
        worker.execute();
    }

    /** Update label under board to say whose turn it is. */
    private void updateStatusLabel() {
        if(currentPlayer == HUMAN) {
            statusLabel.setText("Human player's turn");
        } else if(currentPlayer == COMPUTER) {
            statusLabel.setText("Computer player's turn");
        } else {
            // shouldn't happen, but just in case...
            statusLabel.setText("UNKNOWN STATUS");
        }
    }

    /**
     * Constructor for the GUI.
     */
    private ConnectFourGUI(char[][] board, char player, int depth) {
        this.board = board;
        this.currentPlayer = player;
        this.depth = depth;
        boardFrame = new JFrame();
        boardFrame.setTitle("Connect Four");

        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize( new Dimension(700, 600) );
        boardPanel.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                int x = e.getX();
                int y = e.getY();
                doMouseClick(x, y);
            }
        });

        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel();
        statusPanel.add(statusLabel);
        updateStatusLabel();

        boardFrame.add(boardPanel, BorderLayout.CENTER);
        boardFrame.add(statusPanel, BorderLayout.PAGE_END);

        boardFrame.pack();
        boardFrame.setDefaultCloseOperation ( JFrame.EXIT_ON_CLOSE );
        boardFrame.setLocationRelativeTo ( null );
        //boardFrame.setResizable ( false );
        boardFrame.setVisible ( true );
    }

    /** Kick off the connect 4 game */
    private void startGame() {
        // Not likely that we were handed a non-empty board, but check anyway
        checkForWin();
    }

}
