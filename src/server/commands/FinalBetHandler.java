package server.commands;

import java.util.List;

import game.Game;
import game.GameMessages;
import game.cards.Card;
import game.Game.PlayerConnectionHandler;

public class FinalBetHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler playerConnectionHandler) {

        PlayerConnectionHandler opponent = playerConnectionHandler.getOpponent();

        String finalBetMessage = playerConnectionHandler.getMessage();

        String finalBetToSend = finalBetMessage.substring(9).trim();

        opponent.send(playerConnectionHandler.getName() + " placed a Final BET: " + finalBetToSend);

        String[] cardsToCompare = finalBetToSend.split(";");

        for (int i = 0; i < cardsToCompare.length; i++) {
            cardsToCompare[i] = cardsToCompare[i].trim();
        }

        List<Card> crimeEnvelope = game.getCrimeEnvelope();

        System.out.println("ENVELOPE CRIME: \n"
                + crimeEnvelope.get(0).getName() + " | "
                + crimeEnvelope.get(1).getName() + " | "
                + crimeEnvelope.get(2).getName());

        System.out.println("FINAL BET: "
                + cardsToCompare[0]
                + cardsToCompare[1]
                + cardsToCompare[2]);

        if (crimeEnvelope.get(0).getName().equalsIgnoreCase(cardsToCompare[0])
                && crimeEnvelope.get(1).getName().equalsIgnoreCase(cardsToCompare[1])
                && crimeEnvelope.get(2).getName().equalsIgnoreCase(cardsToCompare[2])) {

            playerConnectionHandler.send("\n\n YOU WIN \n\n");
            opponent.send(playerConnectionHandler.getName() + "guesseed the Crime! \n\n YOU LOST THE GAME");

            game.finishGame();

        } else {
            playerConnectionHandler.send("YOU LOST");
            opponent.send(
                    playerConnectionHandler.getName() + "didn't guess the envelop crime. \n\n YOU ARE THE WINNER!");

            game.finishGame();
        }

    }

}
