import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;

import java.util.Random;

public class NumberGuesser implements Game {
    private String name;
    private int id;
    private int min;
    private int max;
    private int secretNumber;
    private GameUser player1;
    private GameUser player2;
    private boolean player1Turn;
    private GameRoom gameRoom = null;
    private boolean started;
    private int player1Guess;
    private int player2Guess;

    public NumberGuesser() {
        this.id = 0;
        this.name = "Number Guesser";
        this.min = 0;
        this.max = 100;
        this.player1Turn = true;
        started = false;
        generateSecretNumber();
    }

    public NumberGuesser(int id, String name, int min, int max) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.player1Turn = true;
        generateSecretNumber();
    }

    private void generateSecretNumber() {
        Random random = new Random();
        secretNumber = random.nextInt(max - min + 1) + min;
    }

    @Override
    public String getName() {
        return name;
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
        gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", "It's your turn!");
        gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", "Waiting for your opponent's turn.");
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
        int guess = Integer.parseInt(data);
        String message;

        if ((player1Turn && player.getUsername() == player1.getUsername()) || (!player1Turn && player.getUsername() == player2.getUsername())) {
            if (player1Turn) {
                player1Guess = guess;
                message = player1.getUsername() + " guessed: " + guess;
                gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", "It's your turn!");
                gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", "Waiting for your opponent's turn.");
            } else {
                player2Guess = guess;
                message = player2.getUsername() + " guessed: " + guess;
                gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", "It's your turn!");
                gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", "Waiting for your opponent's turn.");
            }

            if (player1Guess == secretNumber || player2Guess == secretNumber) {
                String winner;
                if (player1Guess == secretNumber) {
                    winner = player1.getUsername();
                } else {
                    winner = player2.getUsername();
                }

                message = "The secret number was " + secretNumber + ". " + winner + " wins!";
                gameRoom.sendSystemMessageToAllPlayers(name + " Game",message);
                gameRoom.sendGameOver();
            } else {
                //gameRoom.sendSystemMessageToAllPlayers(name + " Game",message);

                // Determine if the guess is too high or too low
                if (guess < secretNumber) {
                    gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Your guess is too low.");
                } else {
                    gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Your guess is too high.");
                }

                player1Turn = !player1Turn;
            }
        } else {
            // Notify the player that it's not their turn
            gameRoom.sendMessageToUser(player.getUsername(), name + " Game", "Please wait for your turn.");
        }
    }

    @Override
    public boolean isGameOver() {
        return player1Guess == secretNumber || player2Guess == secretNumber;
    }
}