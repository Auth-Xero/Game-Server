package com.katelyn_fred.game_server.server.handlers;

import com.katelyn_fred.game_server.server.helpers.UtilHelper;
import com.katelyn_fred.game_server.server.tcp.ServerEventListener;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final ServerEventListener eventListener;
    private boolean authenticated;
    private final String id;

    public ClientHandler(Socket socket, ServerEventListener listener) {
        this.clientSocket = socket;
        this.eventListener = listener;
        this.id = UtilHelper.generateID(16);
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientAddress() {
        return clientSocket.getInetAddress().toString();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getId() {
        return id;
    }

    public void closeClient() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        eventListener.onClose(this);
    }

    @Override
    public void run() {
        eventListener.onConnect(this);
        try {
            String message;
            while ((message = in.readLine()) != null) {
                eventListener.onMessage(this, message);
            }
        } catch (IOException e) {
            eventListener.onError(this, e);
        } finally {
            closeClient();
        }
    }
}
