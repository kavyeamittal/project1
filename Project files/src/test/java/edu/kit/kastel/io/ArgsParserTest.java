package edu.kit.kastel.io;

import edu.kit.kastel.config.Args;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgsParserTest {

    @Test
    void parseAcceptsRequiredArgumentsWithSingleDeck() {
        String[] args = {
                "seed=42",
                "units=src/units.txt",
                "deck=src/deck.txt",
                "verbosity=compact"
        };

        Args parsed = ArgsParser.parse(args);

        assertEquals(42L, parsed.seed());
        assertEquals("src/units.txt", parsed.units().toString());
        assertEquals("src/deck.txt", parsed.playerDeck().toString());
        assertEquals("src/deck.txt", parsed.enemyDeck().toString());
        assertEquals("compact", parsed.verbosity());
    }

    @Test
    void parseAcceptsSeparateDecks() {
        String[] args = {
                "seed=-123",
                "units=src/units.txt",
                "deck1=src/deck-player.txt",
                "deck2=src/deck-enemy.txt"
        };

        Args parsed = ArgsParser.parse(args);

        assertEquals("src/deck-player.txt", parsed.playerDeck().toString());
        assertEquals("src/deck-enemy.txt", parsed.enemyDeck().toString());
        assertEquals("all", parsed.verbosity());
    }

    @Test
    void parseRejectsMissingSeed() {
        String[] args = {
                "units=src/units.txt",
                "deck=src/deck.txt"
        };

        assertThrows(IllegalArgumentException.class, () -> ArgsParser.parse(args));
    }

    @Test
    void parseRejectsInvalidVerbosity() {
        String[] args = {
                "seed=1",
                "units=src/units.txt",
                "deck=src/deck.txt",
                "verbosity=verbose"
        };

        assertThrows(IllegalArgumentException.class, () -> ArgsParser.parse(args));
    }

    @Test
    void parseRejectsDeckCombinedWithDeck1OrDeck2() {
        String[] args = {
                "seed=1",
                "units=src/units.txt",
                "deck=src/deck.txt",
                "deck1=src/deck-player.txt",
                "deck2=src/deck-enemy.txt"
        };

        assertThrows(IllegalArgumentException.class, () -> ArgsParser.parse(args));
    }

    @Test
    void parseRejectsDuplicateKeys() {
        String[] args = {
                "seed=1",
                "seed=2",
                "units=src/units.txt",
                "deck=src/deck.txt"
        };

        assertThrows(IllegalArgumentException.class, () -> ArgsParser.parse(args));
    }
}
