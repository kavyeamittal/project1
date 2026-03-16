package edu.kit.kastel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UnitTest {

    @Test
    void tryMergeReturnsNullForIdenticalDisplayNames() {
        Unit first = new Unit("Daisy", "Farmer", 300, 500);
        Unit second = new Unit("Daisy", "Farmer", 1200, 700);

        assertNull(Unit.tryMerge(first, second));
    }

    @Test
    void tryMergeAppliesSymbiosisRules() {
        Unit moving = new Unit("Milk Cow", "Farmer", 500, 400);
        Unit target = new Unit("Barn", "Guard", 400, 500);

        Unit merged = Unit.tryMerge(moving, target);

        assertEquals("Barn Milk Cow Guard", merged.getDisplayName());
        assertEquals(500, merged.getAttack());
        assertEquals(500, merged.getDefence());
    }

    @Test
    void tryMergeAppliesConspirativeCompatibilityRules() {
        Unit moving = new Unit("Tractor", "Farmer", 800, 900);
        Unit target = new Unit("Pig", "Farmer", 600, 700);

        Unit merged = Unit.tryMerge(moving, target);

        assertEquals("Pig Tractor Farmer", merged.getDisplayName());
        assertEquals(1200, merged.getAttack());
        assertEquals(1400, merged.getDefence());
    }

    @Test
    void tryMergeAppliesPrimeCompatibilityRules() {
        Unit moving = new Unit("Shield", "Farmer", 200, 500);
        Unit target = new Unit("Fence", "Farmer", 300, 700);

        Unit merged = Unit.tryMerge(moving, target);

        assertEquals("Fence Shield Farmer", merged.getDisplayName());
        assertEquals(500, merged.getAttack());
        assertEquals(1200, merged.getDefence());
    }

    @Test
    void tryMergeReturnsNullForIncompatibleUnits() {
        Unit moving = new Unit("Stable", "Farmer", 450, 350);
        Unit target = new Unit("Grain", "Farmer", 1000, 1100);

        assertNull(Unit.tryMerge(moving, target));
    }
}
