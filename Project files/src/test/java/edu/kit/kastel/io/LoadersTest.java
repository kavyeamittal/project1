package edu.kit.kastel.io;

import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Unit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadersTest {

    @Test
    void unitLoaderParsesValidFile() throws IOException {
        Path file = writeTempFile(
                "Daisy;Farmer;300;500",
                "Shield;Farmer;800;1600"
        );

        List<Unit> units = UnitLoader.loadAndPrint(file, false);

        assertEquals(2, units.size());
        assertEquals("Daisy Farmer", units.get(0).getDisplayName());
        assertEquals(800, units.get(1).getAttack());
    }

    @Test
    void unitLoaderRejectsInvalidLineFormat() throws IOException {
        Path file = writeTempFile(
                "Daisy;Farmer;300",
                "Shield;Farmer;800;1600"
        );

        assertThrows(IllegalArgumentException.class, () -> UnitLoader.loadAndPrint(file, false));
    }

    @Test
    void deckLoaderBuildsDeckWithDeclaredOrderAndCount() throws IOException {
        List<Unit> units = List.of(
                new Unit("A", "Farmer", 100, 100),
                new Unit("B", "Farmer", 200, 200),
                new Unit("C", "Farmer", 300, 300)
        );
        Path deckFile = writeTempFile("10", "20", "10");

        Deck deck = new DeckLoader().loadAndPrint(deckFile, units, false);

        assertEquals(40, deck.size());
        for (int i = 0; i < 10; i++) {
            assertEquals("A Farmer", deck.drawCard().getDisplayName());
        }
        for (int i = 0; i < 20; i++) {
            assertEquals("B Farmer", deck.drawCard().getDisplayName());
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("C Farmer", deck.drawCard().getDisplayName());
        }
    }

    @Test
    void deckLoaderRejectsTotalCardCountNotEqualForty() throws IOException {
        List<Unit> units = List.of(
                new Unit("A", "Farmer", 100, 100),
                new Unit("B", "Farmer", 200, 200)
        );
        Path deckFile = writeTempFile("10", "10");

        assertThrows(IllegalArgumentException.class, () -> new DeckLoader().loadAndPrint(deckFile, units, false));
    }

    @Test
    void deckLoaderRejectsLineCountMismatchWithUnits() throws IOException {
        List<Unit> units = List.of(
                new Unit("A", "Farmer", 100, 100),
                new Unit("B", "Farmer", 200, 200),
                new Unit("C", "Farmer", 300, 300)
        );
        Path deckFile = writeTempFile("20", "20");

        assertThrows(IllegalArgumentException.class, () -> new DeckLoader().loadAndPrint(deckFile, units, false));
    }

    private static Path writeTempFile(String... lines) throws IOException {
        Path path = Files.createTempFile("kastel-tests-", ".txt");
        Files.write(path, List.of(lines));
        path.toFile().deleteOnExit();
        return path;
    }
}
