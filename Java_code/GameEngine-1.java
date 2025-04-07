import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game engine for Motion Maze.
 * Manages game state, logic, and communication.
 */
public class GameEngine {
    private GameState state;
    private GameState playingState;
    private GameState pausedState;
    private GameState gameOverState;
    private GameState completedState;

    private ZigBeeManager zigBeeManager;
    private Maze maze;
    private Player player;
    private List<GameObserver> observers;
    private boolean motionDetected;
    private int level;
    private int score;
    private Random random;

    /**
     * Constructor for creating a new game engine.
     */
    public GameEngine() {
        this.playingState = new PlayingState();
        this.pausedState = new PausedState();
        this.gameOverState = new GameOverState();
        this.completedState = new CompletedState();
        this.state = this.playingState;

        this.observers = new ArrayList<>();
        this.zigBeeManager = new ZigBeeManager(this);
        this.motionDetected = false;
        this.level = 1;
        this.score = 0;
        this.random = new Random();
    }

    /**
     * Start the game engine.
     */
    public void start() {
        // Connect to ZigBee
        zigBeeManager.connect();

        // Send game start command to Arduino
        zigBeeManager.sendGameStartCommand();

        // Create a new maze
        this.maze = new Maze(10, 10, level);

        // Create a new player
        this.player = new Player(maze.getStartX(), maze.getStartY());

        // Start a new game
        startNewGame();
    }

    /**
     * Stop the game engine.
     */
    public void stop() {
        // Disconnect from ZigBee
        zigBeeManager.disconnect();
    }

    /**
     * Update the game engine.
     */
    public void update() {
        // Update state
        state.update(this);

        // Notify observers
        notifyObservers();
    }

    /**
     * Process movement from ZigBee.
     *
     * @param direction Direction (1 = up, 2 = right, 3 = down, 4 = left)
     */
    public void processMovement(int direction) {
        state.processMovement(direction, this);
    }

    /**
     * Process button press from ZigBee.
     */
    public void processButtonPress() {
        state.processButtonPress(this);
    }

    /**
     * Process motion detection from ZigBee.
     */
    public void processMotionDetection() {
        state.processMotionDetection(this);
    }

    /**
     * Move the player.
     *
     * @param direction Direction (1 = up, 2 = right, 3 = down, 4 = left)
     */
    public void movePlayer(int direction) {
        int newX = player.getX();
        int newY = player.getY();

        switch (direction) {
            case 1:  // Up
                newY--;
                break;
            case 2:  // Right
                newX++;
                break;
            case 3:  // Down
                newY++;
                break;
            case 4:  // Left
                newX--;
                break;
        }

        // Check if the move is valid
        if (maze.isValidMove(newX, newY)) {
            player.moveTo(newX, newY);

            // Check if the player reached a power-up
            PowerUp powerUp = maze.getPowerUpAt(newX, newY);
            if (powerUp != null) {
                // Apply power-up effect
                powerUp.apply(player);

                // Remove power-up from maze
                maze.removePowerUp(powerUp);

                // Increase score
                score += 10;

                // Send power-up collected command to Arduino
                zigBeeManager.sendPowerUpCommand();
            }

            // Check if the player reached the finish
            if (newX == maze.getFinishX() && newY == maze.getFinishY()) {
                // Level complete
                setState(completedState);
            }
        }
    }

