package edu.kit.kastel.model;

/**
 * The Class responsible for the 7x7 grid, on which the game is played.
 *
 * @author uwsfc
 */
public class Board {

    private final Occupant[][] grid;

    /**
     * Constructor to make the grid a 7x7 Matrix of Occupants.
     */
    public Board() {
        this.grid = new Occupant[7][7];
    }

    /**
     * Method to get the position of an Occupant.
     *
     * @param pos represents the position to get.
     * @return the grid position
     */
    public Occupant getPos(Position pos) {
        return grid[pos.row()][pos.column()];
    }

    /**
     * Method to check if the position is empty.
     *
     * @param pos represents the position to be checked.
     * @return the boolean value of it is empty or not.
     */
    public boolean isEmpty(Position pos) {
        return getPos(pos) == null;
    }

    /**
     * Method to place an Occupant at a position.
     *
     * @param pos      represents the position to place the Occupant on.
     * @param occupant represents the Occupant which is to be placed.
     */
    public void place(Position pos, Occupant occupant) {
        grid[pos.row()][pos.column()] = occupant;
    }

    /**
     * Method to clear a position from an Occupant.
     *
     * @param pos represents the position to be cleared.
     * @return the position which has been cleared.
     */
    public Occupant remove(Position pos) {
        Occupant old = getPos(pos);
        grid[pos.row()][pos.column()] = null;
        return old;
    }

    /**
     * Method to check the Occupant at a specific row and column.
     *
     * @param col represents the column.
     * @param row represents the row.
     * @return the Occupant at that position.
     */
    public Occupant getAt(int row, int col) {
        return grid[row][col];
    }

    /**
     * Method to return the position of the Farmer King.
     *
     * @param owner represents the Player of the Farmer King.
     * @return the Positon of the Farmer King
     */
    public Position findKing(Player owner) {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Occupant occupant = getAt(row, col);
                if (occupant instanceof FarmerKing king && king.owner() == owner) {
                    return Position.of(col, row);
                }
            }

        }
        return null;
    }

    /**
     * Method to count units of the player.
     *
     * @param owner represents the player.
     * @return an integer of the units of the player.
     */
    public int countUnits(Player owner) {
        int count = 0;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Occupant occ = getAt(row, col);
                if (occ instanceof UnitOnBoard unitOnBoard && unitOnBoard.getOwner() == owner) {
                    count++;
                }

            }

        }
        return count;
    }

    /**
     * Resets the moved-this-turn flag for all units and the Farmer King belonging to the given player.
     *
     * @param owner the player whose pieces should be reset.
     */
    public void resetMovedFlags(Player owner) {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Occupant occ = getAt(row, col);
                if (occ instanceof UnitOnBoard unitOnBoard && unitOnBoard.getOwner() == owner) {
                    unitOnBoard.resetMoved();
                    unitOnBoard.setBlockedThisTurn(false);
                }
                if (occ instanceof FarmerKing king && king.owner() == owner) {
                    king.resetMoved();
                }
            }
        }
    }
}
