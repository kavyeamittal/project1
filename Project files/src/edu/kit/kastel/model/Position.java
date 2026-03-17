package edu.kit.kastel.model;

import edu.kit.kastel.exceptions.GameException;

/**
 * Represents a position on the 7x7 game board.
 *
 * @author uwsfc
 */
public final class Position {

    private final int column;
    private final int row;

    /**
     * Private constructor — use {@link #of(int, int)} or {@link #parse(String)}.
     *
     * @param column the 0-based column index (0=A .. 6=G).
     * @param row    the 0-based row index (0=1 .. 6=7).
     */
    private Position(int column, int row) {
        this.column = column;
        this.row = row;
    }

    /**
     * Creates a Position from 0-based column and row indices.
     *
     * @param column the 0-based column index (0=A .. 6=G).
     * @param row    the 0-based row index (0=1 .. 6=7).
     * @return the created Position.
     */
    public static Position of(int column, int row) {
        return new Position(column, row);
    }

    /**
     * Parses a position string in the format "A1" to "G7".
     *
     * @param s the position string to parse.
     * @return the parsed Position.
     * @throws GameException if the string is null, wrong length, or contains an invalid column or row character.
     */
    public static Position parse(String s) throws GameException {
        if (s == null || s.trim().length() != 2) {
            throw new GameException("ERROR: Invalid position string: " + s);
        }
        char c = s.charAt(0);
        char r = s.charAt(1);
        if (c < 'A' || c > 'G') {
            throw new GameException("ERROR: Invalid position string: " + s);
        }
        if (r < '1' || r > '7') {
            throw new GameException("ERROR: Invalid position string: " + s);
        }
        return new Position(c - 'A', r - '1');
    }

    /**
     * Returns the position as a string in the format "A1" to "G7".
     *
     * @return the string representation.
     */
    @Override
    public String toString() {
        char c = (char) ('A' + column);
        char r = (char) ('1' + row);
        return "" + c + r;
    }

    /**
     * Returns whether this position equals another object.
     *
     * @param o the object to compare with.
     * @return true if equal.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position other)) {
            return false;
        }
        return this.column == other.column && this.row == other.row;
    }

    /**
     * Returns a hash code for this position.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return 7 * column + row;
    }

    /**
     * Returns the 0-based row index.
     *
     * @return row index (0=row 1 .. 6=row 7).
     */
    public int row() {
        return row;
    }

    /**
     * Returns the 0-based column index.
     *
     * @return column index (0=A .. 6=G).
     */
    public int column() {
        return column;
    }
}