    /**
     * Spawn a random event in the maze.
     */
    public void spawnRandomEvent() {
        // 50% chance to spawn a power-up, 50% chance to spawn an obstacle
        if (random.nextBoolean()) {
            // Spawn a power-up
            PowerUpType[] types = PowerUpType.values();
            PowerUpType type = types[random.nextInt(types.length)];

            // Find a valid position
            int x, y;
            do {
                x = random.nextInt(maze.getWidth());
                y = random.nextInt(maze.getHeight());
            } while (!maze.isValidMove(x, y) || (x == player.getX() && y == player.getY()));

            // Add power-up to maze
            maze.addPowerUp(new PowerUp(x, y, type));

            // Use servo to indicate power-up spawn
            int servoPosition = (x * maze.getHeight() + y) % 16; // Map x,y to 0-15 range
            zigBeeManager.sendServoCommand(servoPosition);
        } else {
            // Spawn an obstacle
            ObstacleType[] types = ObstacleType.values();
            ObstacleType type = types[random.nextInt(types.length)];

            // Find a valid position
            int x, y;
            do {
                x = random.nextInt(maze.getWidth());
                y = random.nextInt(maze.getHeight());
            } while (!maze.isValidMove(x, y) || (x == player.getX() && y == player.getY()));

            // Add obstacle to maze
            maze.addObstacle(new Obstacle(x, y, type));

            // Use servo and LED to indicate obstacle spawn
            zigBeeManager.sendLedCommand(0); // Red LED
            zigBeeManager.sendServoCommand((x * maze.getHeight() + y) % 16); // Map x,y to 0-15 range

            // Reset LED after a brief moment (in real implementation)
            zigBeeManager.sendLedCommand(7); // LED off
        }
    }

    /**
     * Complete the current level.
     */
    public void levelComplete() {
        // Increase level
        level++;

        // Increase score
        score += 100;

        // Send level complete command to Arduino
        zigBeeManager.sendLevelCompleteCommand();

        // Create a new maze
        this.maze = new Maze(10 + level, 10 + level, level);

        // Reset player position
        this.player.moveTo(maze.getStartX(), maze.getStartY());

        // Reset player state
        this.player.reset();

        // Set state to playing
        setState(playingState);
    }

    /**
     * Start a new game.
     */
    public void startNewGame() {
        // Reset level and score
        level = 1;
        score = 0;

        // Send game start command to Arduino
        zigBeeManager.sendGameStartCommand();

        // Create a new maze
        this.maze = new Maze(10, 10, level);

        // Reset player position
        this.player.moveTo(maze.getStartX(), maze.getStartY());

        // Reset player state
        this.player.reset();

        // Set state to playing
        setState(playingState);
    }

    /**
     * Handle game over.
     */
    public void gameOver() {
        // Send game over command to Arduino
        zigBeeManager.sendGameOverCommand();

        // Set state to game over
        setState(gameOverState);
    }

    /**
     * Reset the game.
     */
    public void resetGame() {
        // Start a new game
        startNewGame();
    }

    /**
     * Set the current state.
     *
     * @param state New state
     */
    public void setState(GameState state) {
        this.state = state;
        state.init(this);
    }

    /**
     * Add an observer.
     *
     * @param observer Observer to add
     */
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove an observer.
     *
     * @param observer Observer to remove
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all observers.
     */
    public void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGameUpdate(this);
        }
    }

    /**
     * Get the current state.
     *
     * @return Current state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Get the playing state.
     *
     * @return Playing state
     */
    public GameState getPlayingState() {
        return playingState;
    }

    /**
     * Get the paused state.
     *
     * @return Paused state
     */
    public GameState getPausedState() {
        return pausedState;
    }

    /**
     * Get the game over state.
     *
     * @return Game over state
     */
    public GameState getGameOverState() {
        return gameOverState;
    }

    /**
     * Get the completed state.
     *
     * @return Completed state
     */
    public GameState getCompletedState() {
        return completedState;
    }

    /**
     * Get the maze.
     *
     * @return Maze
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Get the player.
     *
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if motion is detected.
     *
     * @return true if motion is detected, false otherwise
     */
    public boolean isMotionDetected() {
        return motionDetected;
    }

    /**
     * Set motion detected.
     *
     * @param motionDetected Whether motion is detected
     */
    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    /**
     * Get the current score.
     *
     * @return Current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the current level.
     *
     * @return Current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the ZigBee manager.
     *
     * @return ZigBee manager
     */
    public ZigBeeManager getZigBeeManager() {
        return zigBeeManager;
    }
}
