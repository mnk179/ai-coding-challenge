package com.contestantbots.framework;

import com.scottlogic.hackathon.game.Player;
import com.scottlogic.hackathon.game.Position;

import java.util.UUID;

public class Unit {
    private final Player underlyingObject;

    public Unit(Player objectToWrap) {
        this.underlyingObject = objectToWrap;
    }

    /** @return The id of this unit. */
    public UUID getUnitId() { return this.underlyingObject.getId(); }

    /** @return The id of the bot that owns this unit. */
    public UUID getOwnerId() { return this.underlyingObject.getOwner(); }

    /** @return The position of this unit. */
    public Position getPosition() { return this.underlyingObject.getPosition(); }

    @Override
    public int hashCode() { return this.getUnitId().hashCode(); }

    @Override
    public boolean equals(Object o) {
        Unit other = (Unit) o;

        return this.getUnitId().equals(other.getUnitId());
    }
}
