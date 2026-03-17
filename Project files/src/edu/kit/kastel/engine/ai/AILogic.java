package edu.kit.kastel.engine.ai;

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
 * Contains AI decision logic for unit placement, movement, and discarding.
 *
 * @author uwsfc
 */
public class AILogic {
    private final Board board;
    private final Player aiPlayer;
    private final Player humanPlayer;
    private final RandomHelper rnd;

    /**
     * Constructor for the AI Logic Instance.
     *
     * @param board       the game board.
     * @param aiPlayer    the AI player.
     * @param humanPlayer the human player.
     * @param rnd         the shared random generator.
     */
    public AILogic(Board board, Player aiPlayer, Player humanPlayer, Random rnd) {
        this.board = board;
        this.aiPlayer = aiPlayer;
        this.humanPlayer = humanPlayer;
        this.rnd = new RandomHelper(rnd);
    }

    /**
     * Method to choose best move for the king.
     *
     * @param kingPos current position of the king
     * @return the best position
     */
    public Position chooseBestKingMove(Position kingPos) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {0, 0}};
        int bestScore = Integer.MIN_VALUE;
        List<Position> bestPositions = new ArrayList<>();

        for (int[] dir : directions) {
            int col = kingPos.column() + dir[0];
            int row = kingPos.row() + dir[1];
            if (col < 0 || col > 6 || row < 0 || row > 6) {
                continue;
            }
            Position candidate = Position.of(col, row);
            Occupant occ = board.getPos(candidate);
            if (occ instanceof UnitOnBoard u && u.getOwner() != aiPlayer) {
                continue;
            }
            if (occ instanceof FarmerKing k && k.owner() != aiPlayer) {
                continue;
            }
            int distance = (dir[0] == 0 && dir[1] == 0) ? 0 : 1;
            int fellows = countSurrounding8(candidate, aiPlayer, false);
            int enemies = countSurrounding8(candidate, humanPlayer, true);
            int fellowPresent = (occ instanceof UnitOnBoard) ? 1 : 0;
            int score = fellows - 2 * enemies - distance - 3 * fellowPresent;
            if (score > bestScore) {
                bestScore = score;
                bestPositions.clear();
                bestPositions.add(candidate);
            } else if (score == bestScore) {
                bestPositions.add(candidate);
            }
        }
        if (bestPositions.isEmpty()) {
            return null;
        }
        if (bestPositions.size() == 1) {
            return bestPositions.getFirst();
        }
        return rnd.weightedRandomPositionDirectional(bestPositions, kingPos);
    }

    /**
     * Method to choose the best field adjacent to the AI king to place a unit on.
     *
     * @return the chosen position, or null if no valid field is available.
     */
    public Position choosePlacementField() {
        Position kingPos = board.findKing(aiPlayer);
        if (kingPos == null) {
            return null;
        }
        Position enemyKingPos = board.findKing(humanPlayer);
        List<Position> candidates = getAdjacentFields(kingPos);
        if (candidates.isEmpty()) {
            return null;
        }
        int bestScore = Integer.MIN_VALUE;
        List<Position> bestPositions = new ArrayList<>();
        for (Position candidate : candidates) {
            Occupant occ = board.getPos(candidate);
            if (occ instanceof UnitOnBoard unitOnBoard && unitOnBoard.getOwner() != aiPlayer) {
                continue;
            }
            if (occ instanceof FarmerKing king && king.owner() != aiPlayer) {
                continue;
            }
            int steps = stepsToPosition(candidate, enemyKingPos);
            int enemies = countSurrounding4(candidate, humanPlayer);
            int fellows = countSurrounding4(candidate, aiPlayer);
            int score = (-steps + (2 * enemies)) - fellows;
            if (score > bestScore) {
                bestScore = score;
                bestPositions.clear();
                bestPositions.add(candidate);
            } else if (score == bestScore) {
                bestPositions.add(candidate);
            }
        }
        if (bestPositions.isEmpty()) {
            return null;
        }
        if (bestPositions.size() == 1) {
            return bestPositions.getFirst();
        }
        return rnd.weightedRandomPositionClockwise(bestPositions, kingPos);
    }

    /**
     * Method to choose a unit from the AI's hand using weighted random selection. ATK values are used as weights.
     *
     * @return the 0-based index of the chosen unit, or -1 if hand is empty.
     */
    public int chooseUnitFromHand() {
        int size = aiPlayer.getHand().size();
        if (size == 0) {
            return -1;
        }
        int[] weights = new int[size];
        for (int i = 0; i < size; i++) {
            weights[i] = aiPlayer.getHand().get(i).getAttack();
        }
        return rnd.weightedRandomIndex(weights);
    }

    /**
     * Method to choose a unit to discard using reverse weighted random selection.
     *
     * @return the 0-based index of the unit to discard.
     */
    public int chooseDiscardIndex() {
        int size = aiPlayer.getHand().size();
        int[] weights = new int[size];
        for (int i = 0; i < size; i++) {
            Unit unit = aiPlayer.getHand().get(i);
            weights[i] = unit.getAttack() + unit.getDefence();
        }
        return rnd.reverseWeightedRandomIndex(weights);
    }

    /**
     * Scores a potential move to the given target position.
     *
     * @param unit         the moving unit.
     * @param from         the unit's current position.
     * @param to           the target position.
     * @param enemyKingPos the enemy king's position.
     * @return the score for this move.
     */
    private int scoreMove(UnitOnBoard unit, Position from, Position to, Position enemyKingPos) {
        Occupant target = board.getPos(to);
        int atkA = unit.getUnit().getAttack();
        int defA = unit.getUnit().getDefence();
        if (target == null) {
            int steps = stepsToPosition(to, enemyKingPos);
            int enemies = countSurrounding4(to, humanPlayer);
            return 10 * steps - enemies;
        }
        if (target instanceof FarmerKing king) {
            return king.owner() == aiPlayer ? -defA : atkA;
        }
        if (target instanceof UnitOnBoard defender) {
            return scoreAgainstDefender(unit, defender, atkA, defA);
        }
        return 0;
    }

    /**
     * Scores a move against a defending unit.
     *
     * @param unit     the attacking unit.
     * @param defender the defending unit.
     * @param atkA     attacker's ATK value.
     * @param defA     attacker's DEF value.
     * @return the score.
     */
    private int scoreAgainstDefender(UnitOnBoard unit, UnitOnBoard defender, int atkA, int defA) {
        if (defender.getOwner() == aiPlayer) {
            Unit merged = Unit.tryMerge(unit.getUnit(), defender.getUnit());
            if (merged != null) {
                return merged.getAttack() + merged.getDefence() - atkA - defA;
            }
            return -defender.getUnit().getAttack() - defender.getUnit().getDefence();
        }
        if (!defender.isFlipped()) {
            return atkA - 500;
        }
        if (defender.isBlocked()) {
            return atkA - defender.getUnit().getDefence();
        }
        return 2 * (atkA - defender.getUnit().getAttack());
    }

    /**
     * Scores initiating a blockade for the given unit.
     *
     * @param unit the unit considering a blockade.
     * @param pos  the unit's position.
     * @return the blockade score.
     */
    private int scoreBlock(UnitOnBoard unit, Position pos) {
        int defA = unit.getUnit().getDefence();
        int maxEnemyAtk = getMaxEnemyAtkInLine(pos);
        return Math.max(1, (defA - maxEnemyAtk) / 100);
    }

    /**
     * Scores an en-place move for the given unit.
     *
     * @param unit the unit considering en-place.
     * @param pos  the unit's position.
     * @return the en-place score.
     */
    private int scoreEnPlace(UnitOnBoard unit, Position pos) {
        int atkA = unit.getUnit().getAttack();
        int maxEnemyAtk = getMaxEnemyAtkInLine(pos);
        return Math.max(0, (atkA - maxEnemyAtk) / 100);
    }

    /**
     * Method to return the maximum ATK value of enemy units in straight lines from the position.
     *
     * @param pos the position to check from.
     * @return the maximum enemy ATK value in line.
     */
    private int getMaxEnemyAtkInLine(Position pos) {
        int max = 0;
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : dirs) {
            int col = pos.column() + dir[0];
            int row = pos.row() + dir[1];
            while (col >= 0 && col <= 6 && row >= 0 && row <= 6) {
                Occupant occ = board.getAt(row, col);
                if (occ instanceof UnitOnBoard u && u.getOwner() == humanPlayer) {
                    max = Math.max(max, u.getUnit().getAttack());
                }
                col += dir[0];
                row += dir[1];
            }
        }
        return max;
    }

    /**
     * Method to return all fields adjacent (including diagonally) to the given position.
     *
     * @param kingPos the center position.
     * @return list of adjacent positions within board bounds.
     */
    private List<Position> getAdjacentFields(Position kingPos) {
        List<Position> result = new ArrayList<>();
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) {
                    continue;
                }
                int col = kingPos.column() + dc;
                int row = kingPos.row() + dr;
                if (col >= 0 && col <= 6 && row >= 0 && row <= 6) {
                    result.add(Position.of(col, row));
                }
            }
        }
        return result;
    }

    /**
     * Returns the Manhattan distance between two positions.
     *
     * @param from the starting position.
     * @param to   the target position.
     * @return the Manhattan distance, or 0 if target is null.
     */
    private int stepsToPosition(Position from, Position to) {
        if (to == null) {
            return 0;
        }
        return Math.abs(from.column() - to.column()) + Math.abs(from.row() - to.row());
    }

    /**
     * Counts occupants belonging to the given player in the four orthogonal directions.
     *
     * @param pos   the center position.
     * @param owner the player to count for.
     * @return the count of surrounding occupants.
     */
    private int countSurrounding4(Position pos, Player owner) {
        int count = 0;
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : dirs) {
            Position posToCheck = Position.of(pos.column() + dir[0], pos.row() + dir[1]);
            if (belongsToPlayer(posToCheck, owner, true)) {
                count++;
            }
        }
        return count;
    }

    private int countSurrounding8(Position pos, Player owner, boolean includeFarmerKing) {
        int count = 0;
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) {
                    continue;
                }
                Position posToCheck = Position.of(pos.column() + dc, pos.row() + dr);
                if (belongsToPlayer(posToCheck, owner, includeFarmerKing)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean belongsToPlayer(Position position, Player player, boolean includeFarmerKing) {
        if (position.column() < 0 || position.column() > 6 || position.row() < 0 || position.row() > 6) {
            return false;
        }
        Occupant occ = board.getPos(position);
        return (occ instanceof UnitOnBoard u && u.getOwner() == player)
                || (occ instanceof FarmerKing k && k.owner() == player && includeFarmerKing);
    }

    /**
     * Computes scores for all possible moves of the unit at the given position.
     *
     * @param unitPosition the position of the unit to score moves for.
     * @return array of six scores, with 0 indicating an invalid move.
     */
    public int[] getMoveScores(Position unitPosition) {
        // Order: (oben, rechts, unten, links, blockieren, Bewegung en place)
        Occupant occ = board.getPos(unitPosition);
        if (!(occ instanceof UnitOnBoard unit)) {
            return new int[6];
        }
        Position enemyKingPos = board.findKing(humanPlayer);
        int[] dirCols = {0, 1, 0, -1};
        int[] dirRows = {1, 0, -1, 0};
        int[] scores = new int[6];
        for (int i = 0; i < 4; i++) {
            int col = unitPosition.column() + dirCols[i];
            int row = unitPosition.row() + dirRows[i];
            if (col < 0 || col > 6 || row < 0 || row > 6) {
                scores[i] = 0;
                continue;
            }
            Position target = Position.of(col, row);
            scores[i] = scoreMove(unit, unitPosition, target, enemyKingPos);
        }
        scores[4] = scoreBlock(unit, unitPosition);
        scores[5] = scoreEnPlace(unit, unitPosition);
        return scores;
    }

    /**
     * Chooses a move for the unit at the given position using weighted random selection over the given scores.
     *
     * @param scores       the move scores as returned by {@link #getMoveScores(Position)}.
     * @param unitPosition the current position of the unit.
     * @return the target position, null for blockade, or unitPosition for en-place.
     */
    public Position chooseUnitMove(int[] scores, Position unitPosition) {
        boolean hasPositiveMovement = false;
        for (int i : new int[]{0, 1, 2, 3, 5}) {
            if (scores[i] > 0) {
                hasPositiveMovement = true;
                break;
            }
        }
        if (!hasPositiveMovement) {
            return null;
        }
        int chosen = rnd.weightedRandomIndex(scores);
        if (chosen == 4) {
            return null;
        }
        if (chosen == 5) {
            return unitPosition;
        }
        int[] dirCols = {0, 1, 0, -1};
        int[] dirRows = {1, 0, -1, 0};
        return Position.of(unitPosition.column() + dirCols[chosen], unitPosition.row() + dirRows[chosen]);
    }
}