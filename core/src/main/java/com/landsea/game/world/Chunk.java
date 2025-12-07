package com.landsea.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Chunk {
    public static final int CHUNK_SIZE = 16; // Tiles per chunk axis
    public static final float TILE_SIZE = 32f; // Pixels per tile
    
    private int chunkX, chunkY;
    private boolean[][] tiles; // true = land, false = water
    
    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.tiles = new boolean[CHUNK_SIZE][CHUNK_SIZE];
    }
    
    public void setTile(int x, int y, boolean isLand) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            tiles[x][y] = isLand;
        }
    }
    
    public boolean isLand(int x, int y) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            return tiles[x][y];
        }
        return false;
    }
    
    public void render(ShapeRenderer shapeRenderer, Vector2 cameraPos, float viewportWidth, float viewportHeight) {
        float worldX = chunkX * CHUNK_SIZE * TILE_SIZE;
        float worldY = chunkY * CHUNK_SIZE * TILE_SIZE;
        
        // Culling: Check if chunk is visible
        if (worldX + CHUNK_SIZE * TILE_SIZE < cameraPos.x - viewportWidth / 2 ||
            worldX > cameraPos.x + viewportWidth / 2 ||
            worldY + CHUNK_SIZE * TILE_SIZE < cameraPos.y - viewportHeight / 2 ||
            worldY > cameraPos.y + viewportHeight / 2) {
            return;
        }
        
        shapeRenderer.setColor(new Color(0.2f, 0.8f, 0.3f, 1f)); // Green for land
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                if (tiles[x][y]) {
                    shapeRenderer.rect(worldX + x * TILE_SIZE, worldY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
    
    public int getChunkX() { return chunkX; }
    public int getChunkY() { return chunkY; }
}
