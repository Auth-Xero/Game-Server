package com.katelyn_fred.game_server.server.exceptions;

public class InvalidMessageException extends Exception {
    public InvalidMessageException(String errorMessage) {
        super(errorMessage);
    }
}
