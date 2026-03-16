package edu.kit.kastel.model;

/**
 * Represents the Farmer King piece for a given player.
 *
 * @author uwsfc
 */
public final class FarmerKing implements Occupant {

    private final Player owner;
    private boolean movedThisTurn;

    /**
     * Creates a new Farmer King for the given player.
     *
     * @param owner the player who owns this king.
     */
    public FarmerKing(Player owner) {
        this.owner = owner;
        this.movedThisTurn = false;
    }

    /**
     * Returns whether the king has already moved this turn.
     *
     * @return true if moved this turn.
     */
    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    /**
     * Marks the king as having moved this turn.
     */
    public void markMoved() {
        movedThisTurn = true;
    }

    /**
     * Resets the moved-this-turn flag. Called at the start of each new turn.
     */
    public void resetMoved() {
        movedThisTurn = false;
    }

    /**
     * Returns the player who owns this Farmer King.
     *
     * @return the owner.
     */
    public Player owner() {
        return owner;
    }

    /**
     * Returns the 3-character cell string for board rendering.
     *
     * @param player the currently active player.
     * @return the 3-character display string.
     */
    @Override
    public String toString(Player player) {
        boolean canMove = !movedThisTurn && owner == player;
        return (canMove ? "*" : " ")
                + (owner instanceof HumanPlayer ? 'X' : 'Y')
                + ' ';
    }
}