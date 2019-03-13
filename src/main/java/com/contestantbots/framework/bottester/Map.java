package com.contestantbots.framework.bottester;

/**
 * Each successive map is larger, and has more out-of-bounds positions that must be avoided.
 * The maps starting with "Three" are designed specifically for 3 players
 */
public enum Map {
    Small,
    VeryEasy,
    Easy,
    Medium,
    LargeMedium,
    Hard,
    ThreeStar,
    ThreeStraight
}
