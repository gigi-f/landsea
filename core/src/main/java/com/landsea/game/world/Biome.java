package com.landsea.game.world;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    OCEAN(new Color(0, 0.4f, 0.6f, 1)), // Deep water
    TROPICAL(new Color(0.9f, 0.8f, 0.5f, 1)), // Sand/Beach
    GRASSLAND(new Color(0.2f, 0.8f, 0.3f, 1)), // Standard Green
    SWAMP(new Color(0.3f, 0.4f, 0.2f, 1)), // Murky Green
    JUNGLE(new Color(0.0f, 0.5f, 0.1f, 1)), // Dark Green
    ROCKY(new Color(0.5f, 0.5f, 0.5f, 1)); // Grey

    private final Color color;

    Biome(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
