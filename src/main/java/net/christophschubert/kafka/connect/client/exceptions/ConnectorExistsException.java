package net.christophschubert.kafka.connect.client.exceptions;

public class ConnectorExistsException extends ConnectRestException {
    public ConnectorExistsException(String message) {
        super(message);
    }
}
