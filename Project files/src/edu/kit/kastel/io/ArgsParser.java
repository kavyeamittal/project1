package edu.kit.kastel.io;

import edu.kit.kastel.config.Args;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class to parse the initial arguments including the unit and deck files.
 *
 * @author uwsfc
 */
public final class ArgsParser {

    /**
     * Empty constructor to initialize the class.
     */
    private ArgsParser() {

    }

    /**
     * The Class which is responsible for the parsing of all the given arguments.
     *
     * @param args represents the program arguments.
     * @return the record of the Initial Arguments given by the game.
     * @throws IllegalArgumentException for invalid arguments.
     */
    public static Args parse(String[] args) {
        Map<String, String> map = toMap(args);

        long seed = parseSeed(map);
        Path unitsFile = getPath(map, "units");

        DeckFiles decks = parseDecks(map);
        Path deck1 = decks.deckFileTeam1;
        Path deck2 = decks.deckFileTeam2;

        Path boardSymbolsFile;
        if (map.containsKey("board")) {
            boardSymbolsFile = Path.of(map.get("board"));
        } else {
            boardSymbolsFile = null;
        }
        String verbosity = map.getOrDefault("verbosity", "all");
        if (!verbosity.equals("all") && !verbosity.equals("compact")) {
            throw new IllegalArgumentException("ERROR: Invalid verbosity value: " + verbosity);
        }
        String team1 = map.get("team1");
        String team2 = map.get("team2");
        if (team1 != null && team1.length() > 14) {
            throw new IllegalArgumentException("ERROR: Team 1 name too long (max 14 chars).");
        }
        if (team2 != null && team2.length() > 14) {
            throw new IllegalArgumentException("ERROR: Team 2 name too long (max 14 chars).");
        }

        return new Args(seed, boardSymbolsFile, unitsFile, deck1, deck2, verbosity, team1, team2);
    }

    private static Map<String, String> toMap(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String arg : args) {
            int equal = arg.indexOf("=");
            if (equal <= 0 || equal == arg.length() - 1) {
                throw new IllegalArgumentException("ERROR: Invalid Argument, = used improperly.");
            }

            String key = arg.substring(0, equal).trim();
            String value = arg.substring(equal + 1).trim();

            if (key.isEmpty() || value.isEmpty()) {
                throw new IllegalArgumentException("ERROR: Invalid argument, key or value are empty!");
            }

            if (map.containsKey(key)) {
                throw new IllegalArgumentException("ERROR: Invalid argument, key already used!");
            }

            map.put(key, value);
        }

        return map;
    }

    private static long parseSeed(Map<String, String> map) {
        String fetchSeed = map.get("seed");
        if (fetchSeed == null) {
            throw new IllegalArgumentException("ERROR: Missing argument 'seed'.");
        }
        return Long.parseLong(fetchSeed);
    }

    private static Path getPath(Map<String, String> map, String key) {
        String fetchKey = map.get(key);
        if (fetchKey == null) {
            throw new IllegalArgumentException("ERROR: Missing argument.");
        }
        return Path.of(fetchKey);
    }

    private static DeckFiles parseDecks(Map<String, String> map) {
        boolean hasDeck = map.containsKey("deck");
        boolean hasDeck1 = map.containsKey("deck1");
        boolean hasDeck2 = map.containsKey("deck2");

        if (hasDeck) {
            if (hasDeck1 || hasDeck2) {
                throw new IllegalArgumentException("ERROR: Use either ‘deck', or 'deck1 and deck2'");
            }
            Path deckPath = getPath(map, "deck");
            return new DeckFiles(deckPath, deckPath);
        }

        if (hasDeck1 || hasDeck2) {
            if (!(hasDeck1 && hasDeck2)) {
                throw new IllegalArgumentException("ERROR: If you use 'deck1' or 'deck2', you must provide both.");
            }

            Path deckPath1 = getPath(map, "deck1");
            Path deckPath2 = getPath(map, "deck2");
            return new DeckFiles(deckPath1, deckPath2);
        }
        throw new IllegalArgumentException("ERROR: Missing required argument 'deck' (or 'deck1' and 'deck2').");
    }

    private record DeckFiles(Path deckFileTeam1, Path deckFileTeam2) {
    }
}
