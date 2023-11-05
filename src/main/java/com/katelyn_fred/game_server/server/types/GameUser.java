package com.katelyn_fred.game_server.server.types;

import com.katelyn_fred.game_server.server.handlers.ClientHandler;

import java.util.HashMap;
import java.util.Map;

public class GameUser {
    public static final Map<String, GameUser> USER_MAP = new HashMap<>();
    private final ClientHandler socket;
    private final String username;

    public GameUser(String username, ClientHandler socket) {
        this.username = username;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public ClientHandler getSocket() {
        return socket;
    }
}