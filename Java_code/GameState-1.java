//package com.motionmaze.state;

//import com.motionmaze.game.GameEngine;

/**
 * Interface for game states.
 * Part of the State pattern.
 */
public interface GameState {
    /**
     * Process movement from the controller.
     *
     * @param direction Direction (1 = up, 2 = right, 3 = down, 4 = left)
     * @param gameEngine Game engine reference
     */
    void processMovement(int direction, GameEngine gameEngine);

    /**
     * Process button press from the controller.
     *
     * @param gameEngine Game engine reference
     */
    void processButtonPress(GameEngine gameEngine);

    /**
     * Process motion detection from the controller.
     *
     * @param gameEngine Game engine reference
     */
    void processMotionDetection(GameEngine gameEngine);

    /**
     * Update the state.
     *
     * @param gameEngine Game engine reference
     */
    void update(GameEngine gameEngine);

    /**
     * Initialize the state.
     *
     * @param gameEngine Game engine reference
     */
    void init(GameEngine gameEngine);
}
