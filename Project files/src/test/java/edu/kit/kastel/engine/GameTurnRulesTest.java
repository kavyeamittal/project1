package edu.kit.kastel.engine;

import edu.kit.kastel.config.Config;
import edu.kit.kastel.exceptions.GameException;
import edu.kit.kastel.model.AIPlayer;
import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Hand;
import edu.kit.kastel.model.HumanPlayer;
import edu.kit.kastel.model.Unit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTurnRulesTest {

    @Test
    void yieldRequiresDiscardWhenCurrentHandIsFull() throws GameException {
        Game game = initializedGame();

        assertEquals(5, game.getCurrentPlayer().getHand().size());
        assertThrows(GameException.class, () -> game.yieldTurn(-1));
    }

    @Test
    void yieldRejectsDiscardWhenCurrentHandIsNotFull() throws GameException {
        Game game = initializedGame();
        game.getCurrentPlayer().getHand().remove(0);
        assertEquals(4, game.getCurrentPlayer().getHand().size());

        assertThrows(GameException.class, () -> game.yieldTurn(1));
    }

    @Test
    void moveRejectsTargetsFurtherThanOneStep() throws GameException {
        Game game = initializedGame();
        game.select(edu.kit.kastel.model.Position.parse("D1"));

        assertThrows(GameException.class, () -> game.moveSelectedTo(edu.kit.kastel.model.Position.parse("D3")));
    }

    private static Game initializedGame() throws GameException {
        Deck playerDeck = new Deck(buildFortyCards("PlayerUnit"));
        Deck enemyDeck = new Deck(buildFortyCards("EnemyUnit"));
        HumanPlayer player = new HumanPlayer("Player", playerDeck, new Hand());
        AIPlayer enemy = new AIPlayer("Enemy", enemyDeck, new Hand());
        Game game = new Game(new Config(12345L, true, player, enemy));
        game.initialize();
        return game;
    }

    private static List<Unit> buildFortyCards(String baseName) {
        List<Unit> cards = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            cards.add(new Unit(baseName + i, "Farmer", 100 + i, 100 + i));
        }
        return cards;
    }
}
