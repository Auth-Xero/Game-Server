package com.katelyn_fred.game_server;

import com.katelyn_fred.game_server.loader.GameLoader;
import com.katelyn_fred.game_server.server.tcp.Server;

public class App 
{
    public static void main( String[] args )
    {
        GameLoader gameLoader = new GameLoader(Constants.GAME_DIRECTORY);
        gameLoader.loadGames();
        Server server = new Server(Constants.PORT);
        server.start();
    }
}
