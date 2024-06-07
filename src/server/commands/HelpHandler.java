package server.commands;

import game.Game;
import game.GameMessages;
import game.Game.PlayerConnectionHandler;

public class HelpHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerHandler) {
        playerHandler.send(GameMessages.COMMAND_LIST);
    }

}
