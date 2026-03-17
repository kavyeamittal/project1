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
 * Class to handle movement logic for units and the Farmer King.
 *
 * @author uwsfc
 */
public class MoveHandler {

    private final Board board;
    private final Player currentPlayer;
    private final Player waitingPlayer;
    private Position selectedPos;

    /**
     * Constructor for the MoveHandler.
     *
     * @param board         the game board.
     * @param currentPlayer the currently active player.
     * @param waitingPlayer the waiting player.
     * @param selectedPos   the currently selected position.
     */
    public MoveHandler(Board board, Player currentPlayer, Player waitingPlayer, Position selectedPos) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
        this.selectedPos = selectedPos;
    }

    /**
     * Returns the selected position after the move (may have changed).
     *
     * @return updated selected position.
     */
    public Position getSelectedPos() {
        return selectedPos;
    }

    /**
     * Method to move a unit to the target position.
     *
     * @param attacker the unit to move.
     * @param pos      the target position.
     * @throws GameException if move is invalid.
     */
    public void handleMoveTarget(UnitOnBoard attacker, Position pos) throws GameException {
        if (pos.equals(selectedPos)) {
            attacker.markMoved();
            System.out.println(attacker.getUnit().getDisplayName() + " moves to " + pos + ".");
            return;
        }
        if (attacker.isBlocked()) {
            attacker.toggleBlocked();
            attacker.setBlockedThisTurn(false);
            System.out.println(attacker.getUnit().getDisplayName() + " no longer blocks.");
        }
        Occupant target = board.getPos(pos);
        if (target == null) {
            board.remove(selectedPos);
            board.place(pos, attacker);
            attacker.markMoved();
            selectedPos = pos;
            System.out.println(attacker.getUnit().getDisplayName() + " moves to " + pos + ".");
        } else if (target instanceof FarmerKing king) {
            handleKingAttack(attacker, king, pos);
        } else if (target instanceof UnitOnBoard defender) {
            if (defender.getOwner() == currentPlayer) {
                handleMerge(attacker, defender, pos);
            } else {
                CombatHandler combat = new CombatHandler(board, currentPlayer, waitingPlayer);
                Position result = combat.handleDuel(attacker, defender, selectedPos, pos);
                selectedPos = result;
            }
        }
    }

    /**
     * Method to move the Farmer King to the target position.
     *
     * @param king the Farmer King.
     * @param pos  the target position.
     * @throws GameException if move is invalid.
     */
    public void handleKingMove(FarmerKing king, Position pos) throws GameException {
        if (pos.equals(selectedPos)) {
            king.markMoved();
            System.out.println("Farmer King moves to " + pos + ".");
            return;
        }
        if (king.owner() != currentPlayer) {
            throw new GameException("ERROR: You can only move your own Farmer King.");
        }
        if (king.hasMovedThisTurn()) {
            throw new GameException("ERROR: Farmer King has already moved this turn.");
        }
        Occupant target = board.getPos(pos);
        if (target instanceof UnitOnBoard u && u.getOwner() != currentPlayer) {
            throw new GameException("ERROR: Farmer King cannot move onto an enemy unit.");
        }
        if (target instanceof FarmerKing k && k.owner() != currentPlayer) {
            throw new GameException("ERROR: Farmer King cannot move onto the enemy King.");
        }
        if (target instanceof UnitOnBoard ownUnit) {
            board.remove(pos);
            System.out.println(ownUnit.getUnit().getDisplayName() + " was eliminated!");
        }
        board.remove(selectedPos);
        board.place(pos, king);
        king.markMoved();
        selectedPos = pos;
        System.out.println("Farmer King moves to " + pos + ".");
    }

    private void handleKingAttack(UnitOnBoard attacker, FarmerKing king, Position pos) throws GameException {
        if (king.owner() == currentPlayer) {
            throw new GameException("ERROR: Cannot move to your own Farmer King.");
        }
        System.out.println(attacker.getUnit().getDisplayName()
                + " (" + attacker.getUnit().getAttack() + "/" + attacker.getUnit().getDefence()
                + ") attacks Farmer King on " + pos + "!");
        if (!attacker.isFlipped()) {
            attacker.flip();
            System.out.println(attacker.getUnit().getDisplayName()
                    + " (" + attacker.getUnit().getAttack() + "/" + attacker.getUnit().getDefence()
                    + ") was flipped on " + selectedPos + "!");
        }
        int damage = attacker.getUnit().getAttack();
        king.owner().loseLife(damage);
        System.out.println(waitingPlayer.getName() + " takes " + damage + " damage!");
        attacker.markMoved();
    }

    private void handleMerge(UnitOnBoard attacker, UnitOnBoard defender, Position pos) {
        System.out.println(attacker.getUnit().getDisplayName() + " moves to " + pos + ".");
        System.out.println(attacker.getUnit().getDisplayName()
                + " and " + defender.getUnit().getDisplayName()
                + " on " + pos + " join forces!");
        Unit merged = Unit.tryMerge(attacker.getUnit(), defender.getUnit());
        if (merged != null) {
            board.remove(selectedPos);
            board.remove(pos);
            board.place(pos, new UnitOnBoard(currentPlayer, merged));
            selectedPos = pos;
            System.out.println("Success!");
        } else {
            board.remove(pos);
            System.out.println("Union failed. " + defender.getUnit().getDisplayName() + " was eliminated.");
        }
    }
}