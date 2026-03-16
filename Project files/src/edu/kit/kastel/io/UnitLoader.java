package edu.kit.kastel.io;

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
     * @throws IllegalArgumentException if the file cannot be read or contains invalid data.
     */
    public static List<Unit> loadAndPrint(Path unitsFile, boolean print) {
        List<String> lines = readAllLinesOrThrow(unitsFile);
        if (print) {
            for (String line : lines) {
                System.out.println(line);
            }
        }
        List<Unit> defs = new ArrayList<>();
        for (String line : lines) {
            defs.add(parseUnitLine(line));
        }
        return defs;
    }

    /**
     * Reads all lines from the given file or throws if the file cannot be read.
     *
     * @param path the file path to read from.
     * @return the list of lines.
     * @throws IllegalArgumentException if the file cannot be read.
     */
    private static List<String> readAllLinesOrThrow(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("ERROR: Could not read file: " + path);
        }
    }

    /**
     * Parses a single unit definition line in the format: qualifier;role;atk;def
     *
     * @param line the line to parse.
     * @return the parsed Unit.
     * @throws IllegalArgumentException if the line format is invalid or values are negative.
     */
    private static Unit parseUnitLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("ERROR: Invalid units line: " + line);
        }
        String qualifier = parts[0];
        String role = parts[1];
        int atk;
        int def;
        try {
            atk = Integer.parseInt(parts[2]);
            def = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERROR: Invalid ATK/DEF in units line: " + line);
        }
        if (atk < 0 || def < 0) {
            throw new IllegalArgumentException("ERROR: Negative ATK/DEF in units line: " + line);
        }
        return new Unit(qualifier, role, atk, def);
    }
}