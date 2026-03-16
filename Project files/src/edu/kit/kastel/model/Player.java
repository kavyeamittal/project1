package edu.kit.kastel.model;

/**
 * Represents a player in the game, either human or AI.
 * Tracks the player's name, deck, hand, and current life points.
 *
 * @author uwsfc
 */
public class Player {

    private static final int STARTING_LP = 8000;

    private final String name;
    private final Deck deck;
    private final Hand hand;
    private int life;

    /**
     * Creates a new Player with full life points.
     *
     * @param name the player's team name.
     * @param deck the player's deck.
     * @param hand the player's hand.
     */
    public Player(String name, Deck deck, Hand hand) {
        this.name = name;
        this.deck = deck;
        this.hand = hand;
        this.life = STARTING_LP;
    }

    /**
     * Getter for the player's team name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the player's hand.
     *
     * @return the hand.
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Returns the player's deck.
     *
     * @return the deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Getter for the player's current life points.
     *
     * @return current LP.
     */
    public int getLife() {
        return life;
    }

    /**
     * Reduces the player's life points by the given amount.
     *
     * @param amount the amount of damage to deal.
     */
    public void loseLife(int amount) {
        life -= amount;
    }
}