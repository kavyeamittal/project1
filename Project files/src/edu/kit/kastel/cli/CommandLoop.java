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
                String[] parts = line.split("\\s+");
                switch (parts[0].toLowerCase()) {
                    case "quit" -> {
                        return;
                    }
                    case "select" -> handleSelect(game, parts);
                    case "board" -> printBoard(game);
                    case "move" -> handleMove(game, parts);
                    case "flip" -> handleFlip(game);
                    case "block" -> handleBlock(game, parts);
                    case "show" -> handleShow(game);
                    case "hand" -> handleHand(game);
                    case "place" -> handlePlace(game, parts);
                    case "state" -> handleState(game);
                    case "yield" -> handleYield(game, parts);
                    default -> System.out.println("ERROR: Unknown command.");
                }
            }
        }
    }

    private void handleSelect(Game game, String[] parts) {
        if (parts.length != 2) {
            System.out.println("ERROR: Wrong number of parameters.");
            return;
        }

        try {
            Position pos = Position.parse(parts[1].toUpperCase());
            game.select(pos);
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }

    }

    private void handleMove(Game game, String[] parts) {
        if (parts.length != 2) {
            System.out.println("ERROR: Wrong number of parameters.");
            return;
        }

        try {
            Position pos = Position.parse(parts[1].toUpperCase());
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
                    + " (" + unit.getUnit().getAttack() + "/"
                    + unit.getUnit().getDefence()
                    + ") was flipped on " + game.getSelectedPos() + "!");

            BoardRenderer.printBoard(game.getBoard(), game.isVerbose(), game.getCurrentPlayer(), game.getSelectedPos());
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleBlock(Game game, String[] parts) {
        if (parts.length != 1) {
            System.out.println("ERROR: Wrong number of parameters.");
            return;
        }

        try {
            game.blockSelected();
            UnitOnBoard unitOnBoard = (UnitOnBoard) game.getPos();
            System.out.println(unitOnBoard.getUnit().getDisplayName()
                    + " (" + game.getSelectedPos() + ") blocks!");
            printBoard(game);
            handleShow(game);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }


    private void handleShow(Game game) {
        ShowHandler.handleShow(game.getPos(), game.getCurrentPlayer());
    }

    private void handleHand(Game game) {
        var hand = game.getCurrentPlayer().getHand();
        for (int i = 0; i < hand.size(); i++) {
            Unit unit = hand.get(i);
            System.out.println("[" + (i + 1) + "] "
                    + unit.getDisplayName()
                    + " (" + unit.getAttack() + "/" + unit.getDefence() + ")");
        }
    }

    private void handlePlace(Game game, String[] parts) {
        if (parts.length != 2) {
            System.out.println("ERROR: Wrong number of parameters.");
            return;
        }

        int[] indices = new int[parts.length - 1];
        for (int i = 0; i < indices.length; i++) {
            try {
                indices[i] = Integer.parseInt(parts[i + 1]);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid Index: " + parts[i + 1]);
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

    private void handleState(Game game) {
        game.printState();
        printBoard(game);
        handleShow(game);
    }

    private void printBoard(Game game) {
        BoardRenderer.printBoard(game.getBoard(), game.isVerbose(), game.getCurrentPlayer(), game.getSelectedPos());
    }

    private void handleYield(Game game, String[] parts) {
        if (parts.length > 2) {
            System.out.println("ERROR: Wrong number of parameters.");
            return;
        }
        int discardIndex = -1;
        if (parts.length == 2) {
            try {
                discardIndex = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid discard index.");
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
