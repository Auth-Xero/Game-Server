package com.katelyn_fred.game_server.server.handlers;

import com.katelyn_fred.game_server.Constants;
import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.helpers.GameHelper;
import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;
import com.katelyn_fred.game_server.server.types.SocketMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SocketMessageHandler {

    public static void handleMessage(ClientHandler client, SocketMessage message) {
        try {
            int type = message.getType();
            switch (type) {
                case 0: {
                    if (!client.isAuthenticated()) {
                        String[] data = message.getData();
                        String username = data[1].toLowerCase();
                        if (Constants.BLACKLISTED_USERNAMES.contains(username)) {
                            client.closeClient();
                            return;
                        }
                        String roomId = data[0];
                        if (!GameHelper.activeGameExists(roomId)) {
                            client.closeClient();
                            return;
                        }
                        GameRoom gameRoom = GameHelper.getActiveGame(roomId);
                        if (gameRoom.getGameUserCount() + 1 > gameRoom.getGame().getMaxPlayerCount()) {
                            client.closeClient();
                            return;
                        }
                        if (gameRoom.doesUserExist(username)) {
                            client.closeClient();
                            return;
                        }
                        client.setAuthenticated(true);
                        GameUser gameUser = new GameUser(username, client);
                        GameUser.USER_MAP.put(client.getId(), gameUser);
                        gameRoom.addGameUser(gameUser);
                        GameRoom.ROOM_MAP.put(client.getId(), gameRoom);
                        if (gameRoom.getGameUserCount() >= gameRoom.getGame().getRequiredPlayerCount()) {
                            gameRoom.getGame().start(gameRoom);
                        }
                        GameHelper.updateActiveGame(roomId, gameRoom);
                    }
                    return;
                }
                case 1: {
                    if (client.isAuthenticated()) return;
                    String[] data = message.getData();
                    String username = data[1].toLowerCase();
                    if (Constants.BLACKLISTED_USERNAMES.contains(username)) {
                        client.closeClient();
                        return;
                    }
                    int gameId = Integer.parseInt(data[0]);
                    if (!GameHelper.gameExists(gameId)) return;
                    client.setAuthenticated(true);
                    Class<?> gameType = GameHelper.getGame(gameId).getClass();
                    Game game = (Game) gameType.getDeclaredConstructor().newInstance();
                    GameUser gameUser = new GameUser(username, client);
                    GameRoom gameRoom = new GameRoom(game, gameUser);
                    GameHelper.addActiveGame(gameRoom);
                    GameUser.USER_MAP.put(client.getId(), gameUser);
                    GameRoom.ROOM_MAP.put(client.getId(), gameRoom);
                    String[] data1 = new String[]{gameRoom.getId(), gameRoom.getHost(), Integer.toString(game.getId()), game.getName()};
                    SocketMessage socketMessage = new SocketMessage(1, data1);
                    client.sendMessage(socketMessage.toString());
                    return;
                }
                case 3: {
                    List<GameRoom> gameRooms = new ArrayList<>(GameHelper.getActiveGameList().values());
                    int numberOfGames = gameRooms.size();
                    String[] data = new String[numberOfGames * 4 + 1];
                    data[0] = Integer.toString(numberOfGames);

                    for (int i = 0; i < numberOfGames; i++) {
                        GameRoom gameRoom = gameRooms.get(i);
                        int startIndex = i * 4 + 1;
                        data[startIndex] = gameRoom.getId();
                        data[startIndex + 1] = gameRoom.getHost();
                        data[startIndex + 2] = Integer.toString(gameRoom.getGame().getId());
                        data[startIndex + 3] = gameRoom.getGame().getName();
                    }

                    SocketMessage socketMessage = new SocketMessage(3, data);
                    client.sendMessage(socketMessage.toString());
                    return;
                }
                case 4: {
                    if (!client.isAuthenticated()) return;
                    String[] data = message.getData();
                    String turnData = data[0];
                    GameRoom gameRoom = GameRoom.ROOM_MAP.get(client.getId());
                    GameUser gameUser = GameUser.USER_MAP.get(client.getId());
                    Game game = gameRoom.getGame();
                    if(!game.isStarted()) return;
                    game.playTurn(gameUser, turnData);
                    return;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
