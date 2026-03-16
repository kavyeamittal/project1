# Agent Handoff Context

## Project And Assignment

- Course assignment: text-based Java game `Crown of Fаrmlаnd` (KIT Programming, Winter 2025/26).
- Main task: implement game logic, command loop, AI behavior, strict output formatting, and strict error handling.
- Important spec source: `assignment-2.pdf` (German).
- Example baseline for deterministic behavior: `example_interaction_1.txt`.

## Core Spec Highlights (Practical)

- Java version: Java SE 21.
- Program args (key=value): requires `seed`, `units`, and either `deck` OR (`deck1` and `deck2`).
- Optional args include `board`, `team1`, `team2`, `verbosity`.
- Error messages must start with `ERROR: `.
- Board is 7x7 (`A1`..`G7`), king positions start at `D1` and `D7`.
- AI behavior is specified and deterministic when random calls are made in the required order.
- Output formatting is strict (board/state/command output).

## What Was Investigated

1. The program initially could not run locally because Java/Maven were missing.
2. After environment setup, game ran successfully with scripted input.
3. Output mismatched `example_interaction_1.txt` in multiple places.
4. Root causes found:
   - Local `src/units.txt` order differed from the example interaction's units order.
   - AI turn logic only moved one unit per AI turn (spec requires iterating until no movable unit remains).
   - `state` spacing alignment was off.
5. After fixes and when units order matches the example file, output became effectively aligned except one known likely example inconsistency (see caveat below).

## Code Changes Made

### 1) AI turn flow fix

- File: `Project files/src/edu/kit/kastel/engine/ai/AITurnHandler.java`
- Change:
  - `moveAiUnits(...)` now loops until there are no AI units left that can move this turn.
  - Candidate units are filtered to only those with `!hasMovedThisTurn()`.
- Reason:
  - Assignment AI movement phase should continue for all eligible units.

### 2) AI fallback-to-block fix

- File: `Project files/src/edu/kit/kastel/engine/ai/AILogic.java`
- Change:
  - In `chooseUnitMove(...)`, if no movement option (up/right/down/left/en-place) has positive score, return block action.
- Reason:
  - Matches spec rule: if no positive movement is available, block.

### 3) State spacing alignment fix

- File: `Project files/src/edu/kit/kastel/engine/StateRenderer.java`
- Change:
  - Padding computation updated from `TOTAL_WIDTH - left.length() - right.length()`
  - To `TOTAL_WIDTH - 2 - left.length() - right.length()`
- Reason:
  - Corrects alignment with two-space left indent and required width formatting.

## Test Infrastructure Added

### Maven updates

- File: `Project files/pom.xml`
- Added:
  - JUnit 5 dependency (`org.junit.jupiter:junit-jupiter`).
  - Surefire plugin.
  - `testSourceDirectory` (`src/test/java`).
  - Compiler exclusion for `test/**` from main compile phase.

### Unit tests added

- `Project files/src/test/java/edu/kit/kastel/model/UnitTest.java`
  - Merge compatibility cases: symbiosis, g3t>100, prime compatibility, incompatible, identical names.
- `Project files/src/test/java/edu/kit/kastel/io/ArgsParserTest.java`
  - Valid/invalid startup argument combinations, duplicates, invalid verbosity.
- `Project files/src/test/java/edu/kit/kastel/io/LoadersTest.java`
  - Unit/deck parsing and deck constraints (40 cards, line mismatch, malformed inputs).
- `Project files/src/test/java/edu/kit/kastel/engine/PlacementAndMoveTest.java`
  - Placement constraints, move behavior, king constraints, duel outcomes.
  - Additional movement constraints:
    - king cannot move onto enemy king
    - king moving onto own unit eliminates that unit
    - king en-place move marks moved
    - normal unit diagonal move rejected (via game rules)
- `Project files/src/test/java/edu/kit/kastel/engine/GameTurnRulesTest.java`
  - Yield/discard constraints and movement distance rejection.
- `Project files/src/test/java/edu/kit/kastel/engine/ai/AILogicTest.java`
  - AI no-positive-move block fallback and placement edge case.
- `Project files/src/test/java/edu/kit/kastel/engine/StateRendererTest.java`
  - State output alignment width checks.

### Current test status

- `mvn test` passes locally (all tests green in the final run).

## Known Caveat (Important)

- Comparing aligned local output to `example_interaction_1.txt` leaves one residual board-line mismatch:
  - Expected file shows `N N` where local output shows `N*X N` at first board print after `select D1`.
- Based on assignment board-marker rules (`*` for movable current-team piece), `*X` appears logically correct.
- This may be an inconsistency in the provided example text, not necessarily a logic bug.

## Why Artemis Screenshots Showed Many Failures

- The screenshots indicate many functional tests were **not executed** because prerequisite `"MANDATORY Example Interaction"` failed.
- That means downstream failures in screenshots are blocked/gated, not confirmed independent bugs.
- First priority in CI should be exact mandatory-interaction compliance.

## Repro/Run Commands

From `Project files`:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
export PATH="$JAVA_HOME/bin:/opt/homebrew/opt/maven/bin:$PATH"
mvn -q compile && java -cp target/classes edu.kit.kastel.Main seed=-4022738 deck=src/deck.txt verbosity=compact units=src/units.txt < src/input.txt
```

Run tests:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
export PATH="$JAVA_HOME/bin:/opt/homebrew/opt/maven/bin:$PATH"
mvn -q test
```

## Pending/Next Actions For Another Agent

1. Verify mandatory interaction against the exact grader files/paths used in Artemis (especially units/deck source files and order).
2. If CI still fails mandatory interaction, isolate first mismatch line and patch minimally.
3. After mandatory passes, check newly unlocked functional error-handling tests and patch message text/validation order if required.
4. Keep output formatting exact (especially board and state alignment).
