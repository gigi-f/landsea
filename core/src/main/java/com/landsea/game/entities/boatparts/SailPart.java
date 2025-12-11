package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;

public class SailPart extends BoatPart {
    private boolean raised;

    public SailPart(Boat boat, int gridX, int gridY) {
        super(boat, gridX, gridY);
        this.raised = true;
    }

    @Override
    public void update(float delta) {
    }
    
    @Override
    public void interact() {
        toggleRaise();
    }

    public void toggleRaise() {
        raised = !raised;
    }
    
    public boolean isRaised() {
        return raised;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        // Default render if no wind provided
        render(shapeRenderer, new Vector2(1, 0));
    }
    
    public void render(ShapeRenderer shapeRenderer, Vector2 windVector) {
        float tileSize = boat.getTileSize();
        
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2;
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        float localX = gridOriginX + gridX * tileSize + tileSize / 2;
        float localY = gridOriginY + gridY * tileSize + tileSize / 2;
        
        Vector2 pos = new Vector2(localX, localY);
        
        // Draw mast
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(pos.x, pos.y, 5);

        if (raised) {
            shapeRenderer.setColor(Color.WHITE); // White sail
            float windAngle = windVector.angleDeg();
            float yardarmAngle = windAngle + 90;
            float rad = (float) Math.toRadians(yardarmAngle);
            
            float sailWidth = 20f;
            float x1 = pos.x + (float)Math.cos(rad) * sailWidth;
            float y1 = pos.y + (float)Math.sin(rad) * sailWidth;
            float x2 = pos.x - (float)Math.cos(rad) * sailWidth;
            float y2 = pos.y - (float)Math.sin(rad) * sailWidth;
            
            // Calculate billow point (center of sail pushed by wind)
            Vector2 windDir = windVector.cpy().nor();
            float billowAmount = 15f;
            float midX = pos.x + windDir.x * billowAmount;
            float midY = pos.y + windDir.y * billowAmount;
            
            // Draw two segments to approximate a curve
            shapeRenderer.rectLine(x1, y1, midX, midY, 4);
            shapeRenderer.rectLine(midX, midY, x2, y2, 4);
        }
    }
}
