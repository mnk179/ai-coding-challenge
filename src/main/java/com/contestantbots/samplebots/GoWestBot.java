package com.contestantbots.samplebots;

import com.contestantbots.framework.Commander;
import com.contestantbots.framework.ContestantBotImplementation;
import com.contestantbots.framework.GameState;
import com.contestantbots.framework.Unit;
import com.scottlogic.hackathon.game.*;

/**
 * This bot is a fan of the Village People.
 */
public class GoWestBot extends ContestantBotImplementation {
    public GoWestBot() { super("Go West Bot"); }

    public void getReady(GameState gameState) {}

    public void issueCommands(GameState gameState, Commander commander) {
        for (Unit unit : gameState.getMyUnits()) {
            commander.issueMoveOrder(unit, Direction.WEST);
        }
    }
}
