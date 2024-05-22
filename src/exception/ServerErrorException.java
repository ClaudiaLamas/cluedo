package exception;

import java.rmi.ServerException;

public class ServerErrorException extends ServerException {
    public ServerErrorException(String message) {
        super(message);
    }
}
