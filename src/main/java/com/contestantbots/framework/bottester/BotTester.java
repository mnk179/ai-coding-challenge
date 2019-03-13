package com.contestantbots.framework.bottester;

import com.contestantbots.framework.ContestantBotImplementation;
import com.scottlogic.hackathon.client.Client;

import java.util.ArrayList;
import java.util.List;

public class BotTester {
    /**
     * @param map       The map you want the simulation to be run within.
     * @param botToTest Your bot, to test against a canned opponent.
     * @param opponents Every game needs at least one opponent, and you can pick up to 3 at a time (duplicates are okay).
     * @param debugMode This causes all Bots' 'makeMoves()' methods to be invoked from the main thread, and prevents them from being disqualified
     *                  if they take longer than the usual time limit. This allows you to run in your IDE debugger and pause on break points without timing out.
     */
    public <TBot extends ContestantBotImplementation> void startTest(
        Class<TBot> botToTest,
        Map map,
        List<Opponent> opponents,
        boolean debugMode)
        throws Exception {

        final List<String> args = new ArrayList<>();

        args.add("--map");
        args.add(map.toString());

        args.add("--className");
        args.add(botToTest.getName());

        args.add("--bot");
        opponents.forEach(o -> args.add(o.toString()));

        if (debugMode) {
            args.add("--debug");
        }

        // System.out.println(String.join(" ", args));

        Client.main(args.toArray(new String[0]));
    }
}

