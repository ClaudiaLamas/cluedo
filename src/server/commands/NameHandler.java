package server.commands;

import game.Game;
import game.Game.PlayerConnectionHandler;

public class NameHandler implements CommandHandler {

    @Override
    public void execute(Game game, PlayerConnectionHandler playerHandler) {
        String message = playerHandler.getMessage();
        String name = message.substring(6);
        String oldName = playerHandler.getName();

    }

}
