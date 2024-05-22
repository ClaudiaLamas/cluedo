import exception.ServerCouldNotLaunch;
import messages.ClientMessages;
import messages.ServerMessages;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.start(8080);
        } catch (ServerCouldNotLaunch e) {
            throw new RuntimeException(e);
        }

    }

    private ServerSocket serverSocket;
    private ExecutorService service;
    private final List<PlayerConnectionHandler> players;

    public Server() {
        players = new CopyOnWriteArrayList<>();
    }

    public void start(int port) throws ServerCouldNotLaunch {

        try {
            serverSocket = new ServerSocket(port);
            service = Executors.newCachedThreadPool();
            int numberOfConnections = 0;
            System.out.printf(ServerMessages.SERVER_STARTED, port);

            while (true) {
                acceptConnection(numberOfConnections);
                ++numberOfConnections;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerCouldNotLaunch(ServerMessages.SERVER_COULNT_LAUNCH);
        }
    }

    private void acceptConnection(int numberOfConnections) throws IOException {
        Socket clientSocket = serverSocket.accept();

        PlayerConnectionHandler playerConnectionHandler = new PlayerConnectionHandler(clientSocket, ClientMessages.DEFAULT_NAME + numberOfConnections);

        service.submit(playerConnectionHandler);


    }

    private void addClient(PlayerConnectionHandler playerConnectionHandler) {
        players.add(playerConnectionHandler);
        playerConnectionHandler.send(ClientMessages.WELCOME.formatted(playerConnectionHandler.getName()));
        broadcast(playerConnectionHandler.getName(), ClientMessages.PLAYER_ENTERED_GAME);

    }
    private void removePlayer(PlayerConnectionHandler playerConnectionHandler) {
        players.remove(playerConnectionHandler);
    }

    public void broadcast(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public class PlayerConnectionHandler implements Runnable {

        private String name;
        private final Socket clientSocket;
        private final BufferedWriter out;
        private String message;

        public PlayerConnectionHandler(Socket clientSocket, String name) throws IOException {
            this.clientSocket = clientSocket;
            this.name = name;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        }

        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removePlayer(this);
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            addClient(this);
            Scanner in = null;

            try {
                in = new Scanner(clientSocket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (in.hasNext()) {
                message = in.nextLine();
                broadcast(name, message);
            }

        }

        public String getName() {
            return name;
        }


    }




}
