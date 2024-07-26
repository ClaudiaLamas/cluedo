package server.commands;

import game.Game;
import game.GameMessages;
import game.Game.PlayerConnectionHandler;

public class FinalBetHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        PlayerConnectionHandler opponent = playerConnectionHandler.getOpponent();

        String finalBetMessage = playerConnectionHandler.getMessage();

        String finalBetToSend = finalBetMessage.substring(9).trim();

        opponent.send(playerConnectionHandler.getName() + " placed a Final BET: " + finalBetToSend);

    }

}
