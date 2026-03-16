package edu.kit.kastel.engine;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Occupant;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;

/**
 * Class to handle unit placement logic for a player.
 *
 * @author uwsfc
 */
public class PlacementHandler {

    private final Board board;
    private final Player currentPlayer;
    private Position selectedPos;

    /**
     * Creates a PlacementHandler.
     *
     * @param board         the game board.
     * @param currentPlayer the currently active player.
     * @param selectedPos   the currently selected position.
     */
    public PlacementHandler(Board board, Player currentPlayer, Position selectedPos) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.selectedPos = selectedPos;
    }

    /**
     * Method to validate and place units from hand onto the selected field.
     *
     * @param handIndices 1-based indices of units to place.
     * @throws GameException if placement is invalid.
     */
    public void placeFromHand(int[] handIndices) throws GameException {
        validatePlacement(handIndices);
        Unit[] toPlace = collectUnits(handIndices);
        removeFromHand(handIndices);
        for (Unit unit : toPlace) {
            placeSingleUnit(unit);
        }
    }

    private void validatePlacement(int[] handIndices) throws GameException {
        if (selectedPos == null) {
            throw new GameException("ERROR: Position is null.");
        }
        Position kingPos = board.findKing(currentPlayer);
        if (kingPos == null) {
            throw new GameException("ERROR: King position is null.");
        }
        int dc = Math.abs(selectedPos.column() - kingPos.column());
        int dr = Math.abs(selectedPos.row() - kingPos.row());
        if (dc > 1 || dr > 1 || (dc == 0 && dr == 0)) {
            throw new GameException("ERROR: Unit can only be placed adjacent and diagonally to the Farmer King.");
        }
        Occupant targetOccupant = board.getPos(selectedPos);
        if (targetOccupant instanceof UnitOnBoard u && u.getOwner() != currentPlayer) {
            throw new GameException("ERROR: Target field is occupied by an enemy unit.");
        }
        if (targetOccupant instanceof FarmerKing k && k.owner() != currentPlayer) {
            throw new GameException("ERROR: Target field is occupied by the enemy Farmer King.");
        }
        validateIndices(handIndices);
    }

    private void validateIndices(int[] handIndices) throws GameException {
        for (int i = 0; i < handIndices.length; i++) {
            int index = handIndices[i] - 1;
            if (index < 0 || index >= currentPlayer.getHand().size()) {
                throw new GameException("ERROR: Index " + handIndices[i] + " is out of range.");
            }
            for (int j = i + 1; j < handIndices.length; j++) {
                if (handIndices[i] == handIndices[j]) {
                    throw new GameException("ERROR: Duplicate index " + handIndices[i] + ".");
                }
            }
        }
    }

    private Unit[] collectUnits(int[] handIndices) {
        Unit[] toPlace = new Unit[handIndices.length];
        for (int i = 0; i < handIndices.length; i++) {
            toPlace[i] = currentPlayer.getHand().get(handIndices[i] - 1);
        }
        return toPlace;
    }

    private void removeFromHand(int[] handIndices) {
        int[] sorted = handIndices.clone();
        java.util.Arrays.sort(sorted);
        for (int i = sorted.length - 1; i >= 0; i--) {
            currentPlayer.getHand().remove(sorted[i] - 1);
        }
    }

    private void placeSingleUnit(Unit unit) {
        System.out.println(currentPlayer.getName() + " places "
                + unit.getDisplayName() + " on " + selectedPos + ".");
        Occupant existing = board.getPos(selectedPos);
        if (existing == null) {
            board.place(selectedPos, new UnitOnBoard(currentPlayer, unit));
        } else if (existing instanceof UnitOnBoard ownUnit) {
            handleMergeOnPlace(unit, ownUnit);
        }
        if (board.countUnits(currentPlayer) > 5) {
            board.remove(selectedPos);
        }
    }

    private void handleMergeOnPlace(Unit unit, UnitOnBoard ownUnit) {
        System.out.println(unit.getDisplayName()
                + " and " + ownUnit.getUnit().getDisplayName()
                + " on " + selectedPos + " join forces!");
        Unit merged = Unit.tryMerge(unit, ownUnit.getUnit());
        if (merged != null) {
            ownUnit.setUnit(merged);
            System.out.println("Success!");
        } else {
            board.remove(selectedPos);
            System.out.println("Union failed. " + ownUnit.getUnit().getDisplayName() + " was eliminated.");
            board.place(selectedPos, new UnitOnBoard(currentPlayer, unit));
        }
    }
}