package com.landsea.game.world;

import java.util.Random;

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
        float threshold = 0.2f; // Water level (higher = less land)

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                float worldX = cx * Chunk.CHUNK_SIZE + x;
                float worldY = cy * Chunk.CHUNK_SIZE + y;
                
                double n = noise(worldX * scale, worldY * scale);
                
                // Simple island generation: if noise > threshold, it's land
                if (n > threshold) {
                    chunk.setTile(x, y, true);
                }
            }
        }
    }
}
