package edu.kit.kastel.engine;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Player;

/**
 * Class to render the game state summary.
 *
 * @author uwsfc
 */
public class StateRenderer {

    private static final int TOTAL_WIDTH = 31;
    private static final int MAX_DECK = 40;
    private static final int MAX_BOARD = 5;
    private static final int MAX_LP = 8000;

    /**
     * Method to print the full state summary for both players.
     *
     * @param player the human player.
     * @param enemy  the AI enemy.
     * @param board  the game board.
     */
    public void printState(Player player, Player enemy, Board board) {
        printStateLine(player.getName(), enemy.getName());
        printStateLine(player.getLife() + "/" + MAX_LP + " LP", enemy.getLife() + "/" + MAX_LP + " LP");
        printStateLine("DC: " + player.getDeck().size() + "/" + MAX_DECK,
                "DC: " + enemy.getDeck().size() + "/" + MAX_DECK);
        printStateLine("BC: " + board.countUnits(player) + "/" + MAX_BOARD,
                "BC: " + board.countUnits(enemy) + "/" + MAX_BOARD);
    }

    private void printStateLine(String left, String right) {
        int spaces = TOTAL_WIDTH - left.length() - right.length();
        if (spaces < 1) {
            spaces = 1;
        }
        System.out.println("  " + left + " ".repeat(spaces) + right);
    }
}