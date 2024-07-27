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
import java.util.stream.Collectors;

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

    public void finishGame() {
        for (PlayerConnectionHandler player : players) {
            player.quitGame();
        }
        isGameFinished = true;
    }

    private void startGame() {

        System.out.println("Game started... - whith players: " + players.size());
        this.isGameStarted = true;
        createCrimeEnvelope();
        dealCards();

        System.out.println("Player's hand: ");
        synchronized (players) {
            for (PlayerConnectionHandler player : players) {
                player.send("Game Ready to Start!");
                player.getSeenCards().addAll(player.hand);
                player.getMissCards().removeAll(player.seenCards);

                System.out.println(player.getName() + " ---> " +
                        player.getHand().toString());
            }
        }

        broadcastAll(GameMessages.START_GAME);
        broadcastAll(GameTitles.TITLE);
        broadcastAll(GameMessages.COMMAND_LIST);
    }

    private void playround() {
        round++;
        broadcastAll("=== ROUND -----> " + round + "  =====");

        // for (PlayerConnectionHandler player : players) {
        for (int i = 0; i < players.size(); i++) {

            PlayerConnectionHandler player = players.get(i);

            PlayerConnectionHandler opponent = player.getOpponent();

            player.send(GameMessages.PLAYER_TURN);

            String missingMessage = getMissingCards(player);
            player.send("Missing Cards: " + missingMessage + "\n\n");

            opponent.send(String.format(GameMessages.OPPONENT_TURN, player.getName()));
            opponent.send(GameMessages.SHOW_HAND_OF_CARDS
                    + Board.printAllCardsArt(opponent.getHand()));

            boolean betReceived = false;

            while (!betReceived) {
                String playerMessage = player.getMessage();

                if (playerMessage != null && playerMessage.startsWith("/bet")) {
                    player.send("Bet received: " + playerMessage);
                    betReceived = true;
                    break;
                }

            }

            boolean validResponse = false;

            while (!validResponse) {
                String opponentMessage = opponent.getMessage();

                if (opponentMessage != null && opponentMessage.startsWith("/showcard")) {
                    Command command = Command.getCommandDescription("/showcard");
                    command.getHandler().handleCommands(this, opponent);
                    validResponse = true;
                    break;
                } else if (opponentMessage.equalsIgnoreCase("no")) {
                    player.send(GameMessages.OPPONENT_DOESNT_HAVE_CARD_TO_SHOW);
                    validResponse = true;
                    break;

                } else {
                    opponent.send("Invalid command");
                }
            }

        }
    }

    public String getMissingCards(PlayerConnectionHandler player) {
        return (player.getMissCards().stream().map(Card::getName)
                .collect(Collectors.joining(" | ")));
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

    private void createCrimeEnvelope() {

        List<Card> criminalCards = selectAllCardsByType(CardsType.CRIMINALS);
        List<Card> placeCards = selectAllCardsByType(CardsType.PLACES);
        List<Card> weaponCards = selectAllCardsByType(CardsType.WEAPONS);

        Card criminalCrime = criminalCards.get((int) (Math.random() * 3));
        Card placeCrime = placeCards.get((int) (Math.random() * 6));
        Card weaponCrime = weaponCards.get((int) (Math.random() * 6));

        crimeEnvelope.add(criminalCrime);
        crimeEnvelope.add(placeCrime);
        crimeEnvelope.add(weaponCrime);

        deck.remove(criminalCrime);
        deck.remove(placeCrime);
        deck.remove(weaponCrime);

        System.out.println("Crime envelope created with: " + crimeEnvelope.stream()
                .map(card -> card.getName())
                .collect(Collectors.joining(" | ")));

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

    public List<Card> getCrimeEnvelope() {
        return crimeEnvelope;
    }

    public class PlayerConnectionHandler implements Runnable {

        private String name;
        private String message;
        private final Socket playerSocket;
        private final BufferedWriter out;
        private Scanner in;

        private List<Card> hand;
        private List<Card> missingCards;
        private List<Card> seenCards;

        public PlayerConnectionHandler(Socket playerSocket) {
            this.playerSocket = playerSocket;
            try {
                this.out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
                in = new Scanner(playerSocket.getInputStream());
                this.hand = new ArrayList<>();
                this.missingCards = new ArrayList<>(deck);
                missingCards.removeAll(crimeEnvelope);
                this.seenCards = new ArrayList<>();
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

        public List<Card> getMissCards() {
            return missingCards;
        }

        public List<Card> getSeenCards() {
            return seenCards;
        }

        public void setHand(List<Card> hand) {
            this.hand = hand;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Scanner getScanner() {
            return in;
        }

        public void clearMessage() {
            this.message = null;
        }

    }

}
