package edu.kit.kastel.engine;

import edu.kit.kastel.config.Config;
import edu.kit.kastel.engine.ai.AITurnHandler;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Occupant;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;

import java.util.Random;

/**
 * Manages game state, player turns, unit movement, placement, and win conditions.
 *
 * @author uwsfc
 */
public class Game {

    private static final int TOTAL_WIDTH = 31;

    private final Random rnd;
    private final HumanPlayer player;
    private final AIPlayer enemy;
    private final boolean verbose;
    private Board board;
    private Position selectedPos;
    private Player currentPlayer;
    private Player waitingPlayer;
    private boolean hasPlacedThisTurn;
    private boolean gameOver = false;
    private boolean isFirstTurn = true;

    /**
     * Creates a new Game from the given configuration.
     *
     * @param config the game configuration containing seed, players and verbosity.
     */
    public Game(Config config) {
        this.rnd = new Random(config.seed());
        this.player = config.player();
        this.enemy = config.enemy();
        this.verbose = config.verbose();
    }

    /**
     * Initializes the game: shuffles decks, draws opening hands, places Farmer Kings.
     *
     * @throws GameException if the deck does not contain enough units.
     */
    public void initialize() throws GameException {
        player.getDeck().shuffle(rnd);
        enemy.getDeck().shuffle(rnd);
        for (int i = 0; i < 5; i++) {
            Unit playerUnit = player.getDeck().drawCard();
            Unit enemyUnit = enemy.getDeck().drawCard();
            if (playerUnit == null || enemyUnit == null) {
                throw new GameException("ERROR: Not enough units in deck!");
            }
            player.getHand().add(playerUnit);
            enemy.getHand().add(enemyUnit);
        }
        board = new Board();
        board.place(Position.parse("D1"), new FarmerKing(player));
        board.place(Position.parse("D7"), new FarmerKing(enemy));
        currentPlayer = player;
        waitingPlayer = enemy;
    }

    /**
     * Selects a position on the board for subsequent commands.
     *
     * @param pos the position to select.
     * @throws GameException if the position is null.
     */
    public void select(Position pos) throws GameException {
        if (pos == null) {
            throw new GameException("ERROR: Position is null!");
        }
        this.selectedPos = pos;
    }

    /**
     * Returns the currently selected position.
     *
     * @return the selected position, or null if none selected.
     */
    public Position getSelectedPos() {
        return selectedPos;
    }

    /**
     * Sets the currently selected position (used by AI turn handler).
     *
     * @param pos the position to select.
     */
    public void setSelectedPos(Position pos) {
        this.selectedPos = pos;
    }

    /**
     * Returns the occupant at the currently selected position.
     *
     * @return the occupant, or null if no position is selected or field is empty.
     */
    public Occupant getPos() {
        if (selectedPos == null) {
            return null;
        }
        return board.getPos(selectedPos);
    }

    /**
     * Returns the game board.
     *
     * @return the board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the human player.
     *
     * @return the human player.
     */
    public HumanPlayer getPlayer() {
        return player;
    }

    /**
     * Method to place one or more units from the current player's hand onto the selected field.
     *
     * @param handIndices 1-based indices of units to place.
     * @throws GameException if placement is invalid.
     */
    public void placeFromHand(int[] handIndices) throws GameException {
        if (hasPlacedThisTurn) {
            throw new GameException("ERROR: You have already placed a unit this turn.");
        }
        new PlacementHandler(board, currentPlayer, selectedPos).placeFromHand(handIndices);
        hasPlacedThisTurn = true;
    }

    /**
     * Handles simple moves, duels, merges, and king attacks.
     *
     * @param pos the target position.
     * @throws GameException if the move is invalid.
     */
    public void moveSelectedTo(Position pos) throws GameException {
        if (pos == null) {
            throw new GameException("ERROR: Position is null.");
        }
        if (selectedPos == null) {
            throw new GameException("ERROR: No field selected.");
        }
        Occupant occupant = board.getPos(selectedPos);
        if (occupant == null) {
            throw new GameException("ERROR: No unit on selected field.");
        }
        int dc = Math.abs(pos.column() - selectedPos.column());
        int dr = Math.abs(pos.row() - selectedPos.row());
        if (dc + dr > 1) {
            throw new GameException("ERROR: Target is more than one step away.");
        }
        MoveHandler mover = new MoveHandler(board, currentPlayer, waitingPlayer, selectedPos);
        if (occupant instanceof FarmerKing king) {
            if (king.owner() != currentPlayer) {
                throw new GameException("ERROR: You can only move your own King.");
            }
            if (king.hasMovedThisTurn()) {
                throw new GameException("ERROR: Farmer King has already moved.");
            }
            mover.handleKingMove(king, pos);
        } else if (occupant instanceof UnitOnBoard unitToMove) {
            if (unitToMove.getOwner() != currentPlayer) {
                throw new GameException("ERROR: You can only move your own units.");
            }
            if (unitToMove.hasMovedThisTurn()) {
                throw new GameException("ERROR: This unit has already moved this turn.");
            }
            mover.handleMoveTarget(unitToMove, pos);
        } else {
            throw new GameException("ERROR: Cannot move this piece.");
        }
        selectedPos = mover.getSelectedPos();
        checkWinCondition();
    }

