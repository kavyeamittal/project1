package edu.kit.kastel.engine;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.config.Config;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementAndMoveTest {

    @Test
    void placementOnAdjacentFieldConsumesHandCard() throws GameException {
        HumanPlayer player = human("Player");
        Board board = boardWithKings(player, ai("Enemy"));
        player.getHand().add(new Unit("Daisy", "Farmer", 300, 500));

        new PlacementHandler(board, player, Position.parse("D2")).placeFromHand(new int[]{1});

        assertEquals(0, player.getHand().size());
        assertNotNull(board.getPos(Position.parse("D2")));
        assertEquals(1, board.countUnits(player));
    }

    @Test
    void placementRejectsDuplicateIndices() {
        HumanPlayer player = human("Player");
        Board board = boardWithKings(player, ai("Enemy"));
        player.getHand().add(new Unit("A", "Farmer", 100, 100));
        player.getHand().add(new Unit("B", "Farmer", 200, 200));

        assertThrows(GameException.class,
                () -> new PlacementHandler(board, player, Position.parse("D2")).placeFromHand(new int[]{1, 1}));
        assertEquals(2, player.getHand().size());
        assertNull(board.getPos(Position.parse("D2")));
    }

    @Test
    void placementRejectsEnemyOccupiedTargetField() {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        board.place(Position.parse("D2"), new UnitOnBoard(enemy, new Unit("Seed", "Farmer", 2500, 2300)));
        player.getHand().add(new Unit("Daisy", "Farmer", 300, 500));

        assertThrows(GameException.class,
                () -> new PlacementHandler(board, player, Position.parse("D2")).placeFromHand(new int[]{1}));
        assertEquals(1, player.getHand().size());
    }

    @Test
    void placementRemovesNewUnitWhenBoardCountExceedsFive() throws GameException {
        HumanPlayer player = human("Player");
        Board board = boardWithKings(player, ai("Enemy"));
        board.place(Position.parse("A1"), new UnitOnBoard(player, new Unit("A", "Farmer", 100, 100)));
        board.place(Position.parse("B1"), new UnitOnBoard(player, new Unit("B", "Farmer", 100, 100)));
        board.place(Position.parse("C1"), new UnitOnBoard(player, new Unit("C", "Farmer", 100, 100)));
        board.place(Position.parse("E1"), new UnitOnBoard(player, new Unit("E", "Farmer", 100, 100)));
        board.place(Position.parse("F1"), new UnitOnBoard(player, new Unit("F", "Farmer", 100, 100)));
        player.getHand().add(new Unit("G", "Farmer", 100, 100));

        new PlacementHandler(board, player, Position.parse("D2")).placeFromHand(new int[]{1});

        assertEquals(5, board.countUnits(player));
        assertNull(board.getPos(Position.parse("D2")));
    }

    @Test
    void moveEnPlaceMarksUnitAsMoved() throws GameException {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position selected = Position.parse("D3");
        UnitOnBoard unit = new UnitOnBoard(player, new Unit("Daisy", "Farmer", 300, 500));
        board.place(selected, unit);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, selected);
        moveHandler.handleMoveTarget(unit, selected);

        assertTrue(unit.hasMovedThisTurn());
        assertEquals(selected, moveHandler.getSelectedPos());
    }

    @Test
    void farmerKingCannotMoveOntoEnemyUnit() {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("D1");
        board.place(Position.parse("D2"), new UnitOnBoard(enemy, new Unit("Seed", "Farmer", 2500, 2300)));
        FarmerKing king = (FarmerKing) board.getPos(from);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        assertThrows(GameException.class, () -> moveHandler.handleKingMove(king, Position.parse("D2")));
    }

    @Test
    void farmerKingCannotMoveOntoEnemyKing() {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("D1");
        FarmerKing king = (FarmerKing) board.getPos(from);
        board.remove(Position.parse("D7"));
        board.place(Position.parse("D2"), new FarmerKing(enemy));

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        assertThrows(GameException.class, () -> moveHandler.handleKingMove(king, Position.parse("D2")));
    }

    @Test
    void farmerKingMovingOntoOwnUnitEliminatesThatUnit() throws GameException {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("D1");
        Position to = Position.parse("D2");
        board.place(to, new UnitOnBoard(player, new Unit("Daisy", "Farmer", 300, 500)));
        FarmerKing king = (FarmerKing) board.getPos(from);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        moveHandler.handleKingMove(king, to);

        assertEquals(0, board.countUnits(player));
        assertEquals(king, board.getPos(to));
        assertNull(board.getPos(from));
    }

    @Test
    void farmerKingEnPlaceMarksMovedAndKeepsPosition() throws GameException {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("D1");
        FarmerKing king = (FarmerKing) board.getPos(from);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        moveHandler.handleKingMove(king, from);

        assertTrue(king.hasMovedThisTurn());
        assertEquals(from, moveHandler.getSelectedPos());
        assertEquals(king, board.getPos(from));
    }

    @Test
    void normalUnitDiagonalMoveIsRejectedViaGameRules() throws GameException {
        HumanPlayer player = new HumanPlayer("Player", new Deck(buildFortyCards("P")), new Hand());
        AIPlayer enemy = new AIPlayer("Enemy", new Deck(buildFortyCards("E")), new Hand());
        Game game = new Game(new Config(7L, true, player, enemy));
        game.initialize();
        game.select(Position.parse("D2"));
        game.placeFromHand(new int[]{1});
        game.select(Position.parse("D2"));

        assertThrows(GameException.class, () -> game.moveSelectedTo(Position.parse("E3")));
    }

    @Test
    void standardDuelAttackerWinsAndDealsDifferenceDamage() throws GameException {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("D3");
        Position to = Position.parse("D4");
        UnitOnBoard attacker = new UnitOnBoard(player, new Unit("A", "Farmer", 1200, 500));
        UnitOnBoard defender = new UnitOnBoard(enemy, new Unit("B", "Farmer", 900, 900));
        board.place(from, attacker);
        board.place(to, defender);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        moveHandler.handleMoveTarget(attacker, to);

        assertEquals(7700, enemy.getLife());
        assertEquals(to, moveHandler.getSelectedPos());
        assertEquals(attacker, board.getPos(to));
        assertNull(board.getPos(from));
    }

    @Test
    void blockedDefenderWithHigherDefenceDamagesAttackerTeamAndStaysPut() throws GameException {
        HumanPlayer player = human("Player");
        AIPlayer enemy = ai("Enemy");
        Board board = boardWithKings(player, enemy);
        Position from = Position.parse("C3");
        Position to = Position.parse("C4");
        UnitOnBoard attacker = new UnitOnBoard(player, new Unit("A", "Farmer", 700, 500));
        UnitOnBoard defender = new UnitOnBoard(enemy, new Unit("B", "Farmer", 600, 1000));
        defender.toggleBlocked();
        board.place(from, attacker);
        board.place(to, defender);

        MoveHandler moveHandler = new MoveHandler(board, player, enemy, from);
        moveHandler.handleMoveTarget(attacker, to);

        assertEquals(7700, player.getLife());
        assertEquals(from, moveHandler.getSelectedPos());
        assertEquals(attacker, board.getPos(from));
        assertEquals(defender, board.getPos(to));
    }

    private static Board boardWithKings(Player human, Player enemy) {
        Board board = new Board();
        board.place(Position.parse("D1"), new FarmerKing(human));
        board.place(Position.parse("D7"), new FarmerKing(enemy));
        return board;
    }

    private static HumanPlayer human(String name) {
        return new HumanPlayer(name, new Deck(List.of()), new Hand());
    }

    private static AIPlayer ai(String name) {
        return new AIPlayer(name, new Deck(List.of()), new Hand());
    }

    private static List<Unit> buildFortyCards(String prefix) {
        List<Unit> cards = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            cards.add(new Unit(prefix + i, "Farmer", 100 + i, 100 + i));
        }
        return cards;
    }
}
