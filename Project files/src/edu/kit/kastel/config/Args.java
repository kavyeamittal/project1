package edu.kit.kastel.config;

import java.nio.file.Path;

/**
 * Record to store the value required at the start of the game.
 * @param seed       represents the different number for each separate game.
 * @param board      represents the 7x7 grid.
 * @param units      represents the main playing unit.
 * @param playerDeck represents the deck of the Human Player.
 * @param enemyDeck  represents the deck of the Enemy.
 * @param verbosity  represents the size of the board.
 * @param playerName represents the name of the Human Player.
 * @param enemyName  represents the name of the Enemy.
 * @author uwsfc
 */
public record Args(long seed, Path board, Path units, Path playerDeck, Path enemyDeck,
                   String verbosity, String playerName, String enemyName) {
}
