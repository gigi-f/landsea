package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rudder {
    private int gridX, gridY;
    private float angle; // Relative to boat, in degrees. Positive = Right, Negative = Left
    private float maxAngle = 45f;
    private float turnSpeed = 90f; // Degrees per second

    public Rudder(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.angle = 0;
    }

    public void update(float delta, float input) {
        // Input: -1 (Left), 1 (Right), 0 (None)
        if (input != 0) {
            angle -= input * turnSpeed * delta; // Invert input because "Left" (A) should turn rudder Left
            angle = MathUtils.clamp(angle, -maxAngle, maxAngle);
        }
    }

    public void render(ShapeRenderer shapeRenderer, Vector2 boatPos, float tileSize, int boatWidth, int boatHeight) {
        float startX = boatPos.x - (boatWidth * tileSize) / 2;
        float startY = boatPos.y - (boatHeight * tileSize) / 2;
        
        float centerX = startX + gridX * tileSize + tileSize / 2;
        float centerY = startY + gridY * tileSize + tileSize / 2;

        // Draw Tiller Base
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.circle(centerX, centerY, 4);

        // Draw Tiller Handle (Visual indication of angle)
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0.1f, 1f)); // Dark wood
        float rad = (float) Math.toRadians(angle + 90); // +90 to point "up" (forward) by default? No, rudder is at back.
        // Actually, let's say angle 0 is straight back.
        // Tiller usually points opposite to rudder blade.
        // Let's just draw a line representing the rudder blade in the water.
        
        float bladeLength = 15f;
        float bladeRad = (float) Math.toRadians(angle - 90); // Pointing down/back
        
        float endX = centerX + (float)Math.cos(bladeRad) * bladeLength;
        float endY = centerY + (float)Math.sin(bladeRad) * bladeLength;
        
        shapeRenderer.rectLine(centerX, centerY, endX, endY, 6);
    }

    public float getAngle() {
        return angle;
    }
    
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
}
