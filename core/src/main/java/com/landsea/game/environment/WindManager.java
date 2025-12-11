package com.landsea.game.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class WindManager {
    private Vector2 windVector;
    private float time;
    private float baseAngle = 0f; // Prevailing wind direction (East)
    private float intensityMultiplier = 1.0f;

    public WindManager() {
        // Start with wind blowing East
        windVector = new Vector2(40f, 0); 
    }
    
    public void setIntensityMultiplier(float multiplier) {
        this.intensityMultiplier = multiplier;
    }

    public void update(float delta) {
        time += delta;
        
        // Real ocean wind has a "Prevailing Direction" (like Trade Winds)
        // It shifts slowly around this base direction over minutes/hours.
        
        // Primary drift: +/- 30 degrees over ~10 minutes (0.01 frequency)
        float slowDrift = 30f * MathUtils.sin(time * 0.01f);
        
        // Gusts: +/- 10 degrees over ~20 seconds (0.3 frequency)
        float gusts = 10f * MathUtils.sin(time * 0.3f);
        
        // Speed variation: +/- 20%
        float speedNoise = 1.0f + 0.2f * MathUtils.sin(time * 0.5f + 1.5f);
        
        windVector.setAngleDeg(baseAngle + slowDrift + gusts);
        windVector.setLength(40f * intensityMultiplier * speedNoise);
    }

    public Vector2 getWindVector() {
        return windVector;
    }
}
