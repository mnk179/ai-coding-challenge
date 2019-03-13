package com.contestantbots.bots;

import com.contestantbots.framework.Commander;
import com.contestantbots.framework.ContestantBotImplementation;
import com.contestantbots.framework.GameState;
import com.contestantbots.framework.Unit;
import com.scottlogic.hackathon.game.*;

import java.util.*;
import java.util.function.Predicate;

public class KTVMBot extends ContestantBotImplementation {
    private final Map<Unit, Position> assignedUnitDestinations = new HashMap<>();
    private final Set<Position> claimedFoodItems = new HashSet<>();
    private Boolean onceFlag = false;

    private final Random random = new Random();

    public KTVMBot() { super("KTVM Bot"); }

    public void getReady(GameState gameState) {}

    public void issueCommands(GameState gameState, Commander commander) {
        if (gameState.getMyUnits().size() < 14) {
            assignFoodCollectionCommands(gameState, commander);
        }
//        else {
////            defend
//            Optional<SpawnPoint> spawnPoint = gameState.getMyBase();
//            if (spawnPoint.isPresent()) {
//                Position base = spawnPoint.get().getPosition();
//                Position defensePosition = new Position(base.getX(), base.getY());
//                assembleGroup(gameState, commander, gameState.getMyUnits(), defensePosition, 4);
//            }
//        }
        else if (!this.onceFlag) {
//            determine centre
            this.onceFlag = true;
            assignedUnitDestinations.clear();
            Position centre = findCentre(gameState.getMap());
            assembleGroup(gameState, commander, gameState.getMyUnits(), centre, 5);
        }
        else {
            assignFoodCollectionCommands(gameState, commander);
        }
//        else {
////            allocate units
////            assemble group
////            attack
//        }
        moveToAssignedDestinations(gameState, commander);
    }

    private Position findCentre(GameMap map) {
        int height = map.getHeight();
        int width = map.getWidth();

        return new Position(width / 2, height / 2);
    }

    private void assembleGroup(GameState gameState, Commander commander, Set<Unit> units, Position destination, Integer size) {
        int height = gameState.getMap().getHeight();
        int width = gameState.getMap().getWidth();
        assert destination.getX() + size < width;
        assert destination.getY() + size < height;

        //        calculate positions
        Set<Position> positions = new HashSet<>();
        Boolean shift = false;
        for (int i = destination.getY(); i < size; i++) {
            for (int j = destination.getX(); j < size; j++) {
                if (shift) {
                    if ((j % 2) != 0) {
                        positions.add(new Position(i, j));
                    }
                }
                else {
                    if ((j % 2) == 0) {
                        positions.add(new Position(i, j));
                    }
                }
            }
            if (shift) {
                shift = false;
            }
            else {
                shift = true;
            }
        }

        Stack<Unit> unitStack = new Stack<>();
        for (Unit unit : units) {
            unitStack.push(unit);
        }

        for (Position position : positions) {
            Unit unit = unitStack.pop();
            if (assignedUnitDestinations.containsKey(unit)) continue;
            assignedUnitDestinations.put(unitStack.pop(), position);
        }
    }

    private void moveToAssignedDestinations(GameState gameState, Commander commander) {
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
                } else { // otherwise there's no clear route, or we've already reached the destination
                    nextMovementDirection = Optional.empty();
                }
            } else { // if this unit hasn't been assigned to a destination, we'll have it explore randomly
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

        private void assignFoodCollectionCommands(GameState gameState, Commander commander) {
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
