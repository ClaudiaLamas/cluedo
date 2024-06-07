package client;

import java.io.*;
import java.net.Socket;

import server.ServerMessages;

public class Client {

    public static void main(String[] args) {

        Client client = new Client();
        try {
            client.start("localhost", 8080);
        } catch (IOException e) {
            System.out.println("Conection closed...");
        }

    }

    private void start(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new playerInput(out, socket)).start();

        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

    private class playerInput implements Runnable {

        private BufferedWriter out;
        private BufferedReader in;
        private Socket playerSocket;

        public playerInput(BufferedWriter out, Socket playerSocket) {
            this.out = out;
            this.playerSocket = playerSocket;
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {

            while (!playerSocket.isClosed()) {
                String line = null;
                try {
                    line = in.readLine();
                    out.write(line);
                    out.newLine();
                    out.flush();

                    if (line.equalsIgnoreCase("/quit")) {
                        playerSocket.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println(ServerMessages.SERVER_ERROR);
                    try {
                        playerSocket.close();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
            }
        }
    }
}
