package server.commands;

import java.util.stream.Collectors;

import game.Game;
import game.Game.PlayerConnectionHandler;
import game.cards.Card;

public class ShowSeenCards implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {
        playerConnectionHandler.send(playerConnectionHandler.getSeenCards().stream()
                .map(Card::getName)
                .collect(Collectors.joining(" | ")));
    }

}
