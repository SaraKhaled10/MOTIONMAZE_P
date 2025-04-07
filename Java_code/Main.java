import javax.swing.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class for Motion Maze.
 */
public class Main {
    /**
     * Main method.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting Motion Maze...");

        // Create the game engine
        GameEngine gameEngine = new GameEngine();

        // Create the UI on the EDT
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame(gameEngine);
            gameFrame.setVisible(true);

            // Start the game engine
            gameEngine.start();

            // Start the game loop
            startGameLoop(gameEngine);

            // Start a status reporter (for headless environments)
            startStatusReporter(gameEngine);
        });
    }

    /**
     * Start the game loop.
     *
     * @param gameEngine Game engine to update
     */
    private static void startGameLoop(GameEngine gameEngine) {
        Thread gameLoop = new Thread(() -> {
            final long TARGET_FPS = 60;
            final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

            long lastUpdateTime = System.nanoTime();
            long lastFpsTime = 0;
            int fps = 0;

            while (true) {
                long currentTime = System.nanoTime();
                long updateLength = currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                double delta = updateLength / ((double) OPTIMAL_TIME);

                lastFpsTime += updateLength;
                fps++;

                if (lastFpsTime >= 1000000000) {
                    lastFpsTime = 0;
                    fps = 0;
                }

                // Update the game engine
                gameEngine.update();

                try {
                    long sleepTime = (lastUpdateTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        gameLoop.start();
    }

    /**
     * Start a status reporter to regularly print game status.
     * This is especially useful in headless environments where the UI isn't visible.
     *
     * @param gameEngine Game engine to report status for
     */
    private static void startStatusReporter(GameEngine gameEngine) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("\n--- MOTION MAZE STATUS REPORT ---");
            System.out.println("Game State: " + gameEngine.getState().getClass().getSimpleName());
            System.out.println("Player Position: (" + gameEngine.getPlayer().getX() + "," + gameEngine.getPlayer().getY() + ")");
            System.out.println("Player Health: " + gameEngine.getPlayer().getHealth());
            System.out.println("Player Score: " + gameEngine.getPlayer().getScore());
            System.out.println("Level: " + gameEngine.getLevel());
            System.out.println("Maze Size: " + gameEngine.getMaze().getWidth() + "x" + gameEngine.getMaze().getHeight());
            System.out.println("Power-ups Remaining: " + gameEngine.getMaze().getPowerUps().size());
            System.out.println("Obstacles Remaining: " + gameEngine.getMaze().getObstacles().size());
            System.out.println("ZigBee Status: " + (gameEngine.getZigBeeManager().isConnected() ? "Connected" : "Disconnected"));
            System.out.println("ZigBee Mode: " + (gameEngine.getZigBeeManager().isSimulationMode() ? "Simulation" : "Hardware"));
            System.out.println("Special States:");
            System.out.println("  - Trapped: " + gameEngine.getPlayer().isTrapped());
            System.out.println("  - Shielded: " + gameEngine.getPlayer().isShielded());
            System.out.println("  - Speed Boost: " + gameEngine.getPlayer().hasSpeedBoost());
            System.out.println("  - Slowed: " + gameEngine.getPlayer().isSlowed());
            System.out.println("  - Has Key: " + gameEngine.getPlayer().hasKey());
            System.out.println("----------------------------------\n");

            // Simulate some random event occasionally to demonstrate the game's functionality
            if (Math.random() < 0.3) { // 30% chance
                int randomEvent = (int)(Math.random() * 6) + 1;
                switch(randomEvent) {
                    case 1:
                        System.out.println("ACTION: Moving UP");
                        gameEngine.processMovement(1); // Up
                        break;
                    case 2:
                        System.out.println("ACTION: Moving RIGHT");
                        gameEngine.processMovement(2); // Right
                        break;
                    case 3:
                        System.out.println("ACTION: Moving DOWN");
                        gameEngine.processMovement(3); // Down
                        break;
                    case 4:
                        System.out.println("ACTION: Moving LEFT");
                        gameEngine.processMovement(4); // Left
                        break;
                    case 5:
                        System.out.println("ACTION: Button Press");
                        gameEngine.processButtonPress(); // Button press
                        break;
                    case 6:
                        System.out.println("ACTION: Motion Detected");
                        gameEngine.processMotionDetection(); // Motion detection
                        break;
                }
            }

        }, 3, 5, TimeUnit.SECONDS);
    }
}
