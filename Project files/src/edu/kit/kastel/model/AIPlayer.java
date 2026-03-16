package edu.kit.kastel.model;

/**
 * The Class for the AI Player (Enemy), which extends from the Player Class.
 *
 * @author uwsfc
 */
public class AIPlayer extends Player {
    /**
     * Constructor of the AI Player, which is a wrapper for the Player Constructor.
     *
     * @param name represents the name of the enemy.
     * @param deck represents the deck which the enemy has.
     * @param hand represents the hand which the enemy has.
     */
    public AIPlayer(String name, Deck deck, Hand hand) {
        super(name, deck, hand);
    }
}
