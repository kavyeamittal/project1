package edu.kit.kastel.engine.ai;

import edu.kit.kastel.model.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Helper Class to handle Randomness for the AI.
 *
 * @author uwsfc
 */
public class RandomHelper {

    private final Random rnd;

    /**
     * Constructor of RandomHelper.
     *
     * @param rnd seeded Random
     */
    public RandomHelper(Random rnd) {
        this.rnd = rnd;
    }

    /**
     * Selects an index using weighted random selection. Each index's probability is proportional to its weight.
     *
     * @param weights array of non-negative weights.
     * @return the selected index.
     */
    int weightedRandomIndex(int[] weights) {
        int total = 0;
        for (int w : weights) {
            total += Math.max(0, w);
        }
        if (total == 0) {
            return 0;
        }
        int r = rnd.nextInt(1, total + 1);
        int cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += Math.max(0, weights[i]);
            if (r <= cumulative) {
                return i;
            }
        }
        return weights.length - 1;
    }

    /**
     * Selects an index using reverse weighted random selection. Higher weights result in lower probability of selection.
     *
     * @param weights array of non-negative weights.
     * @return the selected index.
     */
    int reverseWeightedRandomIndex(int[] weights) {
        int max = 0;
        for (int w : weights) {
            max = Math.max(max, w);
        }
        int[] reversed = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            reversed[i] = max - weights[i];
        }
        return weightedRandomIndex(reversed);
    }

    /**
     * Selects a position from the list using weighted random selection, ordering candidates clockwise from above the king.
     *
     * @param positions the candidate positions.
     * @param kingPos   the king's position used as center for ordering.
     * @return the chosen position.
     */
    Position weightedRandomPositionClockwise(List<Position> positions, Position kingPos) {
        List<Position> ordered = orderClockwise(positions, kingPos);
        int[] weights = new int[ordered.size()];
        Arrays.fill(weights, 1);
        int idx = weightedRandomIndex(weights);
        return ordered.get(idx);
    }


    /**
     * Selects a position from the list using weighted random selection, ordering candidates directional from above the king.
     *
     * @param positions the candidate positions.
     * @param kingPos   the king's position used as center for ordering.
     * @return the chosen position.
     */
    Position weightedRandomPositionDirectional(List<Position> positions, Position kingPos) {
        List<Position> ordered = orderDirectional(positions, kingPos);
        int[] weights = new int[ordered.size()];
        Arrays.fill(weights, 1);
        int idx = weightedRandomIndex(weights);
        return ordered.get(idx);
    }

    private List<Position> orderClockwise(List<Position> positions, Position center) {
        List<Position> ordered = new ArrayList<>();
        int[][] clockwise = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        for (int[] dir : clockwise) {
            Position p = Position.of(center.column() + dir[0], center.row() + dir[1]);
            if (positions.contains(p)) {
                ordered.add(p);
            }
        }
        return ordered;
    }

    private List<Position> orderDirectional(List<Position> positions, Position center) {
        List<Position> ordered = new ArrayList<>();
        int[][] directional = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {0, 0}};
        for (int[] dir : directional) {
            Position p = Position.of(center.column() + dir[0], center.row() + dir[1]);
            if (positions.contains(p)) {
                ordered.add(p);
            }
        }
        return ordered;
    }
}
