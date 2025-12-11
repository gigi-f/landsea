package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;

public abstract class BoatPart {
    protected Boat boat;
    protected float gridX, gridY; // Center position in grid coordinates (or top-left? Let's say center for entities, but tiles might be grid indices)
    
    public BoatPart(Boat boat, float gridX, float gridY) {
        this.boat = boat;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public abstract void update(float delta);
    
    public abstract void render(ShapeRenderer shapeRenderer);
    
    public void interact() {}
    
    public float getGridX() { return gridX; }
    public float getGridY() { return gridY; }
    
    protected Vector2 getLocalPosition() {
        float tileSize = boat.getTileSize();
        // Calculate local position relative to boat center
        // Boat center is at (width/2, height/2) in grid coords?
        // In Boat.java: gridOriginX = -(width * tileSize) / 2;
        // partLocalX = gridOriginX + gridX * tileSize + tileSize / 2;
        
        // We need access to boat dimensions.
        // Let's assume Boat has getGridOriginX() or similar, or we calculate it.
        // For now, we'll let the concrete classes handle specific rendering logic or add helper methods to Boat.
        return new Vector2(gridX, gridY);
    }
}
