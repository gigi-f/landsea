package com.landsea.game.world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.landsea.game.entities.ResourceNode;

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
        
        // Ensure chunk exists for accurate collision
        if (!chunks.containsKey(key)) {
            Chunk chunk = new Chunk(chunkX, chunkY);
            generator.generateChunk(chunk);
            chunks.put(key, chunk);
        }
        
        if (chunks.containsKey(key)) {
            Chunk chunk = chunks.get(key);
            
            // Local tile coordinates
            float localX = worldX - chunkX * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE;
            float localY = worldY - chunkY * Chunk.CHUNK_SIZE * Chunk.TILE_SIZE;
            
            int tileX = (int) (localX / Chunk.TILE_SIZE);
            int tileY = (int) (localY / Chunk.TILE_SIZE);
            
            return chunk.isLand(tileX, tileY);
        }
        return false;
    }
    
    public void addResourceNode(ResourceNode node) {
        int chunkX = (int) Math.floor(node.getPosition().x / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int chunkY = (int) Math.floor(node.getPosition().y / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        
        String key = chunkX + "," + chunkY;
        if (chunks.containsKey(key)) {
            chunks.get(key).addResourceNode(node);
        }
    }

    public ResourceNode getClosestResource(Vector2 pos, float radius) {
        int chunkX = (int) Math.floor(pos.x / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int chunkY = (int) Math.floor(pos.y / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        
        // Check current and adjacent chunks
        ResourceNode closest = null;
        float minDst2 = radius * radius;
        
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int y = chunkY - 1; y <= chunkY + 1; y++) {
                String key = x + "," + y;
                if (chunks.containsKey(key)) {
                    Chunk chunk = chunks.get(key);
                    for (ResourceNode node : chunk.getResourceNodes()) {
                        if (node.isDepleted()) continue;
                        
                        float dst2 = pos.dst2(node.getPosition());
                        if (dst2 < minDst2) {
                            minDst2 = dst2;
                            closest = node;
                        }
                    }
                }
            }
        }
        return closest;
    }

    public java.util.List<ResourceNode> getVisibleResourceNodes(Vector2 cameraPos, float viewportWidth, float viewportHeight) {
        java.util.List<ResourceNode> visibleNodes = new java.util.ArrayList<>();
        
        // Calculate visible chunks range
        int startChunkX = (int) Math.floor((cameraPos.x - viewportWidth/2) / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int endChunkX = (int) Math.floor((cameraPos.x + viewportWidth/2) / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int startChunkY = (int) Math.floor((cameraPos.y - viewportHeight/2) / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        int endChunkY = (int) Math.floor((cameraPos.y + viewportHeight/2) / (Chunk.CHUNK_SIZE * Chunk.TILE_SIZE));
        
        // Add a buffer of 1 chunk
        startChunkX--; endChunkX++;
        startChunkY--; endChunkY++;
        
        for (int x = startChunkX; x <= endChunkX; x++) {
            for (int y = startChunkY; y <= endChunkY; y++) {
                String key = x + "," + y;
                if (chunks.containsKey(key)) {
                    visibleNodes.addAll(chunks.get(key).getResourceNodes());
                }
            }
        }
        return visibleNodes;
    }
    
    public Vector2 findSafeSpawn() {
        // Search for a water tile starting from (0,0)
        // We check in increments of TILE_SIZE * 5 to find a clear spot
        int radius = 0;
        int maxRadius = 500; // Significantly increased search range
        float step = Chunk.TILE_SIZE * 5;
        
        System.out.println("Searching for safe spawn...");
        
        while (radius < maxRadius) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    // Only check the perimeter of the current radius box
                    if (Math.abs(x) != radius && Math.abs(y) != radius) continue;
                    
                    float checkX = x * step;
                    float checkY = y * step;
                    
                    if (isOpenOcean(checkX, checkY)) {
                        System.out.println("Found spawn at: " + checkX + ", " + checkY);
                        return new Vector2(checkX, checkY);
                    }
                }
            }
            radius++;
        }
        
        System.out.println("Failed to find safe spawn, using fallback (0,0)");
        return new Vector2(0, 0); // Fallback
    }
    
    private boolean isOpenOcean(float x, float y) {
        // 1. Check immediate area (Boat Clearance)
        // Check a grid of points around the center to ensure the boat doesn't spawn on a small island
        // The boat rotates, so we need a circular clearance, but a square grid check is sufficient and simple.
        int radiusTiles = 4; // 4 * 32 = 128px radius. Covers the boat (approx 70px radius)
        
        for (int i = -radiusTiles; i <= radiusTiles; i++) {
            for (int j = -radiusTiles; j <= radiusTiles; j++) {
                if (isLand(x + i * Chunk.TILE_SIZE, y + j * Chunk.TILE_SIZE)) {
                    return false;
                }
            }
        }

        // 2. Check a wider area to ensure we aren't in a small lake
        // Check 8 points at a distance of 20 tiles (approx 1.2 chunks)
        float checkDist = Chunk.TILE_SIZE * 20;
        
        // Check 8 directions
        if (isLand(x + checkDist, y)) return false;
        if (isLand(x - checkDist, y)) return false;
        if (isLand(x, y + checkDist)) return false;
        if (isLand(x, y - checkDist)) return false;
        if (isLand(x + checkDist, y + checkDist)) return false;
        if (isLand(x + checkDist, y - checkDist)) return false;
        if (isLand(x - checkDist, y + checkDist)) return false;
        if (isLand(x - checkDist, y - checkDist)) return false;
        
        return true;
    }
}
