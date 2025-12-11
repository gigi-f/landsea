package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.boatparts.AnchorPart;
import com.landsea.game.entities.boatparts.BoatPart;
import com.landsea.game.entities.boatparts.CratePart;
import com.landsea.game.entities.boatparts.HullPart;
import com.landsea.game.entities.boatparts.RudderPart;
import com.landsea.game.entities.boatparts.SailPart;
import com.landsea.game.environment.WindManager;
import java.util.ArrayList;
import java.util.List;

public class Boat {
    private Vector2 position; // World position of the boat's center
    private Vector2 velocity;
    private float angle; // Boat heading in degrees (0 = East/Right, 90 = North/Up)
    private float angularVelocity; // Degrees per second
    
    // Physics Constants
    private float mass = 3000f; // kg (Increased for weight/inertia)
    private float momentOfInertia = 12000f; // High resistance to turning (Heavy boat feel)
    private float dragForward = 0.3f; // Higher drag to limit top speed
    private float dragSideways = 5.0f; // High drag moving sideways (Keel effect)
    private float angularDrag = 10.0f; // High base resistance to spinning
    
    private float kickCooldown = 0f;

    private int width = 10;   // Grid width
    private int height = 10;  // Grid height
    private float tileSize = 32f; // Size of each grid tile in pixels
    
    private List<BoatPart> parts;
    private int tileCount = 0;
    
    // Cached references for physics/gameplay logic
    private SailPart sail;
    private RudderPart rudder;
    private AnchorPart anchor;

