import messages.ServerMessages;

import java.io.*;
import java.net.Socket;

public class Player {

    public static void main(String[] args) {

        Player player = new Player();
        try {
            player.start("localhost", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void start(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new keyboardHandler(out, socket)).start();

        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }

    private class keyboardHandler implements Runnable {

        private BufferedWriter out;
        private BufferedReader in;
        private Socket socket;

        public keyboardHandler(BufferedWriter out, Socket socket) {
            this.out = out;
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {

            while(!socket.isClosed()) {
                String line = null;
                try {
                    line = in.readLine();
                    out.write(line);
                    out.newLine();
                    out.flush();

                    if (line.equals("/quit")) {
                        socket.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println(ServerMessages.SERVER_ERROR);
                }
            }
        }
    }
}
