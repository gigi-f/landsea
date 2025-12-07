package com.landsea.game.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class WorldManager {
    private ObjectMap<String, Chunk> chunks;
    private IslandGenerator generator;
    private int renderDistance = 2; // Chunks radius
    
    public WorldManager() {
        chunks = new ObjectMap<>();
        generator = new IslandGenerator(12345); // Fixed seed for now
    }
    
    public void update(Vector2 playerPos) {
        int pChunkX = (int) Math.floor(playerPos.x / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int pChunkY = (int) Math.floor(playerPos.y / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        
        // Load chunks around player
        for (int x = pChunkX - renderDistance; x <= pChunkX + renderDistance; x++) {
            for (int y = pChunkY - renderDistance; y <= pChunkY + renderDistance; y++) {
                String key = x + "," + y;
                if (!chunks.containsKey(key)) {
                    Chunk chunk = new Chunk(x, y);
                    generator.generateChunk(chunk);
                    chunks.put(key, chunk);
                }
            }
        }
        
        // Unload far chunks (optional, for memory)
        // For now, keep them simple
    }
    
    public void render(ShapeRenderer shapeRenderer, Vector2 cameraPos, float viewportWidth, float viewportHeight) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Chunk chunk : chunks.values()) {
            chunk.render(shapeRenderer, cameraPos, viewportWidth, viewportHeight);
        }
        shapeRenderer.end();
    }
    
    public boolean isLand(float worldX, float worldY) {
        int chunkX = (int) Math.floor(worldX / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int chunkY = (int) Math.floor(worldY / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        
        String key = chunkX + "," + chunkY;
        if (chunks.containsKey(key)) {
            Chunk chunk = chunks.get(key);
            
            // Local tile coordinates
            float localX = worldX - chunkX * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE;
            float localY = worldY - chunkY * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE;
            
            int tileX = (int) (localX / Chunk.TILE_SIZE);
            int tileY = (int) (localY / Chunk.TILE_SIZE);
            
            return chunk.isLand(tileX, tileY);
        }
        // System.out.println("Chunk not found for " + worldX + ", " + worldY + " (Key: " + key + ")");
        return false;
    }
}
