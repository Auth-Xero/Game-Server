package com.katelyn_fred.game_server.server.tcp;

import com.katelyn_fred.game_server.server.handlers.ClientHandler;
import com.katelyn_fred.game_server.server.handlers.SocketMessageHandler;
import com.katelyn_fred.game_server.server.types.SocketMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ServerEventListener {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    @Override
    public void onConnect(ClientHandler client) {
        System.out.println("Client connected: " + client.getClientAddress());
    }

    @Override
    public void onMessage(ClientHandler client, String message) {
        try {
            System.out.println("Received message from " + client.getClientAddress() + ": " + message);
            SocketMessage socketMessage = SocketMessage.parse(message);
            SocketMessageHandler.handleMessage(client, socketMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onError(ClientHandler client, Exception e) {
        System.out.println("Error for client " + client.getClientAddress() + ": " + e.getMessage());
    }

    @Override
    public void onClose(ClientHandler client) {
        System.out.println("Client disconnected: " + client.getClientAddress());
        removeClient(client);
    }

}
