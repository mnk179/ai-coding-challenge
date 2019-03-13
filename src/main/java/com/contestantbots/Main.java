package com.contestantbots;

import com.contestantbots.bots.UnnamedBot;
import com.contestantbots.framework.bottester.BotTester;
import com.contestantbots.framework.bottester.Map;
import com.contestantbots.framework.bottester.Opponent;

import java.util.Arrays;

public class Main {
    /*
     * Run this main as a java application to test and debug your code within your IDE.
     * After each turn, the current state of the game will be printed as an ASCII-art representation in the console.
     * You can study the map before hitting 'Enter' to play the next phase.
     */
    public static void main(String ignored[]) throws Exception {
        new BotTester().startTest(
            // which of your bots you'd like to test (you'll start with only one, but it's fine to create more)
            UnnamedBot.class,

            // the arena to battle in - 'harder' arenas are typically bigger
            Map.Small,

            // the opponents you'll be facing - there must be at least 1 of them, and can be more on larger maps
            Arrays.asList(
                Opponent.Default),

            // disables the time limit on each bot's turn - this saves you from
            // being disqualified when you're debugging your code
            true);
    }
}
