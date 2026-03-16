package edu.kit.kastel.model;

/**
 * Represents a unit definition with a qualifier, role, attack and defence values.
 * Instances are immutable. Merged units are created as new instances via {@link #tryMerge}.
 *
 * @author uwsfc
 */
public class Unit {

    private final String name;
    private final String role;
    private final int attack;
    private final int defence;

    /**
     * Creates a new Unit.
     *
     * @param name    the qualifier part of the unit's name.
     * @param role    the role part of the unit's name.
     * @param attack  the attack value (non-negative).
     * @param defence the defence value (non-negative).
     */
    public Unit(String name, String role, int attack, int defence) {
        this.name = name;
        this.role = role;
        this.attack = attack;
        this.defence = defence;
    }

    /**
     * Attempts to merge unit A (moving/placed) with unit B (already on target field).
     * The merged unit's name is: qualifier_B + " " + qualifier_A + " " + role_B.
     * Three compatibility types are checked in order:
     * Symbiosis, Gleichgesinntheit, and Primkompatibilität.
     *
     * @param unit1 the unit that is moving onto the field (unit A).
     * @param unit2 the unit already on the target field (unit B).
     * @return the merged unit, or null if the units are incompatible.
     */
    public static Unit tryMerge(Unit unit1, Unit unit2) {
        if (unit1.getDisplayName().equals(unit2.getDisplayName())) {
            return null;
        }
        if (unit1.attack > unit2.attack && unit1.attack == unit2.defence && unit2.attack == unit1.defence) {
            String mergedName = unit2.name + " " + unit1.name + " " + unit2.role;
            return new Unit(mergedName, "", unit1.attack, unit2.defence);
        }
        int gcdAtk = gcd(unit1.attack, unit2.attack);
        int gcdDef = gcd(unit1.defence, unit2.defence);
        int g3t = Math.max(gcdAtk, gcdDef);
        if (g3t > 100) {
            String mergedName = unit2.name + " " + unit1.name + " " + unit2.role;
            return new Unit(mergedName, "", unit1.attack + unit2.attack - g3t, unit1.defence + unit2.defence - g3t);
        }
        if (g3t == 100) {
            boolean atkBothPrime = isPrime(unit1.attack / 100) && isPrime(unit2.attack / 100);
            boolean defBothPrime = isPrime(unit1.defence / 100) && isPrime(unit2.defence / 100);
            if (atkBothPrime || defBothPrime) {
                String mergedName = unit2.name + " " + unit1.name + " " + unit2.role;
                return new Unit(mergedName, "", unit1.attack + unit2.attack, unit1.defence + unit2.defence);
            }
        }
        return null;
    }

    /**
     * Computes the greatest common divisor of two integers using the Euclidean algorithm.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return the greatest common divisor.
     */
    private static int gcd(int a, int b) {
        int num1 = a;
        int num2 = b;
        while (num2 != 0) {
            int t = num2;
            num2 = num1 % num2;
            num1 = t;
        }
        return num1;
    }

    /**
     * Returns whether the given integer is a prime number.
     *
     * @param n the integer to check.
     * @return true if n is prime.
     */
    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the qualifier part of the unit's name.
     *
     * @return the qualifier.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the role part of the unit's name.
     *
     * @return the role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the attack value.
     *
     * @return attack.
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Returns the defence value.
     *
     * @return defence.
     */
    public int getDefence() {
        return defence;
    }

    /**
     * Returns the full display name (qualifier + space + role).
     *
     * @return the display name.
     */
    public String getDisplayName() {
        if (role == null || role.isEmpty()) {
            return name;
        }
        return name + " " + role;
    }
}