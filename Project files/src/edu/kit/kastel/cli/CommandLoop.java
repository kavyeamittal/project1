package edu.kit.kastel.cli;

import edu.kit.kastel.engine.Game;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Position;
import edu.kit.kastel.model.Unit;
import edu.kit.kastel.model.UnitOnBoard;

import java.util.Scanner;

/**
 * The Class to loop and take input of the query which is to be played by the user.
 *
 * @author uwsfc
 */
public class CommandLoop {

    private static final String COMMAND_LINE = "Use one of the following commands: select, board, move, flip, block, "
            + "hand, place, show, yield, state, quit.";

    private static final String CMD_QUIT = "quit";
    private static final String CMD_SELECT = "select";
    private static final String CMD_BOARD = "board";
    private static final String CMD_MOVE = "move";
    private static final String CMD_FLIP = "flip";
    private static final String CMD_BLOCK = "block";
    private static final String CMD_SHOW = "show";
    private static final String CMD_HAND = "hand";
    private static final String CMD_PLACE = "place";
    private static final String CMD_STATE = "state";
    private static final String CMD_YIELD = "yield";

    private static final String ERR_UNKNOWN_CMD = "ERROR: Unknown command.";
    private static final String ERR_WRONG_PARAMS = "ERROR: Wrong number of parameters.";
    private static final String ERR_QUIT_ARGS = "ERROR: quit command cannot take arguments.";
    private static final String ERR_BOARD_ARGS = "ERROR: board command cannot take arguments.";
    private static final String ERR_NO_SELECT = "ERROR: No field selected.";
    private static final String ERR_HAND_ARGS = "ERROR: Hand cannot take more arguments.";
    private static final String ERR_INVALID_INDEX = "ERROR: Invalid Index: ";
    private static final String ERR_STATE_ARGS = "ERROR: state command cannot take arguments.";
    private static final String ERR_INVALID_DISCARD = "ERROR: Invalid discard index.";
    private static final String REGEX_WHITESPACE = "\\s+";
    private static final String PAREN_OPEN = " (";
    private static final String SLASH = "/";
    private static final String PAREN_CLOSE = ")";
    private static final String FLIPPED_ON = ") was flipped on ";
    private static final String EXCLAMATION = "!";
    private static final String BLOCKS_MSG = ") blocks!";
    private static final String BRACKET_OPEN = "[";
    private static final String BRACKET_CLOSE = "] ";
    private static final int CMD_INDEX = 0;
    private static final int ARG_INDEX = 1;
    private static final int EXPECTED_LEN_NO_ARGS = 1;
    private static final int EXPECTED_LEN_ONE_ARG = 2;
    private static final int NO_DISCARD_DEFAULT = -1;
    private static final int INDEX_OFFSET = 1;
    /**
     * Method to run the game itself.
     *
     * @param game represents the game which is to be run.
     */
    public void run(Game game) {
        System.out.println(COMMAND_LINE);
        try (Scanner scanner = new Scanner(System.in)) {
            while (!game.isGameOver()) {
                if (!scanner.hasNextLine()) {
                    return;
                }
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(REGEX_WHITESPACE);
                switch (parts[CMD_INDEX].toLowerCase()) {
                    case CMD_QUIT -> {
                        if (parts.length != EXPECTED_LEN_NO_ARGS) {
                            System.out.println(ERR_QUIT_ARGS);
                        } else {
                            return;
                        }

                    }
                    case CMD_SELECT -> handleSelect(game, parts);
                    case CMD_BOARD -> {
                        if (parts.length != EXPECTED_LEN_NO_ARGS) {
                            System.out.println(ERR_BOARD_ARGS);
                        } else {
                            printBoard(game);
                        }
                    }
                    case CMD_MOVE -> handleMove(game, parts);
                    case CMD_FLIP -> handleFlip(game);
                    case CMD_BLOCK -> handleBlock(game, parts);
                    case CMD_SHOW -> handleShow(game);
                    case CMD_HAND -> handleHand(game, parts);
                    case CMD_PLACE -> handlePlace(game, parts);
                    case CMD_STATE -> handleState(game, parts);
                    case CMD_YIELD -> handleYield(game, parts);
                    default -> System.out.println(ERR_UNKNOWN_CMD);
                }
            }
        }
    }

