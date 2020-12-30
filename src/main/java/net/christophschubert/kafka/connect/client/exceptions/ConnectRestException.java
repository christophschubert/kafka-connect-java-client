package net.christophschubert.kafka.connect.client.exceptions;

public class ConnectRestException extends RuntimeException {
    public ConnectRestException(String message) {
        super(message);
    }
}
