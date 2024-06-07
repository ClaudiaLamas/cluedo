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

import javax.management.RuntimeErrorException;

import client.ClientMessages;
import game.cards.Card;
import game.cards.CardsFactory;
import game.cards.CardsType;
import server.commands.Command;

public class Game implements Runnable {

    private ExecutorService service;
    private final List<PlayerConnectionHandler> players;
    private static final int MAX_NUM_PLAYERS = 3;
    private boolean isGameStarted;
    private boolean isGameFinished;

    private List<Card> deck;
    private List<Card> crimeEnvelope;

    public Game() {

        service = Executors.newFixedThreadPool(MAX_NUM_PLAYERS);
        players = new ArrayList<>();
        isGameStarted = false;
        isGameFinished = false;
        deck = CardsFactory.create();

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
        System.out.println("===============");
        System.out.println("FIRST ROUND ");
        System.out.println("===============");
    }

    public boolean isGameFull() {
        return players.size() == MAX_NUM_PLAYERS;
    }

    public void acceptPlayer(Socket playerSocket) throws IOException {
        PlayerConnectionHandler player = new PlayerConnectionHandler(playerSocket);
        service.submit(player);
    }

    private boolean checkIfGameCanStart() {
        return isGameFull() && (players.get(0).getName() != null) && (players.get(1).getName() != null);
    }

    private void startGame() {
        System.out.println("Game started...");
        this.isGameStarted = true;
        createCrimeEnvelope();
        System.out.println("secret envelop Created: -> " + crimeEnvelope.toString());
        dealCards();
        System.out.println("Player's hand: ");
        for (PlayerConnectionHandler player : players) {
            player.send("Game Ready to Start!");

            // System.out.println(player.getName() + " ---> " +
            // player.getHand().toString());
        }
        broadcastAll(GameMessages.START_GAME);
        broadcastAll(GameTitles.TITLE);
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

    }

    private List<Card> selectAllCardsByType(CardsType type) {
        return deck.stream()
                .filter(card -> card.getCardType().equals(type))
                .toList();

    }

    private void dealCards() {

        while (deck.size() > 0) {

            for (int i = 0; i <= players.size(); i++) {

                Card card = deck.get((int) (Math.random() * deck.size()));
                players.get(i).getHand().add(card);
                deck.remove(card);

            }
        }

    }

    private void addPlayer(PlayerConnectionHandler playerConnectionHandler) {
        players.add(playerConnectionHandler);
        playerConnectionHandler.send(GameTitles.TITLE);
        playerConnectionHandler.send(GameMessages.COMMAND_LIST);
        // broadcast(playerConnectionHandler.getName(),
        // ClientMessages.PLAYER_ENTERED_GAME);

    }

    private void removePlayer(PlayerConnectionHandler playerConnectionHandler) {
        players.remove(playerConnectionHandler);
    }

    public void broadcast(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void broadcastAll(String message) {
        players.forEach(player -> player.send(message));
    }

    public Optional<PlayerConnectionHandler> getPlayerByName(String name) {
        return players.stream()
                .filter(playerConnectionHandler -> playerConnectionHandler.getName().equals(name))
                .findFirst();
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            addPlayer(this);
            askName();

            if (players.size() < MAX_NUM_PLAYERS) {
                send(GameMessages.WAITING_FOR_PLAYER_JOIN);
            }

            try {
                in = new Scanner(playerSocket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
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
            name = in.nextLine();
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
