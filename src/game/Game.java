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

import javax.swing.text.html.HTMLDocument.Iterator;

import client.ClientMessages;
import game.cards.Card;
import game.cards.CardsFactory;
import game.cards.CardsType;

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
            // if (isGameStarted && !isGameFinished){
            // playround()
            // }
        }

    }

    private boolean checkIfGameCanStart() {
        return isGameFull();
    }

    public boolean isGameFull() {
        return players.size() == MAX_NUM_PLAYERS;
    }

    public void acceptPlayer(int numberOfConnections, Socket playerSocket) throws IOException {
        // Socket clientSocket = serverSocket.accept();

        PlayerConnectionHandler player = new PlayerConnectionHandler(
                playerSocket, ClientMessages.DEFAULT_NAME + numberOfConnections);

        service.submit(player);

    }

    private void startGame() {
        System.out.println("Game started...");
        this.isGameStarted = true;
        createCrimeEnvelope();
        dealCards();
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

        PlayerConnectionHandler player1 = players.get(0);
        PlayerConnectionHandler player2 = players.get(1);
        PlayerConnectionHandler player3 = players.get(2);

        // SHUFFLE DECK
        // NUMBER Of CARDS PER PLAYER
        // PICK THE CARDS FOR EACH PLAYER
        // player setHand()

    }

    private void addPlayer(PlayerConnectionHandler playerConnectionHandler) {
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

    public Optional<PlayerConnectionHandler> getPlayerByName(String name) {
        return players.stream()
                .filter(playerConnectionHandler -> playerConnectionHandler.getName().equals(name))
                .findFirst();
    }

    public class PlayerConnectionHandler implements Runnable {

        private String name;
        private final Socket clientSocket;
        private final BufferedWriter out;
        private String message;

        private List<Card> hand;

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
            addPlayer(this);
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

        public String getMessage() {
            return message;
        }

        public List<Card> getHand() {
            return hand;
        }

        public void setHand(List<Card> hand) {
            this.hand = hand;
        }

    }

}
