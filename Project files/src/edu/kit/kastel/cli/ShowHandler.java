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

    private static final String MSG_NO_UNIT = "<no unit>";
    private static final String MSG_KING_SUFFIX = "'s Farmer King";
    private static final String MSG_UNKNOWN_TEAM = "??? (Team ";
    private static final String MSG_SUFFIX_PAREN = ")";
    private static final String MSG_UNKNOWN_ATK = "ATK: ???";
    private static final String MSG_UNKNOWN_DEF = "DEF: ???";
    private static final String MSG_TEAM = " (Team ";
    private static final String MSG_ATK = "ATK: ";
    private static final String MSG_DEF = "DEF: ";
    private static final String ERR_UNKNOWN = "ERROR: Unknown Command.";

    private ShowHandler() {
    }

    /**
     * Method to handle show for the Occupant and the player playing.
     * @param occupant represents the occupant of a field.
     * @param currentPlayer represents the current player.
     */
    public static void handleShow(Occupant occupant, Player currentPlayer) {
        switch (occupant) {
            case null -> System.out.println(MSG_NO_UNIT);
            case FarmerKing king -> System.out.println(king.owner().getName() + MSG_KING_SUFFIX);
            case UnitOnBoard unitOnBoard -> {
                boolean isOwn = unitOnBoard.getOwner() == currentPlayer;
                boolean visible = isOwn || unitOnBoard.isFlipped();

                if (!visible) {
                    System.out.println(MSG_UNKNOWN_TEAM + unitOnBoard.getOwner().getName() + MSG_SUFFIX_PAREN);
                    System.out.println(MSG_UNKNOWN_ATK);
                    System.out.println(MSG_UNKNOWN_DEF);
                } else {
                    Unit unit = unitOnBoard.getUnit();
                    System.out.println(unit.getDisplayName() + MSG_TEAM + unitOnBoard.getOwner().getName() + MSG_SUFFIX_PAREN);
                    System.out.println(MSG_ATK + unit.getAttack());
                    System.out.println(MSG_DEF + unit.getDefence());
                }
            }
            default -> System.out.println(ERR_UNKNOWN);
        }
    }
}
