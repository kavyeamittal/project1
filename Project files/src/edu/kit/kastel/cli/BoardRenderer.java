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
    private static final int SYMBOLS_SIZE = 29;
    private static final int TOP_LEFT_INDEX = 0;
    private static final int TOP_RIGHT_INDEX = 1;
    private static final int BOTTOM_LEFT_INDEX = 2;
    private static final int BOTTOM_RIGHT_INDEX = 3;
    private static final int TOP_MID_INDEX = 4;
    private static final int RIGHT_MID_INDEX = 5;
    private static final int BOTTOM_MID_INDEX = 6;
    private static final int LEFT_MID_INDEX = 7;
    private static final int HORIZONTAL_INDEX = 8;
    private static final int VERTICAL_INDEX = 9;
    private static final int CENTER_INDEX = 10;
    private static final int SELECT_HORIZONTAL_INDEX = 27;
    private static final int SELECT_VERTICAL_INDEX = 28;
    private static final int SELECT_CORNER_INDEX = 23;
    private static final char[] DEFAULT_SYMBOLS = {
        '+', '+', '+', '+', '+', '+', '+', '+', '-', '|', '+',
        '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#',
        '#', '#', '#', '#', '=', 'N'
    };
    private static char[] symbols = DEFAULT_SYMBOLS.clone();

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
                sb.append(ROW_PADDING).append(buildSeparator(row, selectedCol)).append(System.lineSeparator());
            }
            if (row == BOTTOM_BORDER_ROW) {
                continue;
            }
            sb.append(row + INDEX_OFFSET).append(SPACE);
            for (int col = MIN_COL; col <= NUM_COLS; col++) {
                char edge = symbols[VERTICAL_INDEX];
                if (row == selectedRow && (col == selectedCol || col == selectedCol + INDEX_OFFSET)) {
                    edge = symbols[SELECT_VERTICAL_INDEX];
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

    /**
     * Sets the active board symbols using a 29-character symbol definition line.
     *
     * @param symbolLine symbol line from board file.
     */
    public static void setCustomSymbols(String symbolLine) {
        if (symbolLine == null || symbolLine.length() != SYMBOLS_SIZE) {
            symbols = DEFAULT_SYMBOLS.clone();
            return;
        }
        symbols = symbolLine.toCharArray();
    }

    /**
     * Resets board symbols back to the default symbol set.
     */
    public static void resetToDefaultSymbols() {
        symbols = DEFAULT_SYMBOLS.clone();
    }

    private static String buildCell(Board board, int col, int row, Player currentPlayer) {
        Occupant occ = board.getAt(row, col);
        if (occ == null) {
            return EMPTY_CELL;
        }
        return occ.toString(currentPlayer);
    }

    private static String buildSeparator(int row, int selectedCol) {
        StringBuilder sb = new StringBuilder();
        for (int col = MIN_COL; col <= NUM_COLS; col++) {
            char corner = defaultCornerFor(row, col);
            if (col == selectedCol || col == selectedCol + INDEX_OFFSET) {
                corner = symbols[SELECT_CORNER_INDEX];
            }
            char edge = col == selectedCol ? symbols[SELECT_HORIZONTAL_INDEX] : symbols[HORIZONTAL_INDEX];
            sb.append(corner);
            if (col == NUM_COLS) {
                continue;
            }
            sb.append(edge).append(edge).append(edge);
        }
        return sb.toString();
    }

    private static char defaultCornerFor(int row, int col) {
        if (row == START_ROW) {
            if (col == MIN_COL) {
                return symbols[TOP_LEFT_INDEX];
            }
            if (col == NUM_COLS) {
                return symbols[TOP_RIGHT_INDEX];
            }
            return symbols[TOP_MID_INDEX];
        }
        if (row == BOTTOM_BORDER_ROW) {
            if (col == MIN_COL) {
                return symbols[BOTTOM_LEFT_INDEX];
            }
            if (col == NUM_COLS) {
                return symbols[BOTTOM_RIGHT_INDEX];
            }
            return symbols[BOTTOM_MID_INDEX];
        }
        if (col == MIN_COL) {
            return symbols[LEFT_MID_INDEX];
        }
        if (col == NUM_COLS) {
            return symbols[RIGHT_MID_INDEX];
        }
        return symbols[CENTER_INDEX];
    }
}