package com.contestantbots.framework;

public interface ContestantBot {
    void issueCommands(final GameState gameState, final Commander commander);
    void getReady(final GameState gameState);
}
