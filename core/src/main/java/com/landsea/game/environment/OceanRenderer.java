package com.landsea.game.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class OceanRenderer {
    private Array<Whitecap> whitecaps;
    private float spawnTimer;
    private float stateTime;
    
    private class Whitecap {
        Vector2 position;
        float life;
        float maxLife;
        
        public Whitecap(float x, float y) {
            this.position = new Vector2(x, y);
            this.maxLife = MathUtils.random(1.0f, 3.0f);
            this.life = maxLife;
        }
    }
    
    public OceanRenderer() {
        whitecaps = new Array<>();
    }
    
    public void update(float delta, OrthographicCamera camera) {
        stateTime += delta;
        // Spawn new whitecaps around the camera
        spawnTimer += delta;
        if (spawnTimer > 0.1f) { // Spawn rate
            spawnTimer = 0;
            // Spawn in a larger area than the camera to avoid popping
            float rangeX = camera.viewportWidth * 1.5f;
            float rangeY = camera.viewportHeight * 1.5f;
            
            float x = camera.position.x + MathUtils.random(-rangeX/2, rangeX/2);
            float y = camera.position.y + MathUtils.random(-rangeY/2, rangeY/2);
            
            whitecaps.add(new Whitecap(x, y));
        }
        
        // Update existing
        for (int i = whitecaps.size - 1; i >= 0; i--) {
            Whitecap w = whitecaps.get(i);
            w.life -= delta;
            if (w.life <= 0) {
                whitecaps.removeIndex(i);
            }
        }
    }
    
    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera, Vector2 windVector) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Draw Whitecaps
        shapeRenderer.setColor(1f, 1f, 1f, 0.6f);
        
        for (Whitecap w : whitecaps) {
            // Only draw if visible
            if (camera.frustum.pointInFrustum(w.position.x, w.position.y, 0)) {
                // Fade in/out
                float alpha = 1.0f;
                if (w.life < 0.5f) alpha = w.life / 0.5f;
                if (w.life > w.maxLife - 0.5f) alpha = (w.maxLife - w.life) / 0.5f;
                
                shapeRenderer.setColor(1f, 1f, 1f, alpha * 0.5f);
                
                // Draw a small curve or line
                shapeRenderer.rect(w.position.x, w.position.y, 10, 2);
            }
        }
        shapeRenderer.end();
    }
    
    public void renderRain(ShapeRenderer shapeRenderer, OrthographicCamera camera, WeatherManager weatherManager) {
        if (weatherManager.getCurrentState() == WeatherManager.WeatherState.CLEAR) return;
        
        float intensity = (weatherManager.getCurrentState() == WeatherManager.WeatherState.STORM) ? 2.0f : 1.0f;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.7f, 0.7f, 0.8f, 0.5f);
        
        // Draw rain streaks
        int drops = (int)(500 * intensity);
        float fallSpeed = 800f;
        float windX = (weatherManager.getCurrentState() == WeatherManager.WeatherState.STORM) ? -200f : -50f;
        
        float viewW = camera.viewportWidth;
        float viewH = camera.viewportHeight;
        float camX = camera.position.x;
        float camY = camera.position.y;
        
        for (int i = 0; i < drops; i++) {
            // Pseudo-random based on index and time
            float r1 = (float)Math.sin(i * 12.9898 + stateTime * 0.1f);
            float r2 = (float)Math.cos(i * 78.233 + stateTime * 0.1f);
            
            // Normalize to 0-1
            r1 = (r1 + 1) / 2;
            r2 = (r2 + 1) / 2;
            
            // Position relative to camera, wrapping
            float x = (r1 * viewW + stateTime * windX) % viewW;
            float y = (r2 * viewH - stateTime * fallSpeed) % viewH;
            
            // Adjust to camera space
            if (x < 0) x += viewW;
            if (y < 0) y += viewH;
            
            x += camX - viewW/2;
            y += camY - viewH/2;
            
            shapeRenderer.line(x, y, x + windX * 0.05f, y - fallSpeed * 0.05f);
        }
        
        shapeRenderer.end();
    }
}
