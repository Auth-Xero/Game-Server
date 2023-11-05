package com.katelyn_fred.game_server.server.types;

import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.handlers.ClientHandler;
import com.katelyn_fred.game_server.server.helpers.GameHelper;
import com.katelyn_fred.game_server.server.helpers.UtilHelper;

import java.util.*;

public class GameRoom {
    public static final Map<String, GameRoom> ROOM_MAP = new HashMap<>();
    private final String id;
    private final String host;
    private Game game;
    private final List<GameUser> gameUsers = new ArrayList<>();

    public GameRoom(Game game, GameUser host) {
        this.game = game;
        this.id = UtilHelper.generateID(10);
        this.host = host.getUsername();
        this.gameUsers.add(host);
    }

    public void sendSystemMessageToAllPlayers(String sender,String message) {
        for (GameUser gameUser : gameUsers) {
            ClientHandler clientHandler = gameUser.getSocket();
            String[] data = new String[]{sender, message};
            SocketMessage socketMessage = new SocketMessage(2, data);
            clientHandler.sendMessage(socketMessage.toString());
        }
    }

    public void sendMessageToUser(String recipient, String sender, String message) {
        for (GameUser gameUser : gameUsers) {
            if (Objects.equals(gameUser.getUsername(), recipient)) {
                ClientHandler clientHandler = gameUser.getSocket();
                String[] data = new String[]{sender, message};
                SocketMessage socketMessage = new SocketMessage(2, data);
                clientHandler.sendMessage(socketMessage.toString());
                return;
            }
        }
    }

    public void sendGameOver() {
        for (GameUser gameUser : gameUsers) {
            ClientHandler clientHandler = gameUser.getSocket();
            clientHandler.closeClient();
        }
        GameHelper.removeActiveGame(this.id);
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addGameUser(GameUser gameUser){
        gameUsers.add(gameUser);
    }

    public int getGameUserCount() {
        return gameUsers.size();
    }

    public boolean doesUserExist(String username){
        return gameUsers.stream().anyMatch(gameUser -> gameUser.getUsername().equals(username));
    }

    public GameUser getGameUser(int index){
        return gameUsers.get(index);
    }
}