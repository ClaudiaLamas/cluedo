package server.commands;

import java.util.Optional;
import game.Game;
import game.GameMessages;
import game.Game.PlayerConnectionHandler;
import game.cards.Card;

public class ShowCardHandler implements CommandHandler {

    @Override
    public void handleCommands(Game game, PlayerConnectionHandler opponent) {

        PlayerConnectionHandler player = opponent.getOpponent();

        String[] commandParts = opponent.getMessage().split(" ", 2);

        if (commandParts.length < 2) {
            opponent.send("Usage: /showcard <CardName>");
            return;
        }

        String cardName = commandParts[1];

        Optional<Card> cardOptional = opponent.getHand().stream()
                .filter(card -> card.getName().equalsIgnoreCase(cardName))
                .findFirst();

        if (cardOptional.isPresent()) {
            Card cardToShow = cardOptional.get();
            player.send("\n\n\n" + opponent.getName() + " " + GameMessages.OPPONENT_HAS_A_CARD + " \n\n"
                    + cardToShow.getCardArt());
            player.getSeenCards().add(cardToShow);
            player.getMissCards().remove(cardToShow);
            opponent.send("You showed the card: \n\n" + cardToShow.getName());

        } else {
            opponent.send(GameMessages.YOU_DONT_HAVE_THAT_CARD);
        }

    }

}
