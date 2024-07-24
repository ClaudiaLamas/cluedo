package game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.cards.Card;
import game.cards.CardsFactory;
import game.cards.CardsType;
import server.commands.Command;

public class Game implements Runnable {

    private ExecutorService service;
    private final List<PlayerConnectionHandler> players;
    private static final int MAX_NUM_PLAYERS = 2;
    private boolean isGameStarted;
    private boolean isGameFinished;

    private List<Card> deck;
    private List<Card> crimeEnvelope;
    private int round;

    public Game() {

        service = Executors.newFixedThreadPool(MAX_NUM_PLAYERS);
        players = new ArrayList<>();
        isGameStarted = false;
        isGameFinished = false;
        deck = CardsFactory.create();
        crimeEnvelope = new ArrayList<>();

    }

    @Override
    public void run() {
        while (!isGameFinished) {
            if (checkIfGameCanStart() && !isGameStarted) {
                startGame();

            }
            if (isGameStarted && !isGameFinished) {
                playround();

            }
        }
        finishGame();

    }

    private void finishGame() {
        for (PlayerConnectionHandler player : players) {
            player.quitGame();
        }
        isGameFinished = true;
    }

    private void playround() {
        round++;
        broadcastAll("=== ROUND -----> " + round + "  =====");

        for (PlayerConnectionHandler player : players) {

            PlayerConnectionHandler opponent = player.getOpponent();

            player.send(GameMessages.PLAYER_TURN);
            opponent.send(String.format(GameMessages.OPPONENT_TURN, player.getName()));

            while (true) {
                if (player.getMessage() == null || opponent.getMessage() == null) {
                    continue;
                }

                String answer;

                if (player.getMessage().equals("/bet")) {

                    answer = opponent.getMessage();

                    if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("one card name")) {
                        break;
                    }
                    ;

                }
            }
        }
    }

    public boolean isGameFull() {
        synchronized (players) {
            return players.size() == MAX_NUM_PLAYERS;
        }
    }

    public void acceptPlayer(Socket playerSocket) throws IOException {

        if (isGameStarted) {
            System.out.println("the game is already full!");
            playerSocket.close();
            return;
        }
        System.out.println("Accepting new player...");
        PlayerConnectionHandler player = new PlayerConnectionHandler(playerSocket);
        synchronized (players) {
            players.add(player);
            System.out.println("Player added: " + playerSocket.toString());
        }
        service.submit(player);
    }

    private boolean checkIfGameCanStart() {
        synchronized (players) {
            boolean canStart = players.size() == MAX_NUM_PLAYERS &&
                    players.stream().allMatch(player -> player.getName() != null && !player.getName().isEmpty());
            return canStart;
        }
    }

    private void startGame() {
        // if (isGameStarted)
        // return;

        System.out.println("Game started... - whith players: " + players.size());
        this.isGameStarted = true;
        createCrimeEnvelope();
        System.out.println("secret envelop Created: -> " + "\n" + crimeEnvelope.get(0).getCardArt() + " \n "
                + crimeEnvelope.get(1).getCardArt() + " \n " + crimeEnvelope.get(2).getCardArt());
        dealCards();

        System.out.println("Player's hand: ");
        synchronized (players) {
            for (PlayerConnectionHandler player : players) {
                player.send("Game Ready to Start!");

                System.out.println(player.getName() + " ---> " +
                        player.getHand().toString());
            }
        }
        broadcastAll(GameMessages.START_GAME);
        broadcastAll(GameTitles.TITLE);
        broadcastAll(GameMessages.COMMAND_LIST);
    }

    private void createCrimeEnvelope() {

        List<Card> placeCards = selectAllCardsByType(CardsType.PLACES);
        List<Card> criminalCards = selectAllCardsByType(CardsType.CRIMINALS);
        List<Card> weaponCards = selectAllCardsByType(CardsType.WEAPONS);

        Card placeCrime = placeCards.get((int) (Math.random() * 6));
        Card criminalCrime = criminalCards.get((int) (Math.random() * 3));
        Card weaponCrime = weaponCards.get((int) (Math.random() * 6));

        crimeEnvelope.add(placeCrime);
        crimeEnvelope.add(criminalCrime);
        crimeEnvelope.add(weaponCrime);

        deck.remove(placeCrime);
        deck.remove(criminalCrime);
        deck.remove(weaponCrime);

        System.out.println("Crime envelope created with: " + crimeEnvelope);

    }

    private List<Card> selectAllCardsByType(CardsType type) {
        return deck.stream()
                .filter(card -> card.getCardType().equals(type))
                .toList();

    }

    private void dealCards() {

        int playerIndex = 0;

        while (deck.size() > 0) {
            Card card = deck.get((int) (Math.random() * deck.size()));
            synchronized (players) {
                players.get(playerIndex).getHand().add(card);
            }
            deck.remove(card);
            playerIndex = (playerIndex + 1) % players.size();

        }

    }

    // private void addPlayer(PlayerConnectionHandler playerConnectionHandler) {
    // synchronized (players) {
    // players.add(playerConnectionHandler);
    // }
    // playerConnectionHandler.send(GameTitles.TITLE);
    // playerConnectionHandler.send(GameMessages.COMMAND_LIST);
    // // broadcast(playerConnectionHandler.getName(),
    // // ClientMessages.PLAYER_ENTERED_GAME);

    // }

    private void removePlayer(PlayerConnectionHandler playerConnectionHandler) {
        synchronized (players) {
            players.remove(playerConnectionHandler);
        }
    }

    public void broadcast(String name, String message) {
        synchronized (players) {
            players.stream()
                    .filter(handler -> !handler.getName().equals(name))
                    .forEach(handler -> handler.send(name + ": " + message));
        }
    }

    public void broadcastAll(String message) {
        synchronized (players) {
            players.forEach(player -> player.send(message));
        }
    }

    public Optional<PlayerConnectionHandler> getPlayerByName(String name) {
        synchronized (players) {
            return players.stream()
                    .filter(playerConnectionHandler -> playerConnectionHandler.getName().equals(name))
                    .findFirst();
        }
    }

    public class PlayerConnectionHandler implements Runnable {

        private String name;
        private String message;
        private final Socket playerSocket;
        private final BufferedWriter out;
        private Scanner in;

        private List<Card> hand;

        public PlayerConnectionHandler(Socket playerSocket) {
            this.playerSocket = playerSocket;
            try {
                this.out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
                in = new Scanner(playerSocket.getInputStream());
                this.hand = new ArrayList<>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public PlayerConnectionHandler getOpponent() {

            if (players.get(0).equals(this)) {
                return players.get(1);
            }

            return players.get(0);

        }

        public void quitGame() {
            try {
                playerSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            askName();

            if (players.size() < MAX_NUM_PLAYERS) {
                send(GameMessages.WAITING_FOR_PLAYER_JOIN);
            }

            while (in.hasNext()) {
                message = in.nextLine();
                if (isCommand(message)) {
                    dealWhithCommand(message);
                    continue;
                }

                broadcast(name, message);
            }

        }

        private void askName() {
            send(GameMessages.ENTER_NAME);
            while (true) {
                name = in.nextLine().trim();
                if (!name.isEmpty())
                    break;
                send(GameMessages.ENTER_NAME);
            }
            send("Hi, " + name);
        }

        private void dealWhithCommand(String message) {

            String description = message.split(" ")[0];
            Command command = Command.getCommandDescription(description);

            command.getHandler().handleCommands(Game.this, this);

        }

        private boolean isCommand(String message) {
            return message.startsWith("/");
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

        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }

        public List<Card> getHand() {
            return hand;
        }

        public void setHand(List<Card> hand) {
            this.hand = hand;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
