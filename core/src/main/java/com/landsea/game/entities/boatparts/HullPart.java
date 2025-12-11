package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;

public class HullPart extends BoatPart {
    
    public HullPart(Boat boat, int gridX, int gridY) {
        super(boat, gridX, gridY);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        float tileSize = boat.getTileSize();
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2;
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        float localX = gridOriginX + gridX * tileSize;
        float localY = gridOriginY + gridY * tileSize;
        
        shapeRenderer.setColor(new Color(0.6f, 0.4f, 0.2f, 1f)); // Brown color for wood
        shapeRenderer.rect(localX, localY, tileSize, tileSize);
    }
}
