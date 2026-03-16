package edu.kit.kastel.model;

/**
 * Represents a unit that has been placed on the board.
 * Tracks the owning player, the unit definition, and per-turn state.
 *
 * @author uwsfc
 */
public final class UnitOnBoard implements Occupant {

    private final Player owner;
    private Unit unit;
    private boolean blocked;
    private boolean blockedThisTurn;
    private boolean flipped;
    private boolean movedThisTurn;

    /**
     * Constructor to create a new UnitOnBoard, in which units start face-down and unblocked.
     *
     * @param player the player who owns this unit.
     * @param unit   the unit definition.
     */
    public UnitOnBoard(Player player, Unit unit) {
        this.owner = player;
        this.unit = unit;
        this.blocked = false;
        this.flipped = false;
        this.movedThisTurn = false;
    }

    /**
     * Getter for the player who owns this unit.
     *
     * @return the owner.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Getter for the unit definition.
     *
     * @return the unit.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Setter for the unit definition. Used after a successful merge.
     *
     * @param unit the new merged unit definition.
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns whether this unit is currently blocking.
     *
     * @return true if blocking.
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Returns whether the blockade was initiated this turn.
     *
     * @return true if blocked this turn.
     */
    public boolean isBlockedThisTurn() {
        return blockedThisTurn;
    }

    /**
     * Setter for whether the blockade was initiated this turn.
     *
     * @param blockedThisTurn the new value.
     */
    public void setBlockedThisTurn(boolean blockedThisTurn) {
        this.blockedThisTurn = blockedThisTurn;
    }

    /**
     * Method to mark a unit as having moved this turn.
     */
    public void markMoved() {
        movedThisTurn = true;
    }

    /**
     * Method to reset the moved-this-turn flag.
     */
    public void resetMoved() {
        movedThisTurn = false;
    }

    /**
     * Returns whether this unit has already moved this turn.
     *
     * @return true if moved this turn.
     */
    public boolean hasMovedThisTurn() {
        return movedThisTurn;
    }

    /**
     * Toggles the blocked state of this unit.
     */
    public void toggleBlocked() {
        blocked = !blocked;
    }

    /**
     * Returns whether this unit has been flipped (revealed) to both players.
     *
     * @return true if flipped.
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Method to reveal a unit, making its name and stats visible to both players.
     */
    public void flip() {
        this.flipped = true;
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
                + (owner instanceof HumanPlayer ? 'x' : 'y')
                + (blocked ? 'b' : ' ');
    }
}