package edu.kit.kastel.model;

/**
 * Represents the human player in the game, used to distinguish the human player from the AI player.
 *
 * @author uwsfc
 */
public class HumanPlayer extends Player {

    /**
     * Creates a new HumanPlayer.
     *
     * @param name the player's team name.
     * @param deck the player's deck.
     * @param hand the player's hand.
     */
    public HumanPlayer(String name, Deck deck, Hand hand) {
        super(name, deck, hand);
    }
}