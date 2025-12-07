package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 localPosition; // Position relative to the boat center
    private Boat boat;
    private float speed = 150f; // Pixels per second
    private float size = 10f;
    
    private boolean isSteering = false;

    public Player(Boat boat) {
        this.boat = boat;
        this.localPosition = new Vector2(0, 0); // Start at center of boat
    }

    public void update(float delta, float moveX, float moveY) {
        if (isSteering) {
            // If steering, input controls the rudder, not player movement
            Rudder rudder = boat.getRudder();
            if (rudder != null) {
                rudder.update(delta, moveX); // Use horizontal input for rudder
            }
            return; // Don't move player
        }
        
        // Normalize diagonal movement
        if (moveX != 0 && moveY != 0) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }

        float nextX = localPosition.x + moveX * speed * delta;
        float nextY = localPosition.y + moveY * speed * delta;

        // Check collision with boat deck
        if (boat.isTileWalkable(nextX, localPosition.y)) {
            localPosition.x = nextX;
        }
        if (boat.isTileWalkable(localPosition.x, nextY)) {
            localPosition.y = nextY;
        }
    }
    
    public void interact() {
        // If already steering, stop steering
        if (isSteering) {
            isSteering = false;
            return;
        }
        
        float tileSize = boat.getTileSize();
        // Grid origin relative to boat center
        float gridOriginX = -(10 * tileSize) / 2; // Hardcoded width 10 for now
        float gridOriginY = -(10 * tileSize) / 2;
        
        int gridX = (int)((localPosition.x - gridOriginX) / tileSize);
        int gridY = (int)((localPosition.y - gridOriginY) / tileSize);

        Sail sail = boat.getSail();
        Rudder rudder = boat.getRudder();
        
        float distToSail = Float.MAX_VALUE;
        float distToRudder = Float.MAX_VALUE;
        
        // Calculate distance to Sail
        if (sail != null) {
            float dx = gridX - sail.getGridX();
            float dy = gridY - sail.getGridY();
            // Check if within interaction range (1 tile radius)
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                distToSail = dx*dx + dy*dy;
            }
        }
        
        // Calculate distance to Rudder
        if (rudder != null) {
            float dx = gridX - rudder.getGridX();
            float dy = gridY - rudder.getGridY();
            // Check if within interaction range (1 tile radius)
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                distToRudder = dx*dx + dy*dy;
            }
        }
        
        // Interact with the closest object
        if (distToRudder < distToSail) {
            isSteering = true;
        } else if (distToSail < Float.MAX_VALUE) {
            sail.toggleRaise();
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        Vector2 worldPos = getWorldPosition();
        
        if (isSteering) {
            shapeRenderer.setColor(Color.BLUE); // Visual feedback for steering mode
        } else {
            shapeRenderer.setColor(Color.BLACK);
        }
        shapeRenderer.circle(worldPos.x, worldPos.y, size);
    }

    public Vector2 getWorldPosition() {
        // Rotate local position by boat angle
        Vector2 rotatedLocal = localPosition.cpy().rotateDeg(boat.getAngle());
        return new Vector2(boat.getPosition()).add(rotatedLocal);
    }
    
    public Vector2 getLocalPosition() {
        return localPosition;
    }
}
