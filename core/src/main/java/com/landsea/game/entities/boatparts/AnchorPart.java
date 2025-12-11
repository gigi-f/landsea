package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;

public class AnchorPart extends BoatPart {
    private boolean dropped;

    public AnchorPart(Boat boat, int gridX, int gridY) {
        super(boat, gridX, gridY);
        this.dropped = false;
    }

    @Override
    public void update(float delta) {
    }
    
    @Override
    public void interact() {
        toggle();
    }

    public void toggle() {
        dropped = !dropped;
    }

    public boolean isDropped() {
        return dropped;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        float tileSize = boat.getTileSize();
        
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2;
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        float localX = gridOriginX + gridX * tileSize + tileSize / 2;
        float localY = gridOriginY + gridY * tileSize + tileSize / 2;
        
        Vector2 pos = new Vector2(localX, localY);

        // Draw Anchor Base
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.circle(pos.x, pos.y, 5);
        
        if (dropped) {
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rectLine(pos.x, pos.y, pos.x, pos.y - 10, 2);
            shapeRenderer.rectLine(pos.x - 5, pos.y - 10, pos.x + 5, pos.y - 10, 2);
        }
    }
}
