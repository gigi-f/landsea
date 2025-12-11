package com.landsea.game.world;

import java.util.Random;
import com.landsea.game.entities.ResourceNode;

public class IslandGenerator {
    private long seed;
    private int[] permutation;

    public IslandGenerator(long seed) {
        this.seed = seed;
        initPermutation();
    }

    private void initPermutation() {
        permutation = new int[512];
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;

        Random r = new Random(seed);
        // Shuffle
        for (int i = 255; i > 0; i--) {
            int index = r.nextInt(i + 1);
            int temp = p[index];
            p[index] = p[i];
            p[i] = temp;
        }

        for (int i = 0; i < 512; i++) {
            permutation[i] = p[i & 255];
        }
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0; // Fix for 2D
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int A = permutation[X] + Y;
        int AA = permutation[A];
        int AB = permutation[A + 1];
        int B = permutation[X + 1] + Y;
        int BA = permutation[B];
        int BB = permutation[B + 1];

        return lerp(v, lerp(u, grad(permutation[AA], x, y), grad(permutation[BA], x - 1, y)),
                lerp(u, grad(permutation[AB], x, y - 1), grad(permutation[BB], x - 1, y - 1)));
    }

    public void generateChunk(Chunk chunk) {
        int cx = chunk.getChunkX();
        int cy = chunk.getChunkY();
        
        float scale = 0.05f; // Zoom level for noise
        float moistureScale = 0.02f; // Larger biomes
        float clusterScale = 0.005f; // Very low frequency for archipelagos

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                float worldX = cx * Chunk.CHUNK_SIZE + x;
                float worldY = cy * Chunk.CHUNK_SIZE + y;
                
                // Archipelago Mask
                double clusterN = noise(worldX * clusterScale, worldY * clusterScale);
                
                double n = noise(worldX * scale, worldY * scale);
                
                // Normalize noise to 0-1 roughly (Perlin is usually -1 to 1)
                float height = (float) (n + 1.0) / 2.0f;
                
                // Add some detail with higher frequency noise (Octaves)
                double n2 = noise(worldX * scale * 2.0, worldY * scale * 2.0);
                height += (float) (n2 * 0.5) / 2.0f;
                
                // Apply Archipelago Clustering
                // Raise terrain in clusters, lower it in gaps
                height += (float)clusterN * 0.4f;
                
                // Clamp
                if (height < 0) height = 0;
                if (height > 1) height = 1;
                
                chunk.setHeight(x, y, height);
                
                // Biome Generation
                double m = noise(worldX * moistureScale + 500, worldY * moistureScale + 500); // Offset for moisture
                float moisture = (float) (m + 1.0) / 2.0f;
                
                Biome biome = Biome.OCEAN;
                if (height > 0.5f) {
                    if (height < 0.55f) {
                        biome = Biome.TROPICAL; // Beach
                    } else if (height > 0.85f) {
                        biome = Biome.ROCKY; // Mountain
                    } else {
                        if (moisture < 0.4f) {
                            biome = Biome.GRASSLAND;
                        } else if (moisture < 0.7f) {
                            biome = Biome.JUNGLE;
                        } else {
                            biome = Biome.SWAMP;
                        }
                    }
                }
                chunk.setBiome(x, y, biome);
                
                // Resource Generation
                if (height > 0.5f) { // Land
                    // Deterministic random for this tile
                    long tileSeed = (long)(worldX * 31 + worldY * 17) + seed;
                    Random tileRand = new Random(tileSeed);
                    
                    float worldPixelX = (cx * Chunk.CHUNK_SIZE + x) * Chunk.TILE_SIZE + Chunk.TILE_SIZE / 2;
                    float worldPixelY = (cy * Chunk.CHUNK_SIZE + y) * Chunk.TILE_SIZE + Chunk.TILE_SIZE / 2;
                    
                    if (biome == Biome.TROPICAL) {
                        if (tileRand.nextFloat() < 0.05f) { // Coconut Palm
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.PALM_TREE));
                        }
                    } else if (biome == Biome.GRASSLAND) {
                        if (tileRand.nextFloat() < 0.02f) {
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.BERRY_BUSH));
                        }
                    } else if (biome == Biome.JUNGLE) {
                        if (tileRand.nextFloat() < 0.15f) { // Dense trees
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.MAHOGANY));
                        } else if (tileRand.nextFloat() < 0.1f) {
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.BUSH));
                        }
                    } else if (biome == Biome.SWAMP) {
                        if (tileRand.nextFloat() < 0.05f) {
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.MANGROVE)); // Mangrove
                        }
                    } else if (biome == Biome.ROCKY) {
                        if (tileRand.nextFloat() < 0.05f) {
                            chunk.addResourceNode(new ResourceNode(worldPixelX, worldPixelY, ResourceNode.Type.ROCK));
                        }
                    }
                }
            }
        }
    }
}
