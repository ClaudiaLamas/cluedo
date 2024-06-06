package game.cards;

public class Card {

    private String name;
    private CardsType cardType;
    private String cardArt;

    public Card(String name, CardsType cardType, String cardArt) {

        this.name = name;
        this.cardType = cardType;
        this.cardArt = cardArt;

    }

    public String getName() {
        return name;
    }

    public CardsType getCardType() {
        return cardType;
    }

    public String getCardArt() {
        return cardArt;
    }

}
