package com.contestantbots.samplebots;

import com.contestantbots.framework.Commander;
import com.contestantbots.framework.ContestantBotImplementation;
import com.contestantbots.framework.GameState;

/**
 * This bot doesn't do anything, so it quickly loses the game because its creatures fatally
 * collide as they spawn at its base
 */
public class IdleBot extends ContestantBotImplementation {
    public IdleBot() { super("Idle Bot"); }

    public void getReady(GameState gameState) {}

    public void issueCommands(GameState gameState, Commander commander) {
    }
}
