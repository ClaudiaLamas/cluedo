package server.commands;

import game.Game;
import game.GameMessages;
import game.Game.PlayerConnectionHandler;

public class BetHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        Game.PlayerConnectionHandler opponent = playerConnectionHandler.getOpponent();

        String betMessage = playerConnectionHandler.getMessage();

        String betToSend = betMessage.substring(4).trim();

        opponent.send("Bet placed by " + playerConnectionHandler.getName() + ":  " + betToSend);
        opponent.send("\n Do you have any of these cards to show to your opponent?");

        playerConnectionHandler.send(GameMessages.YOU_BET + betToSend);

    }

}
