//package com.motionmaze.game;

/**
 * Obstacle in the game.
 */
public class Obstacle {
    private int x;
    private int y;
    private ObstacleType type;

    /**
     * Constructor for creating a new obstacle.
     *
     * @param x X position
     * @param y Y position
     * @param type Type of obstacle
     */
    public Obstacle(int x, int y, ObstacleType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Apply the obstacle effect to the player.
     *
     * @param player Player to apply the obstacle effect to
     */
    public void apply(Player player) {
        switch (type) {
            case TRAP:
                player.takeDamage(10);
                break;

            case ENEMY:
                player.takeDamage(20);
                break;

            case PIT:
                player.takeDamage(30);
                break;

            case FIRE:
                player.takeDamage(15);
                break;

            case ICE:
                // Ice would be handled by the game engine
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
     * Get the type of obstacle.
     *
     * @return Type of obstacle
     */
    public ObstacleType getType() {
        return type;
    }
}
