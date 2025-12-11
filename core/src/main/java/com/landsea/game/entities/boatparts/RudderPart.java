package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;

public class RudderPart extends BoatPart {
    private float angle; // Relative to boat
    private float maxAngle = 45f;
    private float turnSpeed = 90f;

    public RudderPart(Boat boat, float gridX, float gridY) {
        super(boat, gridX, gridY);
        this.angle = 0;
    }

    @Override
    public void update(float delta) {
    }
    
    public void updateInput(float delta, float input) {
        if (input != 0) {
            angle -= input * turnSpeed * delta;
            angle = MathUtils.clamp(angle, -maxAngle, maxAngle);
        }
    }
    
    public float getAngle() {
        return angle;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        float tileSize = boat.getTileSize();
        
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2;
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        float localX = gridOriginX + gridX * tileSize + tileSize / 2;
        float localY = gridOriginY + gridY * tileSize + tileSize / 2;
        
        Vector2 pos = new Vector2(localX, localY);

        // Draw Tiller Base
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.circle(pos.x, pos.y, 4);

        // Draw Rudder Blade
        // Assuming boat forward is +Y relative to boat rotation
        // So back is -Y, which is -90 degrees relative to forward (if forward is 90)
        // Let's assume boatAngle 0 = North (0,1) based on Boat.java logic
        // So back is South (270 or -90)
        
        // Actually, let's just use the boat's forward vector and rotate 180
        // We are in local space, so boat angle is effectively 0.
        float totalAngle = 180 + angle;
        
        float bladeLength = 15f;
        float rad = (float) Math.toRadians(totalAngle + 90); // +90 because Math.cos/sin 0 is East, but we want North to be 0?
        // Standard math: 0 is East.
        // If boatAngle 0 is North (0,1).
        // Then totalAngle 180 is South (0,-1).
        // Math.toRadians(180) -> cos=-1, sin=0 -> (-1,0) West.
        // So we need -90 offset to align math 0 (East) with North? Or +90?
        // If we want 0 to be North (0,1):
        // cos(90) = 0, sin(90) = 1.
        // So we add 90 to our "North-based" angle to get "Math-based" angle.
        
        rad = (float) Math.toRadians(totalAngle + 90);
        
        float endX = pos.x + (float)Math.cos(rad) * bladeLength;
        float endY = pos.y + (float)Math.sin(rad) * bladeLength;
        
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0.1f, 1f));
        shapeRenderer.rectLine(pos.x, pos.y, endX, endY, 3);
    }
}
