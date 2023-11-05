package com.katelyn_fred.game_server.server.helpers;

import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.types.GameRoom;

import java.util.HashMap;
import java.util.Map;

public class GameHelper {
    private static final Map<Integer, Game> gameList = new HashMap<>();

    private static final Map<String, GameRoom> activeGameList = new HashMap<>();

    public static Map<Integer, Game> getGameList() {
        return gameList;
    }

    public static void addGame(Game game) {
        gameList.put(game.getId(), game);
    }

    public static Game getGame(int gameId) {
        return gameList.get(gameId);
    }

    public static boolean gameExists(int gameId) {
        return gameList.containsKey(gameId);
    }

    public static Map<String, GameRoom> getActiveGameList() {
        return activeGameList;
    }

    public static void addActiveGame(GameRoom gameRoom) {
        activeGameList.put(gameRoom.getId(), gameRoom);
    }

    public static void removeActiveGame(String gameRoomId) {
        activeGameList.remove(gameRoomId);
    }

    public static boolean activeGameExists(String roomId) {
        return activeGameList.containsKey(roomId);
    }

    public static GameRoom getActiveGame(String roomId) {
        return activeGameList.get(roomId);
    }

    public static void updateActiveGame(String roomId, GameRoom gameRoom) {
        activeGameList.put(roomId, gameRoom);
    }
}
