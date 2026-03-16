package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's hand of unit cards.
 *
 * @author uwsfc
 */
public class Hand {

    private final List<Unit> cards = new ArrayList<>();

    /**
     * Adds a unit card to the hand.
     *
     * @param unit the unit to add.
     */
    public void add(Unit unit) {
        cards.add(unit);
    }

    /**
     * Returns the number of cards currently in the hand.
     *
     * @return the hand size.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns the unit at the given 0-based index without removing it.
     *
     * @param index the 0-based index.
     * @return the unit at that index.
     */
    public Unit get(int index) {
        return cards.get(index);
    }

    /**
     * Removes and returns the unit at the given 0-based index.
     *
     * @param index the 0-based index.
     * @return the removed unit.
     */
    public Unit remove(int index) {
        return cards.remove(index);
    }
}