
/**
 * Game over state for the game.
 * Implements the State pattern.
 */
public class GameOverState implements GameState {
    @Override
    public void processMovement(int direction, GameEngine gameEngine) {
        // No movement in game over state
    }

    @Override
    public void processButtonPress(GameEngine gameEngine) {
        // Button press in game over state restarts the game
        gameEngine.startNewGame();
    }

    @Override
    public void processMotionDetection(GameEngine gameEngine) {
        // No action for motion detection in game over state
    }

    @Override
    public void update(GameEngine gameEngine) {
        // Update game over state
    }

    @Override
    public void init(GameEngine gameEngine) {
        // Initialize game over state
        System.out.println("Game Over!");
    }
}
