package exception;

import java.rmi.ServerException;

public class ServerCouldNotLaunch extends ServerException {

    public ServerCouldNotLaunch(String message) {
        super(message);
    }
}
