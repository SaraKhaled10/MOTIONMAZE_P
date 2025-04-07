

import java.util.List;

/**
 * Observer for obstacles that can affect the player.
 * Implements the Observer pattern.
 */
public class ObstacleObserver implements GameObserver {
    @Override
    public void onGameUpdate(GameEngine gameEngine) {
        Player player = gameEngine.getPlayer();
        Maze maze = gameEngine.getMaze();

        // Check for obstacles at the player's position
        Obstacle obstacle = maze.getObstacleAt(player.getX(), player.getY());
        if (obstacle != null) {
            // Apply the obstacle effect
            obstacle.apply(player);

            // Remove the obstacle
            maze.removeObstacle(obstacle);

            // Tell the player about the obstacle collision
            gameEngine.getZigBeeManager().sendLedCommand(0); // Red LED
            gameEngine.getZigBeeManager().sendBuzzerCommand(1); // Collision sound

            // Reset LED after a brief moment
            gameEngine.getZigBeeManager().sendLedCommand(7); // LED off
        }
    }
}
