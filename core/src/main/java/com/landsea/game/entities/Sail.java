package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Sail {
    private int gridX, gridY;
    private float angle; // In degrees
    private boolean raised;
    private float size = 20f;

    public Sail(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.angle = 0;
        this.raised = true;
    }

    public void toggleRaise() {
        raised = !raised;
    }

    public void rotate(float amount) {
        angle += amount;
    }

    public void render(ShapeRenderer shapeRenderer, Vector2 boatPos, float tileSize, int boatWidth, int boatHeight, Vector2 windVector) {
        float startX = boatPos.x - (boatWidth * tileSize) / 2;
        float startY = boatPos.y - (boatHeight * tileSize) / 2;
        
        float centerX = startX + gridX * tileSize + tileSize / 2;
        float centerY = startY + gridY * tileSize + tileSize / 2;

        // Draw mast
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(centerX, centerY, 5);

        if (raised) {
            shapeRenderer.setColor(Color.RED);
            
            // Calculate angles
            float windAngle = windVector.angleDeg();
            float windStrength = windVector.len();
            
            // Yardarm is perpendicular to wind (90 degrees offset)
            float yardarmAngle = windAngle + 90;
            float rad = (float) Math.toRadians(yardarmAngle);
            
            // Yardarm endpoints
            float dx = (float) Math.cos(rad) * size;
            float dy = (float) Math.sin(rad) * size;
            float x1 = centerX - dx;
            float y1 = centerY - dy;
            float x2 = centerX + dx;
            float y2 = centerY + dy;
            
            // Draw Yardarm
            shapeRenderer.rectLine(x1, y1, x2, y2, 4);
            
            // Calculate "Belly" point
            // The sail curves DOWNWIND.
            // Midpoint of yardarm is (centerX, centerY).
            // We want a point in the direction of the wind.
            float windRad = (float) Math.toRadians(windAngle);
            // Billow amount depends on wind strength. Max billow = size.
            float billowAmount = Math.min(size, windStrength * 0.2f); 
            
            float bellyX = centerX + (float)Math.cos(windRad) * billowAmount;
            float bellyY = centerY + (float)Math.sin(windRad) * billowAmount;
            
            // Draw Curve (approximated by two lines for now, or many small lines)
            // Bezier: P0(x1,y1), P1(bellyX, bellyY), P2(x2,y2)
            // Actually, P1 should be further out so the curve passes through bellyX/Y?
            // For a quadratic bezier, the curve passes through t=0.5 at 0.25*P0 + 0.5*P1 + 0.25*P2.
            // If P0 and P2 are symmetric around Center, then Mid = 0.5*Center + 0.5*P1.
            // So P1 = 2*Belly - Center.
            
            float controlX = 2 * bellyX - centerX;
            float controlY = 2 * bellyY - centerY;
            
            // Draw curve using segments
            int segments = 10;
            float prevX = x1;
            float prevY = y1;
            
            for (int i = 1; i <= segments; i++) {
                float t = i / (float)segments;
                float invT = 1 - t;
                
                // Quadratic Bezier formula
                float px = (invT * invT * x1) + (2 * invT * t * controlX) + (t * t * x2);
                float py = (invT * invT * y1) + (2 * invT * t * controlY) + (t * t * y2);
                
                shapeRenderer.rectLine(prevX, prevY, px, py, 3);
                prevX = px;
                prevY = py;
            }
        }
    }

    public boolean isRaised() {
        return raised;
    }

    public float getAngle() {
        return angle;
    }
    
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
}
