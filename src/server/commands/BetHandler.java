package server.commands;

import game.Game;
import game.Game.PlayerConnectionHandler;

public class BetHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        Game.PlayerConnectionHandler opponent = playerConnectionHandler.getOpponent();

        // playerHandler.send()

    }

}
