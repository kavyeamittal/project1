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
    private static final int BOTTOM_BORDER_ROW = -1;
    private static final int START_ROW = 6;
    private static final int NUM_COLS = 7;
    private static final int MIN_COL = 0;
    private static final int INDEX_OFFSET = 1;
    private static final String HEADER = "    A   B   C   D   E   F   G";
    private static final String ROW_PADDING = "  ";
    private static final String SPACE = " ";
    private static final String EMPTY_CELL = "   ";
    private static final char VERTICAL_EDGE = '|';
    private static final char SELECT_EDGE_VERT = 'N';
    private static final char CORNER = '+';
    private static final char SELECT_CORNER = '#';
    private static final char HORIZONTAL_EDGE = '-';
    private static final char SELECT_EDGE_HORIZ = '=';

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

        for (int row = START_ROW; row >= BOTTOM_BORDER_ROW; row--) {
            // Get selected column
            int selectedCol = NO_SELECTION;
            if (row == selectedRow || row == selectedRow - INDEX_OFFSET) {
                selectedCol = selectedPos.column();
            }

            if (verbose) {
                sb.append(ROW_PADDING).append(buildSeparator(selectedCol)).append(System.lineSeparator());
            }
            if (row == BOTTOM_BORDER_ROW) {
                continue;
            }
            sb.append(row + INDEX_OFFSET).append(SPACE);
            for (int col = MIN_COL; col <= NUM_COLS; col++) {
                char edge = VERTICAL_EDGE;
                if (row == selectedRow && (col == selectedCol || col == selectedCol + INDEX_OFFSET)) {
                    edge = SELECT_EDGE_VERT;
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
            return EMPTY_CELL;
        }
        return occ.toString(currentPlayer);
    }

    private static String buildSeparator(int selectedCol) {
        StringBuilder sb = new StringBuilder();
        for (int col = MIN_COL; col <= NUM_COLS; col++) {
            char corner = CORNER;
            if (col == selectedCol || col == selectedCol + INDEX_OFFSET) {
                corner = SELECT_CORNER;
            }
            char edge = col == selectedCol ? SELECT_EDGE_HORIZ : HORIZONTAL_EDGE;
            sb.append(corner);
            if (col == NUM_COLS) {
                continue;
            }
            sb.append(edge).append(edge).append(edge);
        }
        return sb.toString();
    }
}