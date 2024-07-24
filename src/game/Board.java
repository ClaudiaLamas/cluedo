package game;

import java.util.List;

import game.cards.Card;

public class Board {

    public static String printAllCardsArt(List<Card> cardList) {

        List<String[]> cardLines = getCardLines(cardList);

        return printHorizontalArt(cardLines);
    }

    private static List<String[]> getCardLines(List<Card> cards) {
        return cards.stream()
                .map(card -> card.getCardArt().split("\n"))
                .toList();
    }

    private static String printHorizontalArt(List<String[]> cardLines) {

        String board = "";

        int numLines = cardLines.get(0).length;

        for (int i = 0; i < numLines; i++) {
            for (String[] lines : cardLines) {

                if (i < lines.length) {
                    board = board.concat(lines[i]);
                } else {
                    board = board.concat(" ".repeat(lines[0].length()));
                }

                board = board.concat("   ");

            }
            board = board.concat("\n");
        }

        return board;

    }

}
