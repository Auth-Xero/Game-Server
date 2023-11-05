package com.katelyn_fred.game_server.server.games.base;

import com.katelyn_fred.game_server.server.types.GameRoom;
import com.katelyn_fred.game_server.server.types.GameUser;

public interface Game {
    String getName();
    int getId();
    int getRequiredPlayerCount();
    int getMaxPlayerCount();
    void start(GameRoom gameRoom);
    boolean isStarted();
    void playTurn(GameUser player, String data);
    boolean isGameOver();
}
