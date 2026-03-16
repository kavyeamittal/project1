package edu.kit.kastel;

import edu.kit.kastel.cli.CommandLoop;
import edu.kit.kastel.config.ConfigManager;
import edu.kit.kastel.engine.Game;
import edu.kit.kastel.exceptions.GameException;

/**
 * Parses program arguments, initializes the game, and starts the command loop.
 * @author uwsfc
 */
public final class Main {

    /**
     * Private constructor to initialize.
     */
    private Main() {
    }

    /**
     * Configures the game from the given arguments, initializes it,
     * and starts the interactive command loop.
     * @param args command-line arguments in key=value format.
     *             Required: seed, units, and either deck or deck1+deck2.
     */
    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();
        configManager.configure(args);
        Game game = new Game(configManager.getConfig());
        try {
            game.initialize();
        } catch (GameException e) {
            System.out.println(e.getMessage());
            return;
        }
        new CommandLoop().run(game);
    }
}