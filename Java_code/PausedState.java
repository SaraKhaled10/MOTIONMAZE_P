//package com.motionmaze.state;

//import com.motionmaze.game.GameEngine;

/**
 * Paused state for the game.
 * Implements the State pattern.
 */
public class PausedState implements GameState {
    @Override
    public void processMovement(int direction, GameEngine gameEngine) {
        // No movement in paused state
    }

    @Override
    public void processButtonPress(GameEngine gameEngine) {
        // Button press in paused state resumes the game
        gameEngine.setState(gameEngine.getPlayingState());
    }

    @Override
    public void processMotionDetection(GameEngine gameEngine) {
        // No action for motion detection in paused state
    }

    @Override
    public void update(GameEngine gameEngine) {
        // Update paused state
    }

    @Override
    public void init(GameEngine gameEngine) {
        // Initialize paused state
        System.out.println("Game Paused!");
    }
}
