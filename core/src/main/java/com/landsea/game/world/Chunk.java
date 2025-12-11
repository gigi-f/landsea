package com.landsea.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.ResourceNode;
import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public static final int CHUNK_SIZE = 16; // Tiles per chunk axis
    public static final float TILE_SIZE = 32f; // Pixels per tile
    
    private int chunkX, chunkY;
    private float[][] heightMap; // 0.0 - 1.0
    private Biome[][] biomeMap;
    private List<ResourceNode> resourceNodes;
    
    public Chunk(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.heightMap = new float[CHUNK_SIZE][CHUNK_SIZE];
        this.biomeMap = new Biome[CHUNK_SIZE][CHUNK_SIZE];
        this.resourceNodes = new ArrayList<>();
    }
    
    public void addResourceNode(ResourceNode node) {
        resourceNodes.add(node);
    }
    
    public List<ResourceNode> getResourceNodes() {
        return resourceNodes;
    }
    
    public void setHeight(int x, int y, float height) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            heightMap[x][y] = height;
        }
    }
    
    public void setBiome(int x, int y, Biome biome) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            biomeMap[x][y] = biome;
        }
    }
    
    public float getHeight(int x, int y) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            return heightMap[x][y];
        }
        return 0f;
    }
    
    public boolean isLand(int x, int y) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
            return heightMap[x][y] > 0.5f; // Water level (Increased to 0.5 for more water)
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
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                float h = heightMap[x][y];
                if (h > 0.5f) {
                    // Color based on biome and height
                    Biome biome = biomeMap[x][y];
                    if (biome == null) biome = Biome.GRASSLAND; // Fallback
                    
                    shapeRenderer.setColor(biome.getColor());
                    
                    // Slight height shading
                    float shade = 0.8f + h * 0.2f;
                    shapeRenderer.setColor(biome.getColor().r * shade, biome.getColor().g * shade, biome.getColor().b * shade, 1f);
                    
                    shapeRenderer.rect(worldX + x * TILE_SIZE, worldY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        
        // Render resource nodes
        for (ResourceNode node : resourceNodes) {
            node.render(shapeRenderer);
        }
    }
    
    public int getChunkX() { return chunkX; }
    public int getChunkY() { return chunkY; }
}
