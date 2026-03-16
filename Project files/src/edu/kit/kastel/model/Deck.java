package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a player's deck of unit cards.
 * Cards are drawn from the top (index 0) of the deck.
 *
 * @author uwsfc
 */
public class Deck {

    private final List<Unit> totalcards;

    /**
     * Creates a new Deck with the given list of units.
     *
     * @param totalcards the units to include in the deck.
     */
    public Deck(List<Unit> totalcards) {
        this.totalcards = new ArrayList<>(totalcards);
    }

    /**
     * Shuffles the deck using the given random generator.
     *
     * @param rnd the random generator to use.
     */
    public void shuffle(Random rnd) {
        Collections.shuffle(totalcards, rnd);
    }

    /**
     * Draws and removes the top card from the deck.
     *
     * @return the drawn unit, or null if the deck is empty.
     */
    public Unit drawCard() {
        if (totalcards.isEmpty()) {
            return null;
        }
        return totalcards.removeFirst();
    }

    /**
     * Returns the number of cards remaining in the deck.
     *
     * @return the deck size.
     */
    public int size() {
        return totalcards.size();
    }
}