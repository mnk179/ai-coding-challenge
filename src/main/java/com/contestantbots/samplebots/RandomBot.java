package com.contestantbots.samplebots;

import com.contestantbots.framework.Commander;
import com.contestantbots.framework.ContestantBotImplementation;
import com.contestantbots.framework.GameState;
import com.contestantbots.framework.Unit;
import com.scottlogic.hackathon.game.Direction;
import com.scottlogic.hackathon.game.Position;

import java.util.*;

public class RandomBot extends ContestantBotImplementation {
    public RandomBot() { super("Random Bot"); }

    public void getReady(GameState gameState) {}

    public void issueCommands(GameState gameState, Commander commander) {
        Set<Position> newlyOccupiedPositions = new HashSet<>();

        for (Unit unit : gameState.getMyUnits()) {
            List<Direction> directionsToTry = Arrays.asList(Direction.values());
            Collections.shuffle(directionsToTry);

            for (Direction direction : directionsToTry) {
                Position destinationPosition = gameState.getMap().getNeighbour(unit.getPosition(), direction);

                // if the cell is occupied, we can't move there
                if (!gameState.isPositionEmpty(destinationPosition))
                    continue;

                // if another unit is planning on moving into this position, we should disregard it
                if (newlyOccupiedPositions.contains(destinationPosition))
                    continue;

                newlyOccupiedPositions.add(destinationPosition);
                commander.issueMoveOrder(unit, direction);
                break;
            }
        }
    }
}
