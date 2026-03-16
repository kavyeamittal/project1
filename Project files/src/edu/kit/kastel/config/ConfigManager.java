package edu.kit.kastel.config;

import edu.kit.kastel.io.ArgsParser;
import edu.kit.kastel.io.DeckLoader;
import edu.kit.kastel.io.UnitLoader;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Unit;

import java.util.List;

/**
 * The Class responsible for the reading of the files and creating the config.
 *
 * @author uwsfc
 */
public class ConfigManager {

    private Args args;
    private Config config;

    /**
     * Empty Constructor to initialize the class.
     */
    public ConfigManager() {
    }

    /**
     * Method to start the process of parsing and reading the arguments.
     *
     * @param programArgs represent the said arguments.
     */
    public void configure(String[] programArgs) {
        parseArguments(programArgs);
        loadFilesAndCreateConfig();
    }

    /**
     * Method to parse the program arguments.
     *
     * @param programArgs represent the said arguments.
     */
    private void parseArguments(String[] programArgs) {
        args = ArgsParser.parse(programArgs);
    }

    /**
     * Method to load the files from the argument.
     */
    private void loadFilesAndCreateConfig() {
        long seed = args.seed();
        boolean verbose = args.verbosity().equals("all");

        List<Unit> playerUnits = UnitLoader.loadAndPrint(args.units(), true);
        List<Unit> enemyUnits = UnitLoader.loadAndPrint(args.units(), false);

        DeckLoader deckLoader = new DeckLoader();
        boolean differentDeck = !args.playerDeck().equals(args.enemyDeck());
        Deck playerDeck = deckLoader.loadAndPrint(args.playerDeck(), playerUnits, true);
        Deck enemyDeck = deckLoader.loadAndPrint(args.enemyDeck(), enemyUnits, differentDeck);

        Hand playerHand = new Hand();
        Hand enemyHand = new Hand();

        String playerName = (args.playerName() == null) ? "Player" : args.playerName();
        String enemyName = (args.enemyName() == null) ? "Enemy" : args.enemyName();

        HumanPlayer player = new HumanPlayer(playerName, playerDeck, playerHand);
        AIPlayer enemy = new AIPlayer(enemyName, enemyDeck, enemyHand);

        config = new Config(seed, verbose, player, enemy);
    }

    /**
     * Getter method to get the config.
     *
     * @return the configuration.
     */
    public Config getConfig() {
        return config;
    }
}