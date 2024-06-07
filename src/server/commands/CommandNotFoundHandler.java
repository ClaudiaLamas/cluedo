package server.commands;

import game.Game;
import game.GameMessages;
import game.Game.PlayerConnectionHandler;

public class CommandNotFoundHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerHandler) {

        playerHandler.send(GameMessages.INVALID_COMMAND);

    }

}
