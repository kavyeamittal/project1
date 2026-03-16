package edu.kit.kastel.cli;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Occupant;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;

/**
 * The Class to display the Board for the Player to see.
 *
 * @author uwsfc
 */
public final class BoardRenderer {
    private static final int NO_SELECTION = -2;
    private static final int START_ROW = 6;
    private static final int NUM_COLS = 7;

    private static final String HEADER = "    A   B   C   D   E   F   G";

    private BoardRenderer() {

    }

    /**
     * Method to print the Board itself.
     *
     * @param board         the board to display.
     * @param verbose       whether to show separators between rows.
     * @param currentPlayer the player whose perspective is used for symbols.
     * @param selectedPos   the currently selected position, or null.
     */
    public static void printBoard(Board board, boolean verbose, Player currentPlayer, Position selectedPos) {
        // Get selected row, -2 indicates not row is selected
        int selectedRow = selectedPos != null ? selectedPos.row() : NO_SELECTION;
        StringBuilder sb = new StringBuilder();

        for (int row = START_ROW; row >= -1; row--) {
            // Get selected column
            int selectedCol = -2;
            if (row == selectedRow || row == selectedRow - 1) {
                selectedCol = selectedPos.column();
            }

            if (verbose) {
                sb.append("  ").append(buildSeparator(selectedCol)).append(System.lineSeparator());
            }
            if (row == -1) {
                continue;
            }
            sb.append(row + 1).append(" ");
            for (int col = 0; col <= NUM_COLS; col++) {
                char edge = '|';
                if (row == selectedRow && (col == selectedCol || col == selectedCol + 1)) {
                    edge = 'N';
                }
                sb.append(edge);
                if (col == NUM_COLS) {
                    continue;
                }
                sb.append(buildCell(board, col, row, currentPlayer));
            }
            sb.append(System.lineSeparator());
        }

        sb.append(HEADER);
        System.out.println(sb);
    }

    private static String buildCell(Board board, int col, int row, Player currentPlayer) {
        Occupant occ = board.getAt(row, col);
        if (occ == null) {
            return "   ";
        }
        return occ.toString(currentPlayer);
    }

    private static String buildSeparator(int selectedCol) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col <= 7; col++) {
            char corner = '+';
            if (col == selectedCol || col == selectedCol + 1) {
                corner = '#';
            }
            char edge = col == selectedCol ? '=' : '-';
            sb.append(corner);
            if (col == 7) {
                continue;
            }
            sb.append(edge).append(edge).append(edge);
        }
        return sb.toString();
    }
}