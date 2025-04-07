//package com.motionmaze.game;

/**
 * Power-up in the game.
 */
public class PowerUp {
    private int x;
    private int y;
    private PowerUpType type;

    /**
     * Constructor for creating a new power-up.
     *
     * @param x X position
     * @param y Y position
     * @param type Type of power-up
     */
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Apply the power-up to the player.
     *
     * @param player Player to apply the power-up to
     */
    public void apply(Player player) {
        switch (type) {
            case HEALTH:
                player.heal(25);
                break;

            case SPEED:
                // Speed boost would be handled by the game engine
                break;

            case KEY:
                player.setHasKey(true);
                break;

            case SCORE_BOOST:
                player.increaseScore(50);
                break;

            case INVINCIBILITY:
                // Invincibility would be handled by the game engine
                break;
        }
    }

    /**
     * Get the X position.
     *
     * @return X position
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y position.
     *
     * @return Y position
     */
    public int getY() {
        return y;
    }

    /**
     * Get the type of power-up.
     *
     * @return Type of power-up
     */
    public PowerUpType getType() {
        return type;
    }
}
