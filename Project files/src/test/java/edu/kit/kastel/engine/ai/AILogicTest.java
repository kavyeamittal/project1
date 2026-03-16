package edu.kit.kastel.engine.ai;

import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AILogicTest {

    @Test
    void chooseUnitMoveReturnsBlockWhenNoPositiveMovementExists() {
        AILogic logic = basicLogic();
        Position unitPos = Position.parse("D6");
        int[] scores = {0, -2, 0, -5, 10, 0};

        Position chosen = logic.chooseUnitMove(scores, unitPos);

        assertNull(chosen);
    }

    @Test
    void choosePlacementFieldReturnsNullWhenNoAdjacentFieldAvailable() {
        HumanPlayer human = new HumanPlayer("Player", new Deck(List.of()), new Hand());
        AIPlayer ai = new AIPlayer("Enemy", new Deck(List.of()), new Hand());
        Board board = new Board();
        board.place(Position.parse("D1"), new FarmerKing(human));
        board.place(Position.parse("D7"), new FarmerKing(ai));
        Position kingPos = Position.parse("D7");
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) {
                    continue;
                }
                int col = kingPos.column() + dc;
                int row = kingPos.row() + dr;
                if (col >= 0 && col <= 6 && row >= 0 && row <= 6) {
                    board.place(Position.of(col, row), new UnitOnBoard(human, new Unit("H", "Farmer", 200, 200)));
                }
            }
        }
        AILogic logic = new AILogic(board, ai, human, new Random(7));

        assertNull(logic.choosePlacementField());
    }

    @Test
    void chooseUnitFromHandUsesHandOrderWithSingleCandidate() {
        HumanPlayer human = new HumanPlayer("Player", new Deck(List.of()), new Hand());
        AIPlayer ai = new AIPlayer("Enemy", new Deck(List.of()), new Hand());
        Board board = new Board();
        board.place(Position.parse("D1"), new FarmerKing(human));
        board.place(Position.parse("D7"), new FarmerKing(ai));
        ai.getHand().add(new Unit("Only", "Farmer", 999, 999));
        AILogic logic = new AILogic(board, ai, human, new Random(1));

        assertEquals(0, logic.chooseUnitFromHand());
    }

    private static AILogic basicLogic() {
        HumanPlayer human = new HumanPlayer("Player", new Deck(List.of()), new Hand());
        AIPlayer ai = new AIPlayer("Enemy", new Deck(List.of()), new Hand());
        Board board = new Board();
        board.place(Position.parse("D1"), new FarmerKing(human));
        board.place(Position.parse("D7"), new FarmerKing(ai));
        return new AILogic(board, ai, human, new Random(42));
    }
}
