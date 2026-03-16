package edu.kit.kastel.model;

/**
 * Marker interface for anything that can occupy a board square.
 *
 * @author uwsfc
 */
public interface Occupant {

    /**
     * Returns the 3-character cell string for board rendering.
     *
     * @param player the currently active player, used to determine whether this occupant belongs to the own or enemy team.
     * @return the 3-character display string.
     */
    String toString(Player player);
}