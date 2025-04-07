

/**
 * Playing state for the game.
 * Implements the State pattern.
 */
public class PlayingState implements GameState {
    @Override
    public void processMovement(int direction, GameEngine gameEngine) {
        Player player = gameEngine.getPlayer();
        Maze maze = gameEngine.getMaze();

        // Calculate the new position
        int dx = 0;
        int dy = 0;

        switch (direction) {
            case 1: // Up
                dy = -1;
                break;

            case 2: // Right
                dx = 1;
                break;

            case 3: // Down
                dy = 1;
                break;

            case 4: // Left
                dx = -1;
                break;
        }

        // Calculate the new position
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        // Check if the move is valid
        if (maze.isValidMove(newX, newY)) {
            // Move the player
            player.move(dx, dy);

            // Check if the player reached the exit
            if (maze.isExit(player.getX(), player.getY())) {
                // Change to the completed state
                gameEngine.setState(gameEngine.getCompletedState());
                return;
            }

            // Check if the player hit an obstacle
            Obstacle obstacle = maze.getObstacleAt(player.getX(), player.getY());
            if (obstacle != null) {
                // Apply the obstacle effect
                obstacle.apply(player);

                // Use LED to indicate collision
                gameEngine.getZigBeeManager().sendLedCommand(0); // Red LED
                gameEngine.getZigBeeManager().sendBuzzerCommand(1); // Collision sound

                // Reset LED after a brief moment (in real implementation)
                gameEngine.getZigBeeManager().sendLedCommand(7); // LED off

                // Decrease score
                player.increaseScore(-50);
            }

            // Check if the player picked up a power-up
            PowerUp powerUp = maze.getPowerUpAt(player.getX(), player.getY());
            if (powerUp != null) {
                // Apply the power-up effect
                powerUp.apply(player);

                // Send power-up collected command to Arduino
                gameEngine.getZigBeeManager().sendPowerUpCommand();

                // Remove the power-up
                maze.removePowerUp(powerUp);

                // Increase score
                player.increaseScore(25);
            }
        }

        // Check if the player is dead
        if (player.getHealth() <= 0) {
            // Call gameOver method in GameEngine to handle Arduino communication
            gameEngine.gameOver();
        }
    }

    @Override
    public void processButtonPress(GameEngine gameEngine) {
        // Button press can be used for special actions in the game
        Player player = gameEngine.getPlayer();

        // Increase score
        player.increaseScore(10);

        // Move servo to indicate button press
        gameEngine.getZigBeeManager().sendServoCommand(10); // ~120 degrees

        // Randomly spawn a new event (power-up or obstacle)
        if (Math.random() < 0.3) { // 30% chance
            gameEngine.spawnRandomEvent();
        }
    }

    @Override
    public void processMotionDetection(GameEngine gameEngine) {
        // Motion detection can be used for special actions in the game
        Player player = gameEngine.getPlayer();

        // Increase score
        player.increaseScore(5);

        // Set motion detected flag
        gameEngine.setMotionDetected(true);

        // Use LED to indicate motion detection
        gameEngine.getZigBeeManager().sendLedCommand(6); // White LED

        // Reset LED after a brief moment (in real implementation)
        gameEngine.getZigBeeManager().sendLedCommand(7); // LED off

        // Move servo to indicate motion detection
        gameEngine.getZigBeeManager().sendServoCommand(3); // ~35 degrees
    }

    @Override
    public void update(GameEngine gameEngine) {
        // Update the game logic

        // Reset motion detected flag
        gameEngine.setMotionDetected(false);

        // Notify all observers
        gameEngine.notifyObservers();
    }

    @Override
    public void init(GameEngine gameEngine) {
        // Initialize the playing state
        gameEngine.getZigBeeManager().sendLedCommand(1); // Green LED
        gameEngine.getZigBeeManager().sendServoCommand(7); // ~90 degrees (center)
    }
}
