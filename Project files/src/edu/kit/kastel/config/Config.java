package edu.kit.kastel.config;

import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.HumanPlayer;

/**
 * Record for the Configuration with which the game is run.
 *
 * @param seed    represents the unique identity of the game.
 * @param verbose represents the compactness of the game.
 * @param player  represents the Human Player.
 * @param enemy   represents the AI opponent.
 * @author uwsfc
 */
public record Config(long seed, boolean verbose, HumanPlayer player, AIPlayer enemy) {
}
