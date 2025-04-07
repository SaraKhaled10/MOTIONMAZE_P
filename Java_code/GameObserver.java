//package com.motionmaze.game;

/**
 * Observer interface for the game.
 * Implements the Observer pattern.
 */
public interface GameObserver {
    /**
     * Called when the game is updated.
     *
     * @param gameEngine Game engine that was updated
     */
    void onGameUpdate(GameEngine gameEngine);
}
