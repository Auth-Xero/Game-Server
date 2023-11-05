package com.katelyn_fred.game_server.server.tcp;

import com.katelyn_fred.game_server.server.handlers.ClientHandler;

public interface ServerEventListener {
    void onConnect(ClientHandler client);
    void onMessage(ClientHandler client, String message);
    void onError(ClientHandler client, Exception e);
    void onClose(ClientHandler client);
}
