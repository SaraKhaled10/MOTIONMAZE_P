
/**
 * Observer for player updates.
 * Implements the Observer pattern.
 */
public class PlayerObserver implements GameObserver {
    @Override
    public void onGameUpdate(GameEngine gameEngine) {
        Player player = gameEngine.getPlayer();

        // Check if player is trapped
        if (player.isTrapped()) {
            // Player is trapped, can't move
            System.out.println("Player is trapped!");

            // Check if we should release the player (10% chance per update)
            if (Math.random() < 0.1) {
                player.release();
                System.out.println("Player released from trap!");
            }
        }

        // Check if player has a speed boost
        if (player.hasSpeedBoost()) {
            // Speed boost active
            System.out.println("Player has speed boost!");

            // Check if we should remove the speed boost (5% chance per update)
            if (Math.random() < 0.05) {
                player.removeSpeedBoost();
                System.out.println("Speed boost expired!");
            }
        }

        // Check if player is shielded
        if (player.isShielded()) {
            // Shield active
            System.out.println("Player is shielded!");

            // Check if we should remove the shield (5% chance per update)
            if (Math.random() < 0.05) {
                player.removeShield();
                System.out.println("Shield expired!");
            }
        }

        // Check if player is slowed
        if (player.isSlowed()) {
            // Slowness active
            System.out.println("Player is slowed!");

            // Check if we should remove the slowness (10% chance per update)
            if (Math.random() < 0.1) {
                player.removeSlowness();
                System.out.println("Slowness expired!");
            }
        }
    }
}
