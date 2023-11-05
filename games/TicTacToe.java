import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;

public class TicTacToe implements Game {
    private String name = "Tic-Tac-Toe";
    private String description = "Play Tic-Tac-Toe with another player.";
    private int id;
    private char[][] board;
    private GameUser player1;
    private GameUser player2;
    private boolean player1Turn;
    private GameRoom gameRoom = null;
    private boolean started;
    private boolean gameOver;

    public TicTacToe() {
        this.id = 2;
        this.player1Turn = true;
        this.started = false;
        this.gameOver = false;
        this.board = new char[3][3];
        initializeBoard();
    }

    public TicTacToe(String dummy) {
        this.id = 2;
        this.player1Turn = true;
        this.started = false;
        this.gameOver = false;
        this.board = new char[3][3];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getRequiredPlayerCount() {
        return 2;
    }

    @Override
    public int getMaxPlayerCount() {
        return 2;
    }

    @Override
    public void start(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        started = true;
        setPlayer1(gameRoom.getGameUser(0));
        setPlayer2(gameRoom.getGameUser(1));

        gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", "You are 'X'. It's your turn.");
        gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", "You are 'O'. Waiting for your opponent's move.");
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public void setPlayer1(GameUser player) {
        this.player1 = player;
    }

    public void setPlayer2(GameUser player) {
        this.player2 = player;
    }

    @Override
    public void playTurn(GameUser player, String data) {
        int row, col;
        try {
            String[] move = data.split(",");
            row = Integer.parseInt(move[0]) - 1;
            col = Integer.parseInt(move[1]) - 1;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Invalid move format. Please use 'row,column' (e.g., '1,1').");
            return;
        }

        if (player1.getUsername().equals(player.getUsername()) && player1Turn ||
                player2.getUsername().equals(player.getUsername()) && !player1Turn) {
            if (isValidMove(row, col)) {
                char marker = player1Turn ? 'X' : 'O';
                board[row][col] = marker;

                // Notify both players about the updated board after each turn.
                String updatedBoard = "Updated Board:\n" + displayBoard();
                gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", updatedBoard);
                gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", updatedBoard);

                if (checkForWin(marker)) {
                    gameOver = true;
                    String resultMessage = player.getUsername() + " wins!";
                    gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", resultMessage);
                    gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", resultMessage);
                } else if (checkForDraw()) {
                    gameOver = true;
                    String resultMessage = "It's a draw!";
                    gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", resultMessage);
                    gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", resultMessage);
                } else {
                    player1Turn = !player1Turn;
                }
            } else {
                gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Invalid move. The cell is already occupied or out of bounds.");
            }
        } else {
            gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Please wait for your turn.");
        }
    }


    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ';
    }

    private String displayBoard() {
        StringBuilder boardStr = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardStr.append(board[row][col]);
                if (col < 2) {
                    boardStr.append(" | ");
                }
            }
            boardStr.append("\n");
            if (row < 2) {
                boardStr.append("---------\n");
            }
        }
        return boardStr.toString();
    }

    private boolean checkForWin(char marker) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == marker && board[i][1] == marker && board[i][2] == marker) {
                return true; // Row win
            }
            if (board[0][i] == marker && board[1][i] == marker && board[2][i] == marker) {
                return true; // Column win
            }
        }
        if (board[0][0] == marker && board[1][1] == marker && board[2][2] == marker) {
            return true; // Diagonal win
        }
        if (board[0][2] == marker && board[1][1] == marker && board[2][0] == marker) {
            return true; // Diagonal win
        }
        return false;
    }

    private boolean checkForDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // The game is still ongoing, not a draw.
                }
            }
        }
        return true; // All cells are occupied, and no one has won, it's a draw.
    }
}
