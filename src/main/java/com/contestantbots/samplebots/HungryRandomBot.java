package com.contestantbots.samplebots;

import com.contestantbots.framework.Commander;
import com.contestantbots.framework.ContestantBotImplementation;
import com.contestantbots.framework.GameState;
import com.contestantbots.framework.Unit;
import com.scottlogic.hackathon.game.Direction;
import com.scottlogic.hackathon.game.Position;
import com.scottlogic.hackathon.game.Route;

import java.util.*;
import java.util.function.Predicate;

public class HungryRandomBot extends ContestantBotImplementation {
    private final Map<Unit, Position> assignedUnitDestinations = new HashMap<>();
    private final Set<Position> claimedFoodItems = new HashSet<>();

    private final Random random = new Random();

    public HungryRandomBot() { super("Hungry Random Bot"); }

    public void getReady(GameState gameState) {}

    public void issueCommands(GameState gameState, Commander commander) {
        // see if we can see any food items that haven't already been claimed
        Set<Position> unclaimedFoodItems = new HashSet<>(gameState.getVisibleFoodPositions());
        unclaimedFoodItems.removeAll(claimedFoodItems);

        for (Position foodPosition : unclaimedFoodItems) {
            // if so, let's assign someone to get them!
            for (Unit unit : gameState.getMyUnits()) {
                // if this unit already has a destination, leave it be
                if (assignedUnitDestinations.containsKey(unit)) continue;

                // otherwise, set that food as claimed and give our unit its new destination
                assignedUnitDestinations.put(unit, foodPosition);
                claimedFoodItems.add(foodPosition);

                break;
            }
        }

        // we'll record in this set the positions that get claimed by our units
        Set<Position> newlyOccupiedPositions = new HashSet<>();

        for (Unit unit : gameState.getMyUnits()) {
            final Optional<Direction> nextMovementDirection;

            if (assignedUnitDestinations.containsKey(unit)) {
                // this unit has been assigned to collect some food, let's check its destination
                Position destination = assignedUnitDestinations.get(unit);

                // and now let's plan a route there
                Optional<Route> routeToDestination = gameState.getMap().findRoute(
                    unit.getPosition(),
                    destination,
                    position ->
                        // avoid a square if somebody's about to move into it...
                        newlyOccupiedPositions.contains(position) ||
                        // ...or if it contains anything but food
                        !(gameState.isPositionEmpty(position) || gameState.isFoodAt(position)));

                // if there's an available route to our destination...
                if (routeToDestination.isPresent()) {
                    /// ...then follow the first step in that route
                    nextMovementDirection = routeToDestination.get().getFirstDirection();
                }
                else { // otherwise there's no clear route, or we've already reached the destination
                    nextMovementDirection = Optional.empty();
                }
            }
            else { // if this unit hasn't been assigned to a destination, we'll have it explore randomly
                nextMovementDirection = chooseRandomDirection(
                    unit,
                    position -> !newlyOccupiedPositions.contains(position),
                    gameState);
            }

            // check whether we've decided a direction for this unit to move in
            if (nextMovementDirection.isPresent()) {
                // if so, dispatch it to the commander and record that this position is claimed
                Position destination = gameState
                    .getMap()
                    .getNeighbour(unit.getPosition(), nextMovementDirection.get());

                newlyOccupiedPositions.add(destination);
                commander.issueMoveOrder(unit, nextMovementDirection.get());
            }
        }
    }

    private Optional<Direction> chooseRandomDirection(
        Unit unit,
        Predicate<Position> positionIsAvailable, // a position is unavailable if another unit is moving into it
        GameState gameState) {

        List<Direction> directionsToTry = Arrays.asList(Direction.values());
        Collections.shuffle(directionsToTry, this.random);

        for (Direction direction : directionsToTry) {
            Position possibleDestination = gameState
                .getMap()
                .getNeighbour(unit.getPosition(), direction);

            // if the cell is occupied, we can't move there
            if (!gameState.isPositionEmpty(possibleDestination))
                continue;

            // if another unit is planning on moving into this position, we should disregard it
            if (!positionIsAvailable.test(possibleDestination))
                continue;

            return Optional.of(direction);
        }

        // there seem to be no available directions to move in!
        return Optional.empty();
    }
}
