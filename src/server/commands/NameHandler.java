package server.commands;

import client.ClientMessages;
import game.Game;
import game.Game.PlayerConnectionHandler;

public class NameHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerHandler) {
        String message = playerHandler.getMessage();
        String name = message.substring(6);
        String oldName = playerHandler.getName();
        game.getPlayerByName(name).ifPresentOrElse(
                player -> playerHandler.send(ClientMessages.PLAYER_ALREADY_EXISTS),
                () -> {
                    playerHandler.setName(name);
                    playerHandler.send(ClientMessages.SELF_NAME_CHANGED);
                    game.broadcast(name, ClientMessages.NAME_CHANGED.formatted(oldName, name));

                });
    }

}
