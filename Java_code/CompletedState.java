/**
 * Completed state for the game.
 * Implements the State pattern.
 */
public class CompletedState implements GameState {
    @Override
    public void processMovement(int direction, GameEngine gameEngine) {
        // No movement in completed state
    }

    @Override
    public void processButtonPress(GameEngine gameEngine) {
        // Button press in completed state advances to the next level
        gameEngine.levelComplete();
        gameEngine.setState(gameEngine.getPlayingState());
    }

    @Override
    public void processMotionDetection(GameEngine gameEngine) {
        // No action for motion detection in completed state
    }

    @Override
    public void update(GameEngine gameEngine) {
        // Update completed state
    }

    @Override
    public void init(GameEngine gameEngine) {
        // Initialize completed state
        System.out.println("Level Completed!");
    }
}
