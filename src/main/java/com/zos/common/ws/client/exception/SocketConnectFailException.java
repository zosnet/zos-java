package com.zos.common.ws.client.exception;

public class SocketConnectFailException extends RuntimeException {

    public SocketConnectFailException() {
        super();
    }

    public SocketConnectFailException(String message) {
        super(message);
    }

    public SocketConnectFailException(Throwable cause) {
        super(cause);
    }


    public SocketConnectFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
