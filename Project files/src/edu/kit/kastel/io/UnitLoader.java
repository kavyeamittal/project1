package edu.kit.kastel.io;

import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.Unit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and validates unit definitions from a file.
 * Each line must follow the format: Qualifier;Role;ATK;DEF
 *
 * @author uwsfc
 */
public final class UnitLoader {

    /**
     * Private constructor — utility class, not to be instantiated.
     */
    private UnitLoader() {
    }

    /**
     * Loads unit definitions from the given file, printing each line before processing.
     *
     * @param unitsFile path to the units definition file.
     * @param print     flag to print the units
     * @return the list of loaded units.
     * @throws GameException if the file cannot be read or contains invalid data.
     */
    public static List<Unit> loadAndPrint(Path unitsFile, boolean print) throws GameException {
        List<String> lines = readAllLinesOrThrow(unitsFile);
        if (print) {
            for (String line : lines) {
                System.out.println(line);
            }
        }

        if (lines.size() > 80) {
            throw new GameException("ERROR: Unit file contains too many units.");
        } else if (lines.isEmpty()) {
            throw new GameException("ERROR: No units in file.");
        }

        List<Unit> defs = new ArrayList<>();
        for (String line : lines) {
            defs.add(parseUnitLine(line));
        }
        return defs;
    }

    private static List<String> readAllLinesOrThrow(Path path) throws GameException {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameException("ERROR: Could not read file: " + path);
        }
    }

    private static Unit parseUnitLine(String line) throws GameException {
        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new GameException("ERROR: Invalid units line: " + line);
        }
        String qualifier = parts[0];
        String role = parts[1];
        int atk;
        int def;
        try {
            atk = Integer.parseInt(parts[2]);
            def = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new GameException("ERROR: Invalid ATK/DEF in units line: " + line);
        }
        if (atk < 0 || def < 0) {
            throw new GameException("ERROR: Negative ATK/DEF in units line: " + line);
        }
        return new Unit(qualifier, role, atk, def);
    }
}