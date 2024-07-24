package server.commands;

import game.Board;
import game.Game;
import game.Game.PlayerConnectionHandler;

public class MyHandHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        playerConnectionHandler.send(Board.printAllCardsArt(playerConnectionHandler.getHand()));
    }

}
