package com.katelyn_fred.game_server.server.types;

import com.katelyn_fred.game_server.Constants;
import com.katelyn_fred.game_server.server.exceptions.InvalidMessageException;

import java.util.Arrays;

public class SocketMessage {
    private int type;
    private String[] data;

    public SocketMessage(int type, String[] data) {
        this.type = type;
        this.data = data;
    }

    public String[] getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + Constants.DELIMITER + String.join(Constants.DELIMITER, data);
    }

    public static SocketMessage parse(String rawData) throws NumberFormatException, InvalidMessageException {
        String[] data = rawData.split(Constants.DELIMITER);
        if (data.length > 0) {
            int type = Integer.parseInt(data[0]);
            if (type > 16 || type < -1) throw new InvalidMessageException("Invalid Message Type.");
            String[] messageData = Arrays.copyOfRange(data, 1, data.length);
            return new SocketMessage(type, messageData);
        }
        throw new InvalidMessageException("Invalid Message Format.");
    }
}
