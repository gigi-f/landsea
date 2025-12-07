package com.landsea.game.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class WindManager {
    private Vector2 windVector;
    private float time;
    private float baseAngle = 0f; // Prevailing wind direction (East)

    public WindManager() {
        // Start with wind blowing East
        windVector = new Vector2(100f, 0); 
    }

    public void update(float delta) {
        time += delta;
        
        // Real ocean wind has a "Prevailing Direction" (like Trade Winds)
        // It shifts slowly around this base direction over minutes/hours.
        
        // Primary drift: +/- 30 degrees over ~2 minutes (0.05 frequency)
        float slowDrift = 30f * MathUtils.sin(time * 0.05f);
        
        // Gusts: +/- 5 degrees over ~10 seconds (0.6 frequency)
        float gusts = 5f * MathUtils.sin(time * 0.6f);
        
        windVector.setAngleDeg(baseAngle + slowDrift + gusts);
    }

    public Vector2 getWindVector() {
        return windVector;
    }
}
