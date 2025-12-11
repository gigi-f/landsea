package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.entities.boatparts.AnchorPart;
import com.landsea.game.entities.boatparts.RudderPart;
import com.landsea.game.entities.boatparts.SailPart;
import com.landsea.game.entities.boatparts.BoatPart;
import com.landsea.game.entities.boatparts.HullPart;
import com.landsea.game.world.WorldManager;
import com.landsea.game.inventory.Inventory;
import com.landsea.game.inventory.ItemStack;
import com.landsea.game.inventory.ItemType;

import com.landsea.game.entities.ResourceNode;

public class Player {
    private Vector2 localPosition; // Position relative to the boat center
    private Boat boat;
    private float speed = 150f; // Pixels per second
    private float size = 10f;
    
    private boolean isSteering = false;
    
    private String warningMessage;
    private float warningTimer;
    private float interactCooldown = 0f;
    private float attackCooldown = 0f;
    private float swingTimer = 0f;
    private float swingDuration = 0.2f;
    private Vector2 lastMoveDir = new Vector2(1, 0); // Default facing right
    
    // Vitals
    private float health = 100f;
    private float maxHealth = 100f;
    private float hunger = 100f; // 0 = Starving
    private float maxHunger = 100f;
    private float thirst = 100f; // 0 = Dehydrated
    private float maxThirst = 100f;
    private float stamina = 100f;
    private float maxStamina = 100f;
    
    private Inventory inventory;

    public Player(Boat boat) {
        this.boat = boat;
        this.localPosition = new Vector2(0, 0); // Start at center of boat
        this.inventory = new Inventory(10); // 10 slots
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void update(float delta, float moveX, float moveY, WorldManager worldManager) {
        // Update Vitals
        hunger -= 0.5f * delta; // Drains in ~200s
        thirst -= 0.8f * delta; // Drains in ~125s
        
        if (stamina < maxStamina) {
            stamina += 5f * delta;
        }

        hunger = Math.max(0, Math.min(hunger, maxHunger));
        thirst = Math.max(0, Math.min(thirst, maxThirst));
        stamina = Math.max(0, Math.min(stamina, maxStamina));
        
        if (hunger <= 0 || thirst <= 0) {
            health -= 1f * delta;
        }
        health = Math.max(0, Math.min(health, maxHealth));

        if (warningTimer > 0) {
            warningTimer -= delta;
            if (warningTimer <= 0) warningMessage = null;
        }
        
        if (interactCooldown > 0) {
            interactCooldown -= delta;
        }
        
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }
        
        if (swingTimer > 0) {
            swingTimer -= delta;
        }

        if (isSteering) {
            // If steering, input controls the rudder, not player movement
            RudderPart rudder = boat.getRudder();
            if (rudder != null) {
                rudder.updateInput(delta, moveX); // Use horizontal input for rudder
            }
            return; // Don't move player
        }
        
        // Normalize diagonal movement
        if (moveX != 0 || moveY != 0) {
            lastMoveDir.set(moveX, moveY).nor();
        }
        
        if (moveX != 0 && moveY != 0) {
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            moveX /= length;
            moveY /= length;
        }
        
        // Convert world-space input to local-space movement
        // We want 'Up' (W) to always mean 'Up on screen', regardless of boat rotation.
        // So we rotate the input vector by -boat.getAngle() to align it with the boat's local axes.
        Vector2 inputVector = new Vector2(moveX, moveY);
        inputVector.rotateDeg(-boat.getAngle());
        
        float nextX = localPosition.x + inputVector.x * speed * delta;
        float nextY = localPosition.y + inputVector.y * speed * delta;

        // Check X movement
        if (canMoveTo(nextX, localPosition.y, worldManager)) {
            localPosition.x = nextX;
        }
        
        // Check Y movement
        if (canMoveTo(localPosition.x, nextY, worldManager)) {
            localPosition.y = nextY;
        }
    }

    private boolean canMoveTo(float localX, float localY, WorldManager worldManager) {
        // 1. Check if on boat deck
        if (boat.isTileWalkable(localX, localY)) {
            return true;
        }
        
        // 2. If not on deck, check if on land (Disembarking)
        Vector2 worldPos = boat.getPosition().cpy().add(new Vector2(localX, localY).rotateDeg(boat.getAngle()));
        
        if (worldManager.isLand(worldPos.x, worldPos.y)) {
            // Check disembark conditions
            boolean sailDown = boat.getSail() == null || !boat.getSail().isRaised();
            boolean anchorDown = boat.getAnchor() != null && boat.getAnchor().isDropped();
            
            if (sailDown && anchorDown) {
                return true;
            } else {
                // Trigger warning
                warningMessage = "Lower Sail & Drop Anchor";
                warningTimer = 2.0f;
                return false;
            }
        }
        
        return false; // Can't walk on water
    }
    
