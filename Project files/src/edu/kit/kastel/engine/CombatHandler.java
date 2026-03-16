package edu.kit.kastel.engine;

import edu.kit.kastel.model.Board;
import edu.kit.kastel.model.Player;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.UnitOnBoard;

/**
 * Handles combat logic between units on the board.
 * @author uwsfc
 */
public class CombatHandler {

    private final Board board;
    private final Player currentPlayer;
    private final Player waitingPlayer;

    /**
     * Creates a CombatHandler constructor.
     * @param board the game board.
     * @param currentPlayer the currently active player.
     * @param waitingPlayer the waiting player.
     */
    public CombatHandler(Board board, Player currentPlayer, Player waitingPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.waitingPlayer = waitingPlayer;
    }

    /**
     * Method to resolve a duel between attacker and defender.
     * @param attacker the attacking unit.
     * @param defender the defending unit.
     * @param attackerPos the attacker's position.
     * @param defenderPos the defender's position.
     * @return the position the attacker ends up at, or null if eliminated.
     */
    public Position handleDuel(UnitOnBoard attacker, UnitOnBoard defender,
                               Position attackerPos, Position defenderPos) {
        printDuelHeader(attacker, defender, defenderPos);
        flipIfNeeded(attacker, attackerPos);
        flipIfNeeded(defender, defenderPos);
        if (defender.isBlocked()) {
            return handleBlockadeDuel(attacker, defender, attackerPos, defenderPos);
        } else {
            return handleStandardDuel(attacker, defender, attackerPos, defenderPos);
        }
    }

    private void printDuelHeader(UnitOnBoard attacker, UnitOnBoard defender, Position defenderPos) {
        String defName = defender.isFlipped() ? defender.getUnit().getDisplayName() : "???";
        String defStats = defender.isFlipped()
                ? " (" + defender.getUnit().getAttack() + "/" + defender.getUnit().getDefence() + ")" : "";
        System.out.println(attacker.getUnit().getDisplayName()
                + " (" + attacker.getUnit().getAttack() + "/" + attacker.getUnit().getDefence()
                + ") attacks " + defName + defStats + " on " + defenderPos + "!");
    }

    private void flipIfNeeded(UnitOnBoard unit, Position unitPos) {
        if (!unit.isFlipped()) {
            unit.flip();
            System.out.println(unit.getUnit().getDisplayName()
                    + " (" + unit.getUnit().getAttack() + "/" + unit.getUnit().getDefence()
                    + ") was flipped on " + unitPos + "!");
        }
    }

    private Position handleBlockadeDuel(UnitOnBoard attacker, UnitOnBoard defender,
                                        Position attackerPos, Position defenderPos) {
        int atkA = attacker.getUnit().getAttack();
        int defB = defender.getUnit().getDefence();
        if (atkA > defB) {
            System.out.println(defender.getUnit().getDisplayName() + " was eliminated!");
            board.remove(defenderPos);
            board.remove(attackerPos);
            board.place(defenderPos, attacker);
            attacker.markMoved();
            System.out.println(attacker.getUnit().getDisplayName() + " moves to " + defenderPos + ".");
            return defenderPos;
        } else if (atkA < defB) {
            int damage = defB - atkA;
            currentPlayer.loseLife(damage);
            System.out.println(currentPlayer.getName() + " takes " + damage + " damage!");
        }
        return attackerPos;
    }

    private Position handleStandardDuel(UnitOnBoard attacker, UnitOnBoard defender,
                                        Position attackerPos, Position defenderPos) {
        int atkA = attacker.getUnit().getAttack();
        int atkB = defender.getUnit().getAttack();
        if (atkA > atkB) {
            return attackerWins(attacker, defender, attackerPos, defenderPos, atkA - atkB);
        } else if (atkB > atkA) {
            return defenderWins(attacker, attackerPos, atkB - atkA);
        } else {
            return draw(attacker, defender, attackerPos, defenderPos);
        }
    }

    private Position attackerWins(UnitOnBoard attacker, UnitOnBoard defender,
                                  Position attackerPos, Position defenderPos, int damage) {
        System.out.println(defender.getUnit().getDisplayName() + " was eliminated!");
        waitingPlayer.loseLife(damage);
        System.out.println(waitingPlayer.getName() + " takes " + damage + " damage!");
        board.remove(defenderPos);
        board.remove(attackerPos);
        board.place(defenderPos, attacker);
        attacker.markMoved();
        System.out.println(attacker.getUnit().getDisplayName() + " moves to " + defenderPos + ".");
        return defenderPos;
    }

    private Position defenderWins(UnitOnBoard attacker, Position attackerPos, int damage) {
        System.out.println(attacker.getUnit().getDisplayName() + " was eliminated!");
        currentPlayer.loseLife(damage);
        System.out.println(currentPlayer.getName() + " takes " + damage + " damage!");
        board.remove(attackerPos);
        return null;
    }

    private Position draw(UnitOnBoard attacker, UnitOnBoard defender,
                          Position attackerPos, Position defenderPos) {
        System.out.println(defender.getUnit().getDisplayName() + " was eliminated!");
        System.out.println(attacker.getUnit().getDisplayName() + " was eliminated!");
        board.remove(defenderPos);
        board.remove(attackerPos);
        return null;
    }

    /**
     * Returns whether the current player has lost.
     * @return true if current player LP is 0 or below.
     */
    public boolean currentPlayerLost() {
        return currentPlayer.getLife() <= 0;
    }

    /**
     * Returns whether the waiting player has lost.
     * @return true if waiting player LP is 0 or below.
     */
    public boolean waitingPlayerLost() {
        return waitingPlayer.getLife() <= 0;
    }
}