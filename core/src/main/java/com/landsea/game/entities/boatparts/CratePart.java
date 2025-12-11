package com.landsea.game.entities.boatparts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.Boat;
import com.landsea.game.inventory.Inventory;

public class CratePart extends BoatPart {
    private Inventory inventory;

    public CratePart(Boat boat, float gridX, float gridY) {
        super(boat, gridX, gridY);
        this.inventory = new Inventory(5); // Small crate, 5 slots
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        float tileSize = boat.getTileSize();
        
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2;
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        float localX = gridOriginX + gridX * tileSize + tileSize / 2;
        float localY = gridOriginY + gridY * tileSize + tileSize / 2;
        
        Vector2 pos = new Vector2(localX, localY);

        // Draw Crate
        shapeRenderer.setColor(new Color(0.5f, 0.3f, 0.1f, 1f)); // Darker brown
        shapeRenderer.rect(pos.x - 10, pos.y - 10, 20, 20);
        
        // Draw lid detail
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0.05f, 1f));
        shapeRenderer.rectLine(pos.x - 10, pos.y, pos.x + 10, pos.y, 2);
    }
    
    @Override
    public void interact() {
        // TODO: Open inventory UI
        System.out.println("Interacted with Crate. Items: " + inventory.getSlots().size());
    }
    
    public Inventory getInventory() {
        return inventory;
    }
}
