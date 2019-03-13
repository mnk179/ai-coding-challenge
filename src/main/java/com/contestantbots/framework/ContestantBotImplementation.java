package com.contestantbots.framework;

import com.contestantbots.framework.util.GameStateLogger;
import com.scottlogic.hackathon.game.*;
import com.scottlogic.hackathon.game.GameState;

import java.util.*;

// not a strict adapter, because it also adds console output
public abstract class ContestantBotImplementation extends Bot implements ContestantBot {
    private final GameStateLogger gameStateLogger;

    public ContestantBotImplementation(String displayName) {
        super(displayName);

        this.gameStateLogger = new GameStateLogger(getId());
    }

    @Override
    public final List<Move> makeMoves(final GameState gameState) {
        gameStateLogger.process(gameState);

        final InternalCommander commander = new InternalCommander(this.getId());
        this.issueCommands(
            new com.contestantbots.framework.GameState(gameState, this.getId()),
            commander);

        return commander.getCommands();
    }

    @Override
    public final void initialise(final GameState gameState) {
        this.getReady(
            new com.contestantbots.framework.GameState(gameState, this.getId()));
    }

    private class InternalCommander implements Commander {
        private final List<Move> commands = new ArrayList<>();
        private final Set<UUID> commandedUnits = new HashSet<>();
        private final UUID botId;

        InternalCommander(final UUID botId) {
            this.botId = botId;
        }

        public void issueMoveOrder(Unit unit, Direction direction) {
            // don't accept orders for things you don't own
            if (unit.getOwnerId() != this.botId) return;

            // don't accept multiple orders for the same unit
            if (commandedUnits.contains(unit.getUnitId())) return;

            this.commands.add(new MoveCommand(unit.getUnitId(), direction));
            this.commandedUnits.add(unit.getUnitId());
        }

        List<Move> getCommands() {
            return this.commands;
        }

        private class MoveCommand implements Move {
            private UUID playerId;
            private Direction direction;

            MoveCommand(UUID playerId, Direction direction) {
                this.playerId = playerId;
                this.direction = direction;
            }

            @Override
            public UUID getPlayer() {
                return playerId;
            }
            @Override
            public Direction getDirection() {
                return direction;
            }
        }
    }
}
