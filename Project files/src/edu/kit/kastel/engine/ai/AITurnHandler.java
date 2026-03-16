package edu.kit.kastel.engine.ai;

import edu.kit.kastel.cli.BoardRenderer;
import edu.kit.kastel.cli.ShowHandler;
import edu.kit.kastel.engine.Game;
import edu.kit.kastel.engine.MoveHandler;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Occupant;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles the AI player's turn logic.
 *
 * @author uwsfc
 */
public class AITurnHandler {

    private final Game game;
    private final Board board;
    private final Player aiPlayer;
    private final Player humanPlayer;
    private final Random rnd;
    private final boolean verbose;

    /**
     * Constructor for an AiTurnHandler.
     *
     * @param game        the game instance.
     * @param board       the game board.
     * @param aiPlayer    the AI player.
     * @param humanPlayer the human player.
     * @param rnd         the shared random generator.
     * @param verbose     whether to print separators.
     */
    public AITurnHandler(Game game, Board board, Player aiPlayer, Player humanPlayer,
                         Random rnd, boolean verbose) {
        this.game = game;
        this.board = board;
        this.aiPlayer = aiPlayer;
        this.humanPlayer = humanPlayer;
        this.rnd = rnd;
        this.verbose = verbose;
    }

    /**
     * Runs the full AI turn.
     *
     * @throws GameException if any game rule is violated.
     */
    public void runAiTurn() throws GameException {
        AILogic ai = new AILogic(board, aiPlayer, humanPlayer, rnd);
        moveAiKing(ai);
        placeAiUnit(ai);
        moveAiUnits(ai);
        if (aiPlayer.getHand().size() >= 5) {
            discardAiUnit(ai);
        }
    }

    private void moveAiKing(AILogic ai) throws GameException {
        Position kingPos = board.findKing(aiPlayer);
        if (kingPos == null) {
            return;
        }
        FarmerKing king = (FarmerKing) board.getPos(kingPos);
        Position chosen = ai.chooseBestKingMove(kingPos);
        if (chosen == null) {
            return;
        }
        game.setSelectedPos(kingPos);
        MoveHandler mover = new MoveHandler(board, aiPlayer, humanPlayer, kingPos);
        mover.handleKingMove(king, chosen);
        game.setSelectedPos(mover.getSelectedPos());
        printBoard();
        handleShow(game);
    }

    private void placeAiUnit(AILogic ai) throws GameException {
        Position field = ai.choosePlacementField();
        if (field == null) {
            return;
        }
        int unitIndex = ai.chooseUnitFromHand();
        if (unitIndex < 0) {
            return;
        }
        game.setSelectedPos(field);
        game.placeFromHand(new int[]{unitIndex + 1});
        printBoard();
        handleShow(game);
    }

    private void moveAiUnits(AILogic ai) throws GameException {
        Position[] unitPositions = getUnitPositions(aiPlayer);
        int[][] scores = new int[unitPositions.length][6]; // (oben, rechts, unten, links, blockieren, en place)
        int highestScoreSum = Integer.MIN_VALUE;
        int highestScoreUnitIndex = -1;

        for (int i = 0; i < unitPositions.length; i++) {
            scores[i] = ai.getMoveScores(unitPositions[i]);
            int sum = 0;
            for (int score : scores[i]) {
                sum += score;
            }
            if (sum > highestScoreSum) {
                highestScoreUnitIndex = i;
                highestScoreSum = sum;
            }
        }

        if (highestScoreUnitIndex == -1) {
            return;
        }
        Position unitPos = unitPositions[highestScoreUnitIndex];
        Position target = ai.chooseUnitMove(scores[highestScoreUnitIndex], unitPos);
        game.select(unitPositions[highestScoreUnitIndex]);
        if (target == null) {
            game.blockSelected();
        } else {
            game.moveSelectedTo(target);
        }
        printBoard();
        handleShow(game);
    }

    private void discardAiUnit(AILogic ai) {
        int idx = ai.chooseDiscardIndex();
        Unit discarded = aiPlayer.getHand().remove(idx);
        System.out.println(aiPlayer.getName() + " discarded "
                + discarded.getDisplayName()
                + " (" + discarded.getAttack() + "/" + discarded.getDefence() + ").");
    }

    private Position[] getUnitPositions(Player owner) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Occupant occ = board.getAt(row, col);
                if (occ instanceof UnitOnBoard u && u.getOwner() == owner) {
                    positions.add(Position.of(col, row));
                }
            }
        }
        return positions.toArray(new Position[0]);
    }

    private void printBoard() {
        BoardRenderer.printBoard(board, verbose, aiPlayer, game.getSelectedPos());
    }

    private void handleShow(Game game) {
        ShowHandler.handleShow(game.getPos(), game.getCurrentPlayer());
    }
}