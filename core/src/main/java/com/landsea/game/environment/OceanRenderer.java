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
        
        // Draw Wind Lines (moved from GameScreen)
        shapeRenderer.setColor(0.0f, 0.5f, 0.7f, 0.5f);
        float windAngle = windVector.angleDeg();
        
        float camX = camera.position.x;
        float camY = camera.position.y;
        float spacing = 100f;
        
        // Snap grid to avoid jitter
        float startX = (float)Math.floor((camX - 700) / spacing) * spacing;
        float startY = (float)Math.floor((camY - 400) / spacing) * spacing;
        
        for (float x = startX; x < camX + 700; x += spacing) {
            for (float y = startY; y < camY + 400; y += spacing) {
                // Offset based on time to make them move
                float offsetX = (System.currentTimeMillis() / 10f) % spacing;
                float drawX = x + (windVector.x > 0 ? offsetX : -offsetX);
                
                shapeRenderer.rect(drawX, y, 20, 2, 10, 1, 1, 1, windAngle);
            }
        }
        shapeRenderer.end();
    }
}