    private void handleSelect(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_ONE_ARG) {
            System.out.println(ERR_WRONG_PARAMS);
            return;
        }

        try {
            Position pos = Position.parse(parts[ARG_INDEX].toUpperCase());
            game.select(pos);
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }

    }

    private void handleMove(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_ONE_ARG) {
            System.out.println(ERR_WRONG_PARAMS);
            return;
        }

        try {
            Position pos = Position.parse(parts[ARG_INDEX].toUpperCase());
            game.moveSelectedTo(pos);
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleFlip(Game game) {
        try {
            game.flipSelected();

            UnitOnBoard unit = (UnitOnBoard) game.getPos();
            System.out.println(unit.getUnit().getDisplayName()
                    + PAREN_OPEN + unit.getUnit().getAttack() + SLASH
                    + unit.getUnit().getDefence()
                    + FLIPPED_ON + game.getSelectedPos() + EXCLAMATION);

            BoardRenderer.printBoard(game.getBoard(), game.isVerbose(), game.getCurrentPlayer(), game.getSelectedPos());
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleBlock(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_NO_ARGS) {
            System.out.println(ERR_WRONG_PARAMS);
            return;
        }

        try {
            game.blockSelected();
            UnitOnBoard unitOnBoard = (UnitOnBoard) game.getPos();
            System.out.println(unitOnBoard.getUnit().getDisplayName()
                    + PAREN_OPEN + game.getSelectedPos() + BLOCKS_MSG);
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }


    private void handleShow(Game game) {
        if (game.getSelectedPos() == null) {
            System.out.println(ERR_NO_SELECT);
            return;
        }
        ShowHandler.handleShow(game.getPos(), game.getCurrentPlayer());
    }

    private void handleHand(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_NO_ARGS) {
            System.out.println(ERR_HAND_ARGS);
            return;
        }
        var hand = game.getCurrentPlayer().getHand();
        for (int i = CMD_INDEX; i < hand.size(); i++) {
            Unit unit = hand.get(i);
            System.out.println(BRACKET_OPEN + (i + INDEX_OFFSET) + BRACKET_CLOSE
                    + unit.getDisplayName()
                    + PAREN_OPEN + unit.getAttack() + SLASH + unit.getDefence() + PAREN_CLOSE);
        }
    }

    private void handlePlace(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_ONE_ARG) {
            System.out.println(ERR_WRONG_PARAMS);
            return;
        }

        int[] indices = new int[parts.length - INDEX_OFFSET];
        for (int i = CMD_INDEX; i < indices.length; i++) {
            try {
                indices[i] = Integer.parseInt(parts[i + INDEX_OFFSET]);
            } catch (NumberFormatException e) {
                System.out.println(ERR_INVALID_INDEX + parts[i + INDEX_OFFSET]);
                return;
            }
        }
        try {
            game.placeFromHand(indices);
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleState(Game game, String[] parts) {
        if (parts.length != EXPECTED_LEN_NO_ARGS) {
            System.out.println(ERR_STATE_ARGS);
            return;
        }
        game.printState();
        printBoard(game);
        if (game.getSelectedPos() != null) {
            handleShow(game);
        }
    }

    private void printBoard(Game game) {
        BoardRenderer.printBoard(game.getBoard(), game.isVerbose(), game.getCurrentPlayer(), game.getSelectedPos());
    }

    private void handleYield(Game game, String[] parts) {
        if (parts.length > EXPECTED_LEN_ONE_ARG) {
            System.out.println(ERR_WRONG_PARAMS);
            return;
        }
        int discardIndex = NO_DISCARD_DEFAULT;
        if (parts.length == EXPECTED_LEN_ONE_ARG) {
            try {
                discardIndex = Integer.parseInt(parts[ARG_INDEX]);
            } catch (NumberFormatException e) {
                System.out.println(ERR_INVALID_DISCARD);
                return;
            }
        }
        try {
            game.yieldTurn(discardIndex);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }
}
