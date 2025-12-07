package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.environment.WindManager;

public class Boat {
    private Vector2 position; // World position of the boat's center
    private Vector2 velocity;
    private float angle; // Boat heading in degrees (0 = East/Right, 90 = North/Up)
    private float angularVelocity; // Degrees per second
    
    // Physics Constants
    private float mass = 500f; // kg (Reduced from 1000)
    private float momentOfInertia = 1000f; // Resistance to turning (Reduced from 5000)
    private float dragForward = 0.2f; // Low drag moving forward (Reduced from 0.5)
    private float dragSideways = 5.0f; // High drag moving sideways (Keel effect)
    private float angularDrag = 2.0f; // Resistance to spinning
    
    private int width = 10;   // Grid width
    private int height = 10;  // Grid height
    private float tileSize = 32f; // Size of each grid tile in pixels
    private boolean[][] deck; // True if a tile exists at [x][y]
    private Sail sail;
    private Rudder rudder;

    public Boat(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.angle = 0;
        this.angularVelocity = 0;
        this.deck = new boolean[width][height];
        
        // Initialize a simple 2x4 raft in the center of the 10x10 grid
        // 2 wide (x), 4 high (y) - composed of two 2x2 sections
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Center X is 4-5. Center Y is 3-6
                if (x >= 4 && x <= 5 && y >= 3 && y <= 6) {
                    deck[x][y] = true;
                }
            }
        }
        
        // Place sail in the middle-ish (on the grid)
        this.sail = new Sail(4, 4);
        
        // Place rudder at the back (bottom center)
        this.rudder = new Rudder(4, 3);
    }

    public void update(float delta, WindManager windManager) {
        // 1. Calculate Forces
        Vector2 totalForce = new Vector2(0, 0);
        float totalTorque = 0;
        
        // A. Sail Force (Thrust)
        if (sail.isRaised()) {
            Vector2 wind = windManager.getWindVector();
            float windSpeed = wind.len();
            float windAngle = wind.angleDeg();
            
            // Calculate angle of attack (Wind vs Boat Heading)
            // Normalize angle difference to -180 to 180
            float angleDiff = (windAngle - angle + 180 + 360) % 360 - 180;
            
            // Square Rig Physics:
            // Best thrust when wind is behind (angleDiff ~ 0 if wind is pushing from behind? No, wind vector points direction it blows TO)
            // If Wind is (1,0) [East], and Boat is (1,0) [East], angleDiff is 0. Wind is pushing boat.
            // Max thrust at 0 degrees diff. Zero thrust at 180 degrees diff (Head to wind).
            
            // Actually, let's use dot product for simplicity and robustness
            Vector2 boatHeading = new Vector2(1, 0).setAngleDeg(angle);
            Vector2 windDir = wind.cpy().nor();
            
            float dot = boatHeading.dot(windDir); // 1.0 = Downwind, -1.0 = Upwind
            
            if (dot > -0.2f) { // Can sail downwind and reaching, but not too close to upwind
                // Thrust efficiency curve
                float efficiency = (dot + 0.2f) / 1.2f; // Normalize to 0..1 range
                
                // Apply force in direction of boat heading (Keel converts sideways wind force to forward motion)
                // Realistically, sail produces lift perpendicular to sail, but let's simplify:
                // Sail pushes boat FORWARD based on how much wind it catches.
                float thrust = windSpeed * efficiency * 150.0f; // 150.0 is sail power (Increased from 5.0)
                totalForce.add(boatHeading.scl(thrust));
            }
        }
        
        // B. Hydrodynamic Forces (Drag & Keel)
        // Convert velocity to local boat space
        Vector2 localVel = velocity.cpy().rotateDeg(-angle);
        
        // Forward Drag (Water resistance)
        float fDrag = -localVel.x * dragForward * localVel.x * Math.signum(localVel.x); // v^2 drag
        // Sideways Drag (Keel resistance - prevents drift)
        float sDrag = -localVel.y * dragSideways * Math.abs(localVel.y); // High resistance
        
        // Apply local drag forces
        Vector2 localDragForce = new Vector2(fDrag, sDrag);
        // Rotate back to world space
        totalForce.add(localDragForce.rotateDeg(angle));
        
        
        // C. Rudder Forces (Torque)
        // Rudder works based on water flow.
        // Flow speed over rudder is approx boat forward speed (localVel.x)
        // We ignore prop wash from propeller since we have none.
        float flowSpeed = localVel.x;
        
        if (Math.abs(flowSpeed) > 1.0f) {
            float rudderAngle = rudder.getAngle(); // +/- 45 degrees
            
            // Lift force generated by rudder is proportional to flowSpeed^2 * sin(angle)
            // This force is applied at the back of the boat, creating torque.
            // Torque = Force * Distance (Lever arm)
            // Lever arm is approx half boat length (~60 pixels)
            
            float rudderLift = -flowSpeed * 5.0f * MathUtils.sinDeg(rudderAngle); // Linear approx for stability
            
            // Torque
            totalTorque += rudderLift * 50.0f; // 50 is lever arm
        }
        
        // D. Angular Drag (Water resistance to turning)
        totalTorque -= angularVelocity * angularDrag * 100f; // Damping
        
        
        // 2. Integrate Physics (Euler)
        // Linear
        Vector2 acceleration = totalForce.scl(1.0f / mass);
        velocity.add(acceleration.scl(delta));
        position.add(velocity.x * delta, velocity.y * delta);
        
        // Angular
        float angularAccel = totalTorque / momentOfInertia;
        angularVelocity += angularAccel * delta;
        angle += angularVelocity * delta;
    }

    public void render(ShapeRenderer shapeRenderer, WindManager windManager) {
        // Save original matrix
        com.badlogic.gdx.math.Matrix4 originalMatrix = shapeRenderer.getTransformMatrix().cpy();
        
        // Apply transformation: Translate to boat pos, then Rotate
        com.badlogic.gdx.math.Matrix4 newMatrix = new com.badlogic.gdx.math.Matrix4();
        newMatrix.translate(position.x, position.y, 0);
        newMatrix.rotate(0, 0, 1, angle);
        shapeRenderer.setTransformMatrix(newMatrix);
        
        // Draw relative to (0,0)
        shapeRenderer.setColor(new Color(0.6f, 0.4f, 0.2f, 1f)); // Brown color for wood
        
        float startX = -(width * tileSize) / 2;
        float startY = -(height * tileSize) / 2;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (deck[x][y]) {
                    shapeRenderer.rect(startX + x * tileSize, startY + y * tileSize, tileSize, tileSize);
                }
            }
        }
        
        // Draw rudder (under sail)
        // Pass (0,0) as position because we are already transformed
        rudder.render(shapeRenderer, Vector2.Zero, tileSize, width, height);
        
        // Draw sail
        // We need to rotate the wind vector into local space for the sail to render correctly
        Vector2 localWind = windManager.getWindVector().cpy().rotateDeg(-angle);
        sail.render(shapeRenderer, Vector2.Zero, tileSize, width, height, localWind);
        
        // Restore matrix
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
    
    public Sail getSail() {
        return sail;
    }
    
    public Rudder getRudder() {
        return rudder;
    }
    
    public float getTileSize() {
        return tileSize;
    }
    
    public boolean isTileWalkable(float localX, float localY) {
        // Convert local coordinates (relative to boat center) to grid coordinates
        float gridOriginX = -(width * tileSize) / 2;
        float gridOriginY = -(height * tileSize) / 2;
        
        int gridX = (int)((localX - gridOriginX) / tileSize);
        int gridY = (int)((localY - gridOriginY) / tileSize);
        
        if (gridX >= 0 && gridX < width && gridY >= 0 && gridY < height) {
            return deck[gridX][gridY];
        }
        return false;
    }
}
