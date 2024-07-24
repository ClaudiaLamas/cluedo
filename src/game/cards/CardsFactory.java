package game.cards;

import java.util.ArrayList;
import java.util.List;

public class CardsFactory {
    public static List<Card> create() {

        List<Card> deck = new ArrayList<>();

        Card p1MindSwap = new Card("MindSwap", CardsType.PLACES, CardArt.p1MindSwap);
        Card p2Porto42 = new Card("42Porto", CardsType.PLACES, CardArt.p2Porto42);
        Card p3CodeForAll = new Card("CodeForAll_", CardsType.PLACES, CardArt.p3CodeForAll);
        Card p4Switch = new Card("Switch", CardsType.PLACES, CardArt.p4Switch);
        Card p5IronHack = new Card("Ironhack", CardsType.PLACES, CardArt.p5IronHack);
        Card p6DataCamp = new Card("DataCamp", CardsType.PLACES, CardArt.p6DataCamp);
        Card c1Diogo = new Card("DiogoVelho", CardsType.CRIMINALS, CardArt.c1Diogo);
        Card c2Christophe = new Card("ChristopheSoares", CardsType.CRIMINALS, CardArt.c2Christophe);
        Card c3Joao = new Card("JoaoAnes", CardsType.CRIMINALS, CardArt.c3Joao);
        Card w1Git = new Card("Git", CardsType.WEAPONS, CardArt.w1Git);
        Card w2Java = new Card("Java", CardsType.WEAPONS, CardArt.w2Java);
        Card w3JavaScript = new Card("JavaScript", CardsType.WEAPONS, CardArt.w3JavaScript);
        Card w4Docker = new Card("Docker", CardsType.WEAPONS, CardArt.w4Docker);
        Card w5SpringBoot = new Card("SpringBoot", CardsType.WEAPONS, CardArt.w5SpringBoot);
        Card w6React = new Card("React", CardsType.WEAPONS, CardArt.w6React);

        deck.add(p1MindSwap);
        deck.add(p2Porto42);
        deck.add(p3CodeForAll);
        deck.add(p4Switch);
        deck.add(p5IronHack);
        deck.add(p6DataCamp);
        deck.add(c1Diogo);
        deck.add(c2Christophe);
        deck.add(c3Joao);
        deck.add(w1Git);
        deck.add(w2Java);
        deck.add(w3JavaScript);
        deck.add(w4Docker);
        deck.add(w5SpringBoot);
        deck.add(w6React);

        return deck;
    }
}
