package hk.ust.cse.comp4521.nfcard.CardObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardHolder implements Serializable {
    private List<Card> cards;

    public CardHolder(){
        cards = new ArrayList<>();
    }

    public void addCard(Card c){
        cards.add(c);
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card getCard(String uid){
        for(Card c : cards)
            if((c.getUid().equals(uid)))
                return c;
        return null;
    }

    public Card getCard(int i){
        return cards.get(i);
    }

    public int getCardsSize(){
        return cards.size();
    }

    public boolean contentCard(String uid){
        for(Card c : cards)
            if((uid.equals(c.getUid())))
                return true;
        return false;
    }

}
