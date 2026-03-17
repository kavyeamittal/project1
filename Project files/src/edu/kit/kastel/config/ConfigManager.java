package edu.kit.kastel.config;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.io.ArgsParser;
import edu.kit.kastel.io.DeckLoader;
import edu.kit.kastel.io.UnitLoader;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Unit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The Class responsible for the reading of the files and creating the config.
 *
 * @author uwsfc
 */
public class ConfigManager {
    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final String DEFAULT_ENEMY_NAME = "Enemy";
    private static final String VERBOSITY_ALL = "all";
    private static final String VERBOSITY_COMPACT = "compact";
    private static final String ERR_MISSING_UNIT = "ERROR: Missing argument 'unit'";
    private static final String ERR_NAME_TOO_LONG = "ERROR: Team name too long.";
    private static final String ERR_INVALID_VERBOSITY = "ERROR: Invalid verbosity value: ";
    private static final String ERR_BOARD_READ = "ERROR: Could not read board file: ";
    private static final String ERR_BOARD_FORMAT = "ERROR: Board must contain exactly 29 characters in one line.";
    private static final int MAX_NAME_LENGTH = 14;
    private static final int EXPECTED_BOARD_LINES = 1;
    private static final int EXPECTED_BOARD_LENGTH = 29;

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
     * @throws GameException for invalid exceptions.
     */
    public void configure(String[] programArgs) throws GameException {
        parseArguments(programArgs);
        loadFilesAndCreateConfig();
    }

    /**
     * Method to parse the program arguments.
     *
     * @param programArgs represent the said arguments.
     */
    private void parseArguments(String[] programArgs) throws GameException {
        args = ArgsParser.parse(programArgs);
    }

    /**
     * Method to load the files from the argument.
     */
    private void loadFilesAndCreateConfig() throws GameException {
        long seed = args.seed();


        if (args.board() != null) {
            processBoardFile(args.board());
        }

        if (args.units() == null) {
            throw new GameException(ERR_MISSING_UNIT);
        }
        List<Unit> playerUnits = UnitLoader.loadAndPrint(args.units(), true);
        List<Unit> enemyUnits = UnitLoader.loadAndPrint(args.units(), false);

        DeckLoader deckLoader = new DeckLoader();
        boolean differentDeck = !args.playerDeck().equals(args.enemyDeck());
        Deck playerDeck = deckLoader.loadAndPrint(args.playerDeck(), playerUnits, true);
        Deck enemyDeck = deckLoader.loadAndPrint(args.enemyDeck(), enemyUnits, differentDeck);

        Hand playerHand = new Hand();
        Hand enemyHand = new Hand();

        String playerName = (args.playerName() == null) ? DEFAULT_PLAYER_NAME : args.playerName();
        String enemyName = (args.enemyName() == null) ? DEFAULT_ENEMY_NAME : args.enemyName();

        if (playerName.length() > MAX_NAME_LENGTH || enemyName.length() > MAX_NAME_LENGTH) {
            throw new GameException(ERR_NAME_TOO_LONG);
        }


        String verbosity = args.verbosity();
        if (!verbosity.equals(VERBOSITY_ALL) && !verbosity.equals(VERBOSITY_COMPACT)) {
            throw new GameException(ERR_INVALID_VERBOSITY + verbosity);
        }

        boolean verbose = args.verbosity().equals(VERBOSITY_ALL);
        HumanPlayer player = new HumanPlayer(playerName, playerDeck, playerHand);
        AIPlayer enemy = new AIPlayer(enemyName, enemyDeck, enemyHand);

        config = new Config(seed, verbose, player, enemy);
    }

    private void processBoardFile(Path path) throws GameException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameException(ERR_BOARD_READ + path);
        }

        for (String line : lines) {
            System.out.println(line);
        }

        if (lines.size() != EXPECTED_BOARD_LINES || lines.getFirst().length() != EXPECTED_BOARD_LENGTH) {
            throw new GameException(ERR_BOARD_FORMAT);
        }
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