    /**
     * Method to print the current game state (LP, DC, BC for both players).
     */
    public void printState() {
        new StateRenderer().printState(player, enemy, board);
    }

    /**
     * Returns whether the game is in verbose (non-compact) mode.
     *
     * @return true if verbose.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Returns the currently active player.
     *
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Method to draw a card for the next player and triggers AI turn if applicable.
     *
     * @param discardIndex 1-based index of card to discard, or -1 for no discard.
     * @throws GameException if the yield conditions are not met.
     */
    public void yieldTurn(int discardIndex) throws GameException {
        Hand hand = currentPlayer.getHand();
        if (hand.size() >= 5 && discardIndex < 0) {
            throw new GameException("ERROR: " + currentPlayer.getName() + "'s hand is full!");
        }
        if (hand.size() < 5 && discardIndex >= 0) {
            throw new GameException("ERROR: You don't need to discard.");
        }
        if (discardIndex >= 0) {
            int index = discardIndex - 1;
            if (index < 0 || index >= hand.size()) {
                throw new GameException("ERROR: Discard index out of range.");
            }
            Unit discarded = hand.remove(index);
            System.out.println(currentPlayer.getName() + " discarded "
                    + discarded.getDisplayName()
                    + " (" + discarded.getAttack() + "/" + discarded.getDefence() + ").");
        }
        Player swap = currentPlayer;
        currentPlayer = waitingPlayer;
        waitingPlayer = swap;
        selectedPos = null;
        hasPlacedThisTurn = false;
        board.resetMovedFlags(currentPlayer);
        if (isFirstTurn) {
            isFirstTurn = false;
        } else {
            Unit drawn = currentPlayer.getDeck().drawCard();
            if (drawn == null) {
                System.out.println("It is " + currentPlayer.getName() + "'s turn!");
                System.out.println(currentPlayer.getName() + " has no cards left in the deck!");
                System.out.println(waitingPlayer.getName() + " wins!");
                gameOver = true;
                return;
            }
            currentPlayer.getHand().add(drawn);
        }
        System.out.println("It is " + currentPlayer.getName() + "'s turn!");
        if (currentPlayer instanceof AIPlayer) {
            new AITurnHandler(this, board, currentPlayer, waitingPlayer, rnd, verbose).runAiTurn();
            yieldTurn(-1);
        }
    }

    /**
     * Method to initiate a blockade for the selected unit. Counts as a move for this turn.
     *
     * @throws GameException if the block is invalid.
     */
    public void blockSelected() throws GameException {
        if (selectedPos == null) {
            throw new GameException("ERROR: No selected position.");
        }
        Occupant occupant = board.getPos(selectedPos);
        if (occupant == null) {
            throw new GameException("ERROR: Selected field is empty.");
        }
        if (!(occupant instanceof UnitOnBoard unitOnBoard)) {
            throw new GameException("ERROR: Only units can be blocked.");
        }
        if (unitOnBoard.getOwner() != currentPlayer) {
            throw new GameException("ERROR: You can only block your own units.");
        }
        if (unitOnBoard.hasMovedThisTurn()) {
            throw new GameException("ERROR: Unit has already moved this turn.");
        }
        if (unitOnBoard.isBlocked() && unitOnBoard.isBlockedThisTurn()) {
            throw new GameException("ERROR: Unit is already blocking this turn.");
        }
        unitOnBoard.setBlocked(true);
        unitOnBoard.setBlockedThisTurn(true);
        unitOnBoard.markMoved();
    }

    /**
     * method to Flip the selected unit to both players.
     *
     * @throws GameException if the flip is invalid.
     */
    public void flipSelected() throws GameException {
        if (selectedPos == null) {
            throw new GameException("ERROR: No field selected.");
        }
        Occupant occ = board.getPos(selectedPos);
        if (!(occ instanceof UnitOnBoard unitOnBoard)) {
            throw new GameException("ERROR: No unit on selected field.");
        }
        if (unitOnBoard.getOwner() != currentPlayer) {
            throw new GameException("ERROR: You can only flip your own units.");
        }
        if (unitOnBoard.hasMovedThisTurn()) {
            throw new GameException("ERROR: Cannot flip a unit that has already moved this turn.");
        }
        if (unitOnBoard.isFlipped()) {
            throw new GameException("ERROR: Unit is already flipped.");
        }
        unitOnBoard.flip();
    }

    private void checkWinCondition() {
        if (currentPlayer.getLife() <= 0) {
            System.out.println(currentPlayer.getName() + "'s life points dropped to 0!");
            System.out.println(waitingPlayer.getName() + " wins!");
            gameOver = true;
        }
        if (waitingPlayer.getLife() <= 0) {
            System.out.println(waitingPlayer.getName() + "'s life points dropped to 0!");
            System.out.println(currentPlayer.getName() + " wins!");
            gameOver = true;
        }
    }

    /**
     * Returns whether the game has ended.
     *
     * @return true if game over.
     */
    public boolean isGameOver() {
        return gameOver;
    }
}