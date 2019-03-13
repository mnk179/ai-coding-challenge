package com.contestantbots.framework;

import com.scottlogic.hackathon.game.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameState {
    private final UUID operativeBotId;
    private final com.scottlogic.hackathon.game.GameState wrappedGameState;

    public GameState(com.scottlogic.hackathon.game.GameState wrappedGameState, UUID operativeBotId) {
        this.wrappedGameState = wrappedGameState;
        this.operativeBotId = operativeBotId;
    }

    /**
     * @return The current phase of the game. The phase starts at 0 and simply counts up
     *         during the game.
     */
    public int getElapsedTurns() {
        return this.wrappedGameState.getPhase();
    }

    /**
     * @return The game's map.
     */
    public GameMap getMap() {
        return this.wrappedGameState.getMap();
    }

    /**
     * @return The known hazardous positions for the current game.
     */
    public Set<Position> getHazardousPositions() {
        return this.wrappedGameState.getOutOfBoundsPositions();
    }

    /**
     * Checks whether the given position is out of bounds.
     * @param position The position to check
     * @return {@code true} iff the position is out of bounds
     */
    public boolean isHazardous(Position position) {
        return wrappedGameState.isOutOfBounds(position);
    }

    /**
     * @return All known units in the current game, both your own and other bots'.
     */
    public Set<Unit> getAllVisibleUnits() {
        return asUnits(this.wrappedGameState.getPlayers());
    }

    /**
     * @return The operative bot's active units
     */
    public Set<Unit> getMyUnits() {
        return this.wrappedGameState.getPlayers()
            .stream()
            .map(Unit::new)
            .filter(unit -> unit.getOwnerId() == this.operativeBotId)
            .collect(Collectors.toSet());
    }

    /**
     * Gets the {@linkplain Unit} at the given position, if there is one, or an empty Optional otherwise.
     */
    public Optional<Unit> getUnitAt(Position position) {
        return wrappedGameState.getPlayerAt(position).map(Unit::new);
    }

    /**
     *
     * @return The dead unit that were removed after the previous phase.
     */
    public Set<Unit> getAllCasualtiesSinceLastTurn() {
        return asUnits(this.wrappedGameState.getRemovedPlayers());
    }

    /**
     *
     * @return The active spawn points in the current game.
     */
    public Set<SpawnPoint> getBases() {
        return this.wrappedGameState.getSpawnPoints();
    }

    /** Gets  */
    public Optional<SpawnPoint> getMyBase() {
        return this.wrappedGameState.getSpawnPoints()
            .stream()
            .filter(base -> base.getOwner() == this.operativeBotId)
            .findFirst();
    }

    /**
     * Gets the {@linkplain SpawnPoint} at the given position, if there is one (or other an empty Optional)
     */
    public Optional<SpawnPoint> getBaseAt(Position position) {
        return wrappedGameState.getSpawnPointAt(position);
    }

    /**
     * @return The destroyed spawn points that were removed after the previous phase.
     */
    public Set<SpawnPoint> getDestroyedBasesSinceLastTurn() {
        return this.wrappedGameState.getRemovedSpawnPoints();
    }

    /**
     *
     * @return The food items currently visible to the bot.
     */
    public Set<Position> getVisibleFoodPositions() {
        return this.wrappedGameState.getCollectables()
            .stream()
            .map(Collectable::getPosition)
            .collect(Collectors.toSet());
    }

    /**
     * Returns true if there is food at the specified position.
     */
    public boolean isFoodAt(Position position) {
        return wrappedGameState.getCollectableAt(position).isPresent();
    }

    /**
     * Determines if the given position is empty (i.e. not hazardous, and doesn't contain a player, base, or food)
     */
    public boolean isPositionEmpty(Position position) {
        return wrappedGameState.isEmpty(position);
    }

    /**
     * Returns all positions that would be visible to a unit from a specified position, based
     * on the standard view distance
     */
    public Stream<Position> getPositionsVisibleFrom(Position position) {
        final int viewDistance = 5;

        return IntStream.rangeClosed(-viewDistance, viewDistance)
            .mapToObj(x ->
                IntStream.rangeClosed(-viewDistance, viewDistance).mapToObj(y ->
                    this.getMap().createPosition(
                        position.getX() + x,
                        position.getY() + y)))
            .flatMap(Function.identity());
    }

    private static Set<Unit> asUnits(Set<Player> players) {
        return players.stream().map(Unit::new).collect(Collectors.toSet());
    }
}
