package edu.kit.kastel.io;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Unit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and validates a deck configuration from a file.
 * Each line in the file specifies how many copies of the corresponding unit.
 *
 * @author uwsfc
 */
public final class DeckLoader {

    /**
     * Loads a deck from the given file path, printing each line before processing.
     *
     * @param deckFile path to the deck configuration file.
     * @param units    the list of available units, in the same order as the file lines.
     * @param print    boolean for enabling printing
     * @return the constructed Deck.
     * @throws GameException if the file cannot be read, contains invalid values, or does not result in exactly 40 cards.
     */
    public Deck loadAndPrint(Path deckFile, List<Unit> units, boolean print) throws GameException {
        List<String> lines = readAllLinesOrThrow(deckFile);
        if (print) {
            for (String line : lines) {
                System.out.println(line);
            }
        }

        if (lines.size() != units.size()) {
            throw new GameException("ERROR: Number of units does not match");
        }

        int totalCards = 0;
        List<Integer> cardsCount = new ArrayList<>(lines.size());

        for (String line : lines) {
            int c;
            try {
                c = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                throw new GameException("ERROR: Invalid card number" + line);
            }
            if (c < 0) {
                throw new GameException("ERROR: Negative number in deck file: " + line);
            }
            cardsCount.add(c);
            totalCards += c;
        }

        if (totalCards != 40) {
            throw new GameException("ERROR: Deck must contain exactly 40 cards (was " + totalCards + ").");
        }

        List<Unit> cardsUnit = new ArrayList<>(40);
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            int copies = cardsCount.get(i);
            for (int k = 0; k < copies; k++) {
                cardsUnit.add(unit);
            }
        }

        return new Deck(cardsUnit);
    }

    private List<String> readAllLinesOrThrow(Path deckFile) throws GameException {
        try {
            return Files.readAllLines(deckFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameException("ERROR: Could not read file: " + deckFile);
        }
    }
}