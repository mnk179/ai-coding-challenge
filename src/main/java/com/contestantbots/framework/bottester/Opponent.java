package com.contestantbots.framework.bottester;

public enum Opponent {
    Default, // Players move in random directions
    Milestone1, // Units just try to stay out of trouble
    Milestone2, // Some units gather collectables, some attack enemy units, and some attack enemy spawn points
    Milestone3, // Strategy dynamically updates based on the current state of the game
    Self
}