    public String getWarningMessage() {
        return warningMessage;
    }
    
    public void attack(WorldManager worldManager) {
        if (attackCooldown > 0 || isSteering) return;
        
        attackCooldown = 0.5f;
        swingTimer = swingDuration;
        
        // Check for resources in front of player
        Vector2 worldPos = getWorldPosition();
        Vector2 attackPos = worldPos.cpy().add(lastMoveDir.cpy().scl(20f)); // 20px in front
        
        ResourceNode node = worldManager.getClosestResource(attackPos, 30f); // 30px range
        if (node != null) {
            float damage = 10f; // Base damage (Knife/Fist)
            ItemStack tool = null;
            
            // Check for tools
            // Prioritize correct tool
            if (node.getType() == ResourceNode.Type.TREE || 
                node.getType() == ResourceNode.Type.PALM_TREE || 
                node.getType() == ResourceNode.Type.MANGROVE || 
                node.getType() == ResourceNode.Type.MAHOGANY) {
                
                tool = inventory.findItem(ItemType.AXE);
                if (tool != null) damage = 30f;
                
            } else if (node.getType() == ResourceNode.Type.ROCK) {
                tool = inventory.findItem(ItemType.PICKAXE);
                if (tool != null) damage = 30f;
            }
            
            // If we have a tool but it wasn't the preferred one, we might still use it?
            // For now, only use tool if it matches.
            
            ItemStack stack = node.takeDamage(damage);
            
            // Damage tool
            if (tool != null) {
                tool.damage(1f);
                if (tool.isBroken()) {
                    inventory.removeStack(tool);
                    System.out.println("Tool broken!");
                }
            }
            
            if (stack != null) {
                inventory.addItem(stack.getItem(), stack.getCount());
                System.out.println("Harvested " + stack.getItem().getName() + " x" + stack.getCount());
            }
        }
    }

    public void place(WorldManager worldManager) {
        if (interactCooldown > 0) return;
        
        // Check if we have a Plank
        if (inventory.hasItem(ItemType.PLANK)) {
            // Try to build Hull
            float tileSize = boat.getTileSize();
            float gridOriginX = -(boat.getGridWidth() * tileSize) / 2; 
            float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
            
            // Calculate grid position of player
            int gridX = (int)((localPosition.x - gridOriginX) / tileSize);
            int gridY = (int)((localPosition.y - gridOriginY) / tileSize);
            
            // We want to place in front of the player? Or at current pos?
            // If at current pos, we must be standing on water (impossible if we can't walk on water).
            // So we must place adjacent.
            // Let's place in the direction we are facing (lastMoveDir).
            
            // Rotate lastMoveDir by -boatAngle to get local direction
            Vector2 localDir = lastMoveDir.cpy().rotateDeg(-boat.getAngle());
            
            // Round to nearest cardinal direction
            int dx = 0, dy = 0;
            if (Math.abs(localDir.x) > Math.abs(localDir.y)) {
                dx = (localDir.x > 0) ? 1 : -1;
            } else {
                dy = (localDir.y > 0) ? 1 : -1;
            }
            
            int targetX = gridX + dx;
            int targetY = gridY + dy;
            
            // Check bounds
            if (targetX >= 0 && targetX < boat.getGridWidth() && targetY >= 0 && targetY < boat.getGridHeight()) {
                // Check if empty
                if (!boat.isTileOccupied(targetX, targetY)) {
                    // Check adjacency (must be connected to existing hull)
                    boolean connected = boat.isTileOccupied(targetX + 1, targetY) ||
                                      boat.isTileOccupied(targetX - 1, targetY) ||
                                      boat.isTileOccupied(targetX, targetY + 1) ||
                                      boat.isTileOccupied(targetX, targetY - 1);
                                      
                    if (connected) {
                        // Build!
                        inventory.removeItem(ItemType.PLANK, 1);
                        boat.addPart(new HullPart(boat, targetX, targetY));
                        interactCooldown = 0.5f;
                        System.out.println("Built Hull Part at " + targetX + "," + targetY);
                        return;
                    }
                }
            }
        }
        
        // Campfire placement
        if (inventory.hasItem(ItemType.CAMPFIRE)) {
            Vector2 worldPos = getWorldPosition();
            // Place in front
            Vector2 placePos = worldPos.cpy().add(lastMoveDir.cpy().scl(30f));
            
            if (worldManager.isLand(placePos.x, placePos.y)) {
                // Check if too close to other resources?
                // For now just place it
                inventory.removeItem(ItemType.CAMPFIRE, 1);
                worldManager.addResourceNode(new ResourceNode(placePos.x, placePos.y, ResourceNode.Type.CAMPFIRE));
                interactCooldown = 0.5f;
                System.out.println("Placed Campfire");
            } else {
                warningMessage = "Must place on Land";
                warningTimer = 2.0f;
            }
        }
    }

