package com.contestantbots.framework;

import com.scottlogic.hackathon.game.Direction;

public interface Commander {
    void issueMoveOrder(Unit unit, Direction direction);
}
