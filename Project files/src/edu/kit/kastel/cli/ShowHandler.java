package edu.kit.kastel.cli;

import edu.kit.kastel.model.FarmerKing;
import edu.kit.kastel.model.Occupant;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;

/**
 * Utility Class to handle show after printing board.
 * @author uwsfc
 */
public final class ShowHandler {

    private ShowHandler() {
    }

    /**
     * Method to handle show for the Occupant and the player playing.
     * @param occupant represents the occupant of a field.
     * @param currentPlayer represents the current player.
     */
    public static void handleShow(Occupant occupant, Player currentPlayer) {
        switch (occupant) {
            case null -> System.out.println("<no unit>");
            case FarmerKing king -> System.out.println(king.owner().getName() + "'s Farmer King");
            case UnitOnBoard unitOnBoard -> {
                boolean isOwn = unitOnBoard.getOwner() == currentPlayer;
                boolean visible = isOwn || unitOnBoard.isFlipped();

                if (!visible) {
                    System.out.println("??? (Team " + unitOnBoard.getOwner().getName() + ")");
                    System.out.println("ATK: ???");
                    System.out.println("DEF: ???");
                } else {
                    Unit unit = unitOnBoard.getUnit();
                    System.out.println(unit.getDisplayName() + " (Team " + unitOnBoard.getOwner().getName() + ")");
                    System.out.println("ATK: " + unit.getAttack());
                    System.out.println("DEF: " + unit.getDefence());
                }
            }
            default -> System.out.println("ERROR: Unknown Command.");
        }
    }
}
