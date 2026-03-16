package edu.kit.kastel.exceptions;

/**
 * Checked exception for all game-related errors.
 * Thrown when a game rule is violated or an invalid action is attempted.
 *
 * @author uwsfc
 */
public class GameException extends Exception {

    /**
     * Creates a GameException with the given error message.
     *
     * @param message the error message.
     */
    public GameException(String message) {
        super(message);
    }
}