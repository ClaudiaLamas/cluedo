package server.commands;

import java.util.List;

import game.Game;
import game.GameMessages;
import game.GameTitles;
import game.cards.Card;
import game.Game.PlayerConnectionHandler;

public class FinalBetHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        PlayerConnectionHandler opponent = playerConnectionHandler.getOpponent();

        if (game.getRound() < 2) {
            playerConnectionHandler.send("you cannot place a final bet on the 1st round!");
            return;
        }

        String finalBetMessage = playerConnectionHandler.getMessage();

        String finalBetToSend = finalBetMessage.substring(9).trim();

        opponent.send(playerConnectionHandler.getName() + " placed a Final BET: " + finalBetToSend);

        String[] cardsToCompare = finalBetToSend.split(";");

        for (int i = 0; i < cardsToCompare.length; i++) {
            cardsToCompare[i] = cardsToCompare[i].trim();
        }

        List<Card> crimeEnvelope = game.getCrimeEnvelope();

        if (crimeEnvelope.get(0).getName().equalsIgnoreCase(cardsToCompare[0])
                && crimeEnvelope.get(1).getName().equalsIgnoreCase(cardsToCompare[1])
                && crimeEnvelope.get(2).getName().equalsIgnoreCase(cardsToCompare[2])) {

            playerConnectionHandler.send(GameTitles.WINNER_TITLE);
            opponent.send(playerConnectionHandler.getName() + "guesseed the Crime! \n\n");
            opponent.send(GameTitles.LOOSER_TITLE);

            game.finishGame();

        } else {
            playerConnectionHandler.send(GameTitles.LOOSER_TITLE);
            opponent.send(
                    playerConnectionHandler.getName() + "didn't guess the envelop crime. \n\n");
            opponent.send(GameTitles.WINNER_TITLE);

            game.finishGame();
        }

    }

}
