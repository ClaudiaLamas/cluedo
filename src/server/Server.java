package server;

import game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    private ExecutorService service;
    private static final int PORT = 8080;

    public Server() {
    }

    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.start(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        service = Executors.newSingleThreadExecutor();
        int numberOfConnections = 0;
        System.out.printf(ServerMessages.SERVER_STARTED, port);

        Game game = new Game();
        service.execute(game);
        System.out.println(ServerMessages.GAME_CREATED);

        while (serverSocket.isBound()) {

            if (!game.isGameFull()) {
                game.acceptPlayer(serverSocket.accept());
                ++numberOfConnections;
                System.out.println(ServerMessages.NEW_PLAYER);
            }

        }

    }

}
