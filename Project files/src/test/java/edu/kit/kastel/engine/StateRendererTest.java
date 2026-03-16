package edu.kit.kastel.engine;

import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateRendererTest {

    @Test
    void printStateUsesExpectedSpacingForThirtyOneCharacterContentWidth() {
        HumanPlayer player = new HumanPlayer("Player", new Deck(List.of()), new Hand());
        AIPlayer enemy = new AIPlayer("Enemy", new Deck(List.of()), new Hand());
        Board board = new Board();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            new StateRenderer().printState(player, enemy, board);
        } finally {
            System.setOut(original);
        }

        String[] lines = out.toString().split("\\R");
        // Output lines should align to the board's compact width.
        assertEquals(31, lines[0].length());
        assertEquals(31, lines[1].length());
        assertEquals(31, lines[2].length());
        assertEquals(31, lines[3].length());
    }
}