    public Boat(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.angle = 0;
        this.angularVelocity = 0;
        this.parts = new ArrayList<>();
        
        // Initialize a simple 2x4 raft in the center of the 10x10 grid
        // 2 wide (x), 4 high (y) - composed of two 2x2 sections
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Center X is 4-5. Center Y is 3-6
                if (x >= 4 && x <= 5 && y >= 3 && y <= 6) {
                    HullPart hull = new HullPart(this, x, y);
                    parts.add(hull);
                    tileCount++;
                }
            }
        }
        
        // Place sail in the middle-ish (on the grid)
        this.sail = new SailPart(this, 4, 4);
        parts.add(this.sail);
        
        // Place rudder at the back (bottom center)
        // Centered between tile 4 and 5 (4.5)
        // Note: RudderPart takes float gridX, gridY
        this.rudder = new RudderPart(this, 4.5f, 3f);
        parts.add(this.rudder);
        
        // Place anchor at the front (top right-ish)
        this.anchor = new AnchorPart(this, 5, 6);
        parts.add(this.anchor);
        
        // Place a crate for testing
        parts.add(new CratePart(this, 5, 4));
    }

    public void addPart(BoatPart part) {
        parts.add(part);
        if (part instanceof HullPart) {
            tileCount++;
            // Recalculate mass?
            mass += 50f;
        }
    }
    
    public boolean isTileOccupied(int gridX, int gridY) {
        for (BoatPart part : parts) {
            if (part instanceof HullPart) {
                if (part.getGridX() == gridX && part.getGridY() == gridY) {
                    return true;
                }
            }
        }
        return false;
    }

    public void update(float delta, WindManager windManager, com.landsea.game.world.WorldManager worldManager) {
        if (kickCooldown > 0) kickCooldown -= delta;

        // Update all parts
        for (BoatPart part : parts) {
            part.update(delta);
        }

        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;

        // 1. Calculate Forces
        Vector2 totalForce = new Vector2(0, 0);
        float totalTorque = 0;
        
        // A. Sail Force (Thrust)
        if (sail != null && sail.isRaised()) {
            Vector2 wind = windManager.getWindVector();
            float windSpeed = wind.len();
            
            // Boat "Forward" is Y-axis (Up) in local space
            Vector2 boatHeading = new Vector2(0, 1).rotateDeg(angle);
            Vector2 windDir = wind.cpy().nor();
            
            float dot = boatHeading.dot(windDir); // 1.0 = Downwind, -1.0 = Upwind
            
            // Calculate Sail Position relative to Center of Mass
            float sailLocalX = gridOriginX + sail.getGridX() * tileSize + tileSize / 2;
            float sailLocalY = gridOriginY + sail.getGridY() * tileSize + tileSize / 2;
            Vector2 sailPos = new Vector2(sailLocalX, sailLocalY);
            
            Vector2 clr = new Vector2(0, 0);
            Vector2 leverArm = sailPos.cpy().sub(clr);
            
            Vector2 windForce = new Vector2(0, 0);
            
            if (dot > -0.75f) { 
                // Sailing Mode
                float efficiency = (dot + 0.75f) / 1.75f; 
                float thrustMag = windSpeed * efficiency * 500.0f; // Reduced thrust for slower acceleration
                Vector2 forwardThrust = boatHeading.cpy().scl(thrustMag);
                
                // Sideways Force
                float cross = boatHeading.x * windDir.y - boatHeading.y * windDir.x;
                Vector2 sideDir = boatHeading.cpy().rotate90(cross > 0 ? -1 : 1); 
                float sideFactor = 1.0f - (dot + 1.0f) / 2.0f; 
                float sideMag = windSpeed * sideFactor * 500.0f; // Reduced side force
                
                Vector2 sideForce = sideDir.scl(sideMag);
                windForce = forwardThrust.add(sideForce);
            } else {
                // In Irons
                float dragMag = windSpeed * 50.0f; 
                windForce = windDir.cpy().scl(dragMag);
            }
            
            totalForce.add(windForce);
            
            // Apply Torque
            Vector2 localWindForce = windForce.cpy().rotateDeg(-angle);
            float torque = leverArm.x * localWindForce.y - leverArm.y * localWindForce.x;
            totalTorque += torque * 0.5f; 
        }
        
        // B. Hydrodynamic Forces (Drag & Keel)
        Vector2 localVel = velocity.cpy().rotateDeg(-angle);
        float fDrag = -localVel.y * dragForward * localVel.y * Math.signum(localVel.y); 
        float sDrag = -localVel.x * dragSideways * Math.abs(localVel.x); 
        Vector2 localDragForce = new Vector2(sDrag, fDrag);
        totalForce.add(localDragForce.rotateDeg(angle));
        
        // C. Rudder Forces
        if (rudder != null) {
            float rudderLocalX = gridOriginX + rudder.getGridX() * tileSize + tileSize / 2;
            float rudderLocalY = gridOriginY + rudder.getGridY() * tileSize + tileSize / 2;
            Vector2 rudderPosLocal = new Vector2(rudderLocalX, rudderLocalY);
            
            float omegaRad = angularVelocity * MathUtils.degreesToRadians;
            Vector2 tangentialVel = new Vector2(-omegaRad * rudderPosLocal.y, omegaRad * rudderPosLocal.x);
            Vector2 rudderVel = localVel.cpy().add(tangentialVel);
            Vector2 waterFlow = rudderVel.cpy().scl(-1);
            
            Vector2 rudderDir = new Vector2(0, 1).rotateDeg(rudder.getAngle());
            Vector2 rudderNormal = rudderDir.cpy().rotate90(1); 
            
            float normalFlow = waterFlow.dot(rudderNormal);
            float rudderForceMag = normalFlow * 100.0f; 
            
            Vector2 rudderForceLocal = rudderNormal.cpy().scl(rudderForceMag);
            totalForce.add(rudderForceLocal.cpy().rotateDeg(angle));
            
            float rudderTorque = rudderPosLocal.x * rudderForceLocal.y - rudderPosLocal.y * rudderForceLocal.x;
            totalTorque += rudderTorque;
        }
        
        // D. Angular Drag
        float currentAngularDrag = angularDrag + (tileCount * 2.0f); 
        totalTorque -= angularVelocity * currentAngularDrag * 50f; 
        
        // E. Anchor Drag
        if (anchor != null && anchor.isDropped()) {
            float anchorLocalX = gridOriginX + anchor.getGridX() * tileSize + tileSize / 2;
            float anchorLocalY = gridOriginY + anchor.getGridY() * tileSize + tileSize / 2;
            Vector2 anchorPosLocal = new Vector2(anchorLocalX, anchorLocalY);
            
            float anchorOmegaRad = angularVelocity * MathUtils.degreesToRadians;
            Vector2 anchorTangentialVel = new Vector2(-anchorOmegaRad * anchorPosLocal.y, anchorOmegaRad * anchorPosLocal.x);
            Vector2 anchorVel = localVel.cpy().add(anchorTangentialVel);
            
            Vector2 anchorDrag = anchorVel.cpy().scl(-500.0f); 
            totalForce.add(anchorDrag.cpy().rotateDeg(angle));
            
            float anchorTorque = anchorPosLocal.x * anchorDrag.y - anchorPosLocal.y * anchorDrag.x;
            totalTorque += anchorTorque;
        }
        
        // 2. Integrate Physics (Euler)
        Vector2 acceleration = totalForce.scl(1.0f / mass);
        velocity.add(acceleration.scl(delta));
        position.add(velocity.x * delta, velocity.y * delta);
        
        float angularAccel = totalTorque / momentOfInertia;
        angularVelocity += angularAccel * delta;
        angle += angularVelocity * delta;
        
        // Collision Detection
        if (worldManager != null && checkCollision(worldManager)) {
            position.add(-velocity.x * delta, -velocity.y * delta);
            angle -= angularVelocity * delta;
            velocity.scl(-0.2f); 
            angularVelocity *= 0.5f;
        }
    }
    
    public void kickOff(com.landsea.game.world.WorldManager worldManager) {
        if (kickCooldown > 0) return;

        Vector2 pushVector = new Vector2(0, 0);
        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;
        
        boolean foundLand = false;

        for (BoatPart part : parts) {
            if (part instanceof HullPart) {
                float tileLocalX = gridOriginX + part.getGridX() * tileSize + tileSize / 2;
                float tileLocalY = gridOriginY + part.getGridY() * tileSize + tileSize / 2;
                
                Vector2 tilePos = new Vector2(tileLocalX, tileLocalY);
                tilePos.rotateDeg(angle);
                tilePos.add(position);
                
                float checkDist = tileSize * 2.0f;
                
                if (worldManager.isLand(tilePos.x + checkDist, tilePos.y)) { pushVector.add(-1, 0); foundLand = true; }
                if (worldManager.isLand(tilePos.x - checkDist, tilePos.y)) { pushVector.add(1, 0); foundLand = true; }
                if (worldManager.isLand(tilePos.x, tilePos.y + checkDist)) { pushVector.add(0, -1); foundLand = true; }
                if (worldManager.isLand(tilePos.x, tilePos.y - checkDist)) { pushVector.add(0, 1); foundLand = true; }
            }
        }
        
        if (!foundLand && velocity.len() < 10f) {
             Vector2 heading = new Vector2(0, 1).rotateDeg(angle);
             pushVector = heading.scl(-1);
             foundLand = true;
        }
        
        if (foundLand) {
            pushVector.nor();
            pushVector.rotateDeg(MathUtils.randomSign() * 45f);
            velocity.add(pushVector.scl(80f)); 
            angularVelocity += MathUtils.random(-30, 30); 
            kickCooldown = 1.0f;
        }
    }
    
    private boolean checkCollision(com.landsea.game.world.WorldManager worldManager) {
        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;
        
        for (BoatPart part : parts) {
            if (part instanceof HullPart) {
                float tileLocalX = gridOriginX + part.getGridX() * tileSize + tileSize / 2;
                float tileLocalY = gridOriginY + part.getGridY() * tileSize + tileSize / 2;
                
                Vector2 tilePos = new Vector2(tileLocalX, tileLocalY);
                tilePos.rotateDeg(angle);
                tilePos.add(position);
                
                if (worldManager.isLand(tilePos.x, tilePos.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void render(ShapeRenderer shapeRenderer, WindManager windManager) {
        com.badlogic.gdx.math.Matrix4 originalMatrix = shapeRenderer.getTransformMatrix().cpy();
        
        com.badlogic.gdx.math.Matrix4 newMatrix = originalMatrix.cpy();
        newMatrix.translate(position.x, position.y, 0);
        newMatrix.rotate(0, 0, 1, angle);
        
        shapeRenderer.setTransformMatrix(newMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); 
        
        // Render all parts
        // Note: Some parts might need specific render calls (like Sail with wind)
        // But BoatPart.render(ShapeRenderer) is generic.
        // SailPart has an overloaded render(ShapeRenderer, Vector2).
        
        // We should probably iterate and check types if we want to pass wind,
        // or update SailPart to store wind reference? No, passing is better.
        
        // Let's render HullParts first (bottom layer)
        for (BoatPart part : parts) {
            if (part instanceof HullPart) {
                part.render(shapeRenderer);
            }
        }
        
        // Render other parts
        for (BoatPart part : parts) {
            if (!(part instanceof HullPart)) {
                if (part instanceof SailPart) {
                    Vector2 localWind = windManager.getWindVector().cpy().rotateDeg(-angle);
                    ((SailPart) part).render(shapeRenderer, localWind);
                } else {
                    part.render(shapeRenderer);
                }
            }
        }
        
        shapeRenderer.end(); 
        shapeRenderer.setTransformMatrix(originalMatrix);
    }

    public Vector2 getPosition() {
        return position;
    }
    
    public Vector2 getRudderWorldPosition() {
        if (rudder == null) return position.cpy();
        
        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;
        
        float rudderLocalX = gridOriginX + rudder.getGridX() * tileSize + tileSize / 2;
        float rudderLocalY = gridOriginY + rudder.getGridY() * tileSize + tileSize / 2;
        
        Vector2 localPos = new Vector2(rudderLocalX, rudderLocalY);
        localPos.rotateDeg(angle);
        
        return position.cpy().add(localPos);
    }
    
    public float getAngle() {
        return angle;
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public SailPart getSail() {
        return sail;
    }
    
    public RudderPart getRudder() {
        return rudder;
    }
    
    public AnchorPart getAnchor() {
        return anchor;
    }
    
    public float getTileSize() {
        return tileSize;
    }
    
    public int getGridWidth() {
        return width;
    }
    
    public int getGridHeight() {
        return height;
    }
    
    public List<BoatPart> getParts() {
        return parts;
    }
    
    public boolean isTileWalkable(float localX, float localY) {
        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;
        
        int gridX = (int)((localX - gridOriginX) / tileSize);
        int gridY = (int)((localY - gridOriginY) / tileSize);
        
        if (gridX >= 0 && gridX < width && gridY >= 0 && gridY < height) {
            // Check if there is a HullPart at this location
            for (BoatPart part : parts) {
                if (part instanceof HullPart && (int)part.getGridX() == gridX && (int)part.getGridY() == gridY) {
                    return true;
                }
            }
        }
        return false;
    }
}
