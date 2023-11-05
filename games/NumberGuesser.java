import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;

import java.util.Random;

public class NumberGuesser implements Game {
    private String name;
    private String description;
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

    // Constructor for the NumberGuesser game
    public NumberGuesser() {
        this.id = 0;
        this.name = "Number Guesser";
        this.description = "A game where the first person to guess the correct number wins!";
        this.min = 0;
        this.max = 100;
        this.player1Turn = true;
        started = false;
        generateSecretNumber();
    }

    public NumberGuesser(String dummy) {
        this.id = 0;
        this.name = "Number Guesser";
        this.description = "A game where the first person to guess the correct number wins!";
        this.min = 0;
        this.max = 100;
        this.player1Turn = true;
        started = false;
    }

    // Constructor with custom game parameters
    public NumberGuesser(int id, String name, int min, int max) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.player1Turn = true;
        generateSecretNumber();
    }

    // Generates a random secret number within the specified range
    private void generateSecretNumber() {
        Random random = new Random();
        secretNumber = random.nextInt(max - min + 1) + min;
    }

    // Getter for the game name
    @Override
    public String getName() {
        return name;
    }

    // Getter for the game description
    @Override
    public String getDescription() {
        return description;
    }


    // Getter for the game ID
    @Override
    public int getId() {
        return id;
    }

    // Returns the number of players required for the game
    @Override
    public int getRequiredPlayerCount() {
        return 2;
    }

    // Returns the maximum number of players the game can accommodate
    @Override
    public int getMaxPlayerCount() {
        return 2;
    }

    // Initializes the game and notifies players about their turn
    @Override
    public void start(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        started = true;
        setPlayer1(gameRoom.getGameUser(0));
        setPlayer2(gameRoom.getGameUser(1));

        // Notify players about their turn and the game status
        gameRoom.sendMessageToUser(player1.getUsername(), name + " Game", "It's your turn!");
        gameRoom.sendMessageToUser(player2.getUsername(), name + " Game", "Waiting for your opponent's turn.");
    }

    // Checks if the game has started
    @Override
    public boolean isStarted() {
        return started;
    }

    // Sets the first player
    public void setPlayer1(GameUser player) {
        this.player1 = player;
    }

    // Sets the second player
    public void setPlayer2(GameUser player) {
        this.player2 = player;
    }

    // Handles a player's turn and updates game state
    @Override
    public void playTurn(GameUser player, String data) {
        int guess = Integer.parseInt(data);
        String message;

        // Check if it's the player's turn and process their guess
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

            // Check if a player has won
            if (player1Guess == secretNumber || player2Guess == secretNumber) {
                String winner;
                if (player1Guess == secretNumber) {
                    winner = player1.getUsername();
                } else {
                    winner = player2.getUsername();
                }

                message = "The secret number was " + secretNumber + ". " + winner + " wins!";
                gameRoom.sendSystemMessageToAllPlayers(name + " Game", message);
                gameRoom.sendGameOver();
            } else {
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

    // Checks if the game is over
    @Override
    public boolean isGameOver() {
        return player1Guess == secretNumber || player2Guess == secretNumber;
    }
}