    public void heal(float amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }
    
    public void restoreStamina(float amount) {
        stamina += amount;
        if (stamina > maxStamina) stamina = maxStamina;
    }

    public void interact(WorldManager worldManager) {
        if (interactCooldown > 0) return;
        interactCooldown = 0.5f; // 0.5 second cooldown
        
        // If already steering, stop steering
        if (isSteering) {
            isSteering = false;
            return;
        }
        
        float tileSize = boat.getTileSize();
        // Grid origin relative to boat center
        float gridOriginX = -(boat.getGridWidth() * tileSize) / 2; 
        float gridOriginY = -(boat.getGridHeight() * tileSize) / 2;
        
        int gridX = (int)((localPosition.x - gridOriginX) / tileSize);
        int gridY = (int)((localPosition.y - gridOriginY) / tileSize);

        BoatPart closestPart = null;
        float minDistance = Float.MAX_VALUE;

        for (BoatPart part : boat.getParts()) {
            // Skip HullParts for interaction (unless we want to inspect them)
            if (part instanceof HullPart) continue;

            float dx = gridX - part.getGridX();
            float dy = gridY - part.getGridY();
            
            // Check if within interaction range (1.5 tile radius to be generous)
            if (Math.abs(dx) <= 1.5f && Math.abs(dy) <= 1.5f) {
                float dist = dx*dx + dy*dy;
                if (dist < minDistance) {
                    minDistance = dist;
                    closestPart = part;
                }
            }
        }
        
        if (closestPart != null) {
            if (closestPart instanceof RudderPart) {
                isSteering = true;
            } else {
                closestPart.interact();
            }
        } else {
            // Try to interact with world resources (Non-destructive)
            Vector2 worldPos = getWorldPosition();
            ResourceNode node = worldManager.getClosestResource(worldPos, 50f); // 50px range
            if (node != null) {
                node.interact(this);
            }
        }
    }

    public void tryConsume() {
        if (interactCooldown > 0) return;
        
        // Prioritize what is needed most
        boolean hungry = hunger < maxHunger * 0.9f;
        boolean thirsty = thirst < maxThirst * 0.9f;
        
        if (thirsty) {
            if (consumeItem(ItemType.COCONUT)) return; // Coconuts give both
            if (consumeItem(ItemType.BERRY)) return; // Berries give a little water
        }
        
        if (hungry) {
            if (consumeItem(ItemType.COCONUT)) return;
            if (consumeItem(ItemType.BERRY)) return;
        }
    }
    
    private boolean consumeItem(ItemType type) {
        if (inventory.hasItem(type)) {
            inventory.removeItem(type, 1);
            interactCooldown = 0.5f;
            
            // Apply effects
            switch (type) {
                case COCONUT:
                    hunger += 20;
                    thirst += 30;
                    System.out.println("Consumed Coconut");
                    break;
                case BERRY:
                    hunger += 10;
                    thirst += 5;
                    System.out.println("Consumed Berry");
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    public void render(ShapeRenderer shapeRenderer) {
        Vector2 worldPos = getWorldPosition();
        
        if (isSteering) {
            shapeRenderer.setColor(Color.BLUE); // Visual feedback for steering mode
        } else {
            shapeRenderer.setColor(Color.YELLOW);
        }
        shapeRenderer.circle(worldPos.x, worldPos.y, size);
        
        // Render swing
        if (swingTimer > 0) {
            shapeRenderer.setColor(Color.WHITE);
            Vector2 swingStart = worldPos.cpy();
            Vector2 swingEnd = worldPos.cpy().add(lastMoveDir.cpy().scl(25f));
            
            // Simple arc or line
            shapeRenderer.rectLine(swingStart, swingEnd, 3f);
        }
    }

    public float getHealth() { return health; }
    public float getHunger() { return hunger; }
    public float getThirst() { return thirst; }
    public float getStamina() { return stamina; }

    public void consume(float foodValue, float waterValue) {
        hunger += foodValue;
        thirst += waterValue;
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
