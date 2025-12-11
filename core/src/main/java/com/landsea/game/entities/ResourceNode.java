package com.landsea.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.landsea.game.inventory.ItemType;
import com.landsea.game.inventory.ItemStack;

public class ResourceNode {
    public enum Type {
        TREE, // Generic
        PALM_TREE,
        MANGROVE,
        MAHOGANY,
        ROCK,
        BUSH,
        BERRY_BUSH,
        CAMPFIRE
    }

    private Vector2 position; // World position
    private Type type;
    private float health;
    private float maxHealth;
    private boolean depleted;

    public ResourceNode(float x, float y, Type type) {
        this.position = new Vector2(x, y);
        this.type = type;
        this.depleted = false;
        
        switch (type) {
            case TREE: 
            case PALM_TREE:
            case MANGROVE:
                maxHealth = 60f; break; // 3 hits with 20 dmg
            case MAHOGANY:
                maxHealth = 100f; break; // 5 hits
            case ROCK: 
                maxHealth = 80f; break; // 4 hits
            case BUSH: 
            case BERRY_BUSH:
                maxHealth = 20f; break; // 1 hit
            case CAMPFIRE:
                maxHealth = 100f; break; // Durable
            default:
                maxHealth = 20f;
        }
        this.health = maxHealth;
    }

    public ItemStack takeDamage(float damage) {
        if (depleted) return null;
        
        if (type == Type.CAMPFIRE) {
            // Campfires can be destroyed to get resources back?
            // Or maybe they just break.
            health -= damage;
            if (health <= 0) {
                depleted = true;
                return new ItemStack(ItemType.STONE, 2); // Return some stone
            }
            return null;
        }
        
        health -= damage;
        // Visual shake or particle effect could be triggered here
        
        if (health <= 0) {
            depleted = true;
            return getDrops();
        }
        return null;
    }
    
    private ItemStack getDrops() {
        switch (type) {
            case TREE: return new ItemStack(ItemType.WOOD, 3);
            case PALM_TREE: return new ItemStack(Math.random() > 0.5 ? ItemType.WOOD : ItemType.COCONUT, 3);
            case MANGROVE: return new ItemStack(ItemType.WOOD, 3);
            case MAHOGANY: return new ItemStack(ItemType.WOOD, 5);
            case ROCK: return new ItemStack(ItemType.STONE, 3);
            case BUSH: return new ItemStack(ItemType.FIBER, 2);
            case BERRY_BUSH: return new ItemStack(Math.random() > 0.5 ? ItemType.FIBER : ItemType.BERRY, 2);
            default: return null;
        }
    }
    
    // Deprecated
    public ItemStack harvest() {
        return takeDamage(1000f); // Instant kill
    }
    
    public void interact(Player player) {
        if (depleted) return;
        
        if (type == Type.CAMPFIRE) {
            // Rest / Heal
            player.heal(10f);
            player.restoreStamina(50f);
            System.out.println("Rested by the fire.");
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (depleted) return;
        
        // Flash white if damaged recently? (Not implemented yet)
        
        switch (type) {
            case TREE:
                shapeRenderer.setColor(new Color(0.4f, 0.2f, 0.0f, 1f)); // Brown trunk
                shapeRenderer.rect(position.x - 5, position.y, 10, 20);
                shapeRenderer.setColor(new Color(0.1f, 0.6f, 0.1f, 1f)); // Green leaves
                shapeRenderer.circle(position.x, position.y + 20, 15);
                break;
            case PALM_TREE:
                shapeRenderer.setColor(new Color(0.6f, 0.5f, 0.3f, 1f)); // Tan trunk
                shapeRenderer.rect(position.x - 3, position.y, 6, 25);
                shapeRenderer.setColor(new Color(0.2f, 0.8f, 0.2f, 1f)); // Bright Green leaves
                shapeRenderer.circle(position.x, position.y + 25, 12);
                break;
            case MANGROVE:
                shapeRenderer.setColor(new Color(0.3f, 0.2f, 0.1f, 1f)); // Dark trunk
                shapeRenderer.rect(position.x - 6, position.y, 12, 15);
                shapeRenderer.setColor(new Color(0.2f, 0.5f, 0.2f, 1f)); // Dark Green leaves
                shapeRenderer.circle(position.x, position.y + 15, 18);
                break;
            case MAHOGANY:
                shapeRenderer.setColor(new Color(0.3f, 0.1f, 0.0f, 1f)); // Reddish trunk
                shapeRenderer.rect(position.x - 8, position.y, 16, 30);
                shapeRenderer.setColor(new Color(0.0f, 0.4f, 0.0f, 1f)); // Deep Green leaves
                shapeRenderer.circle(position.x, position.y + 30, 20);
                break;
            case ROCK:
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.circle(position.x, position.y, 12);
                break;
            case BUSH:
                shapeRenderer.setColor(new Color(0.2f, 0.8f, 0.2f, 1f));
                shapeRenderer.circle(position.x, position.y, 8);
                break;
            case BERRY_BUSH:
                shapeRenderer.setColor(new Color(0.2f, 0.8f, 0.2f, 1f));
                shapeRenderer.circle(position.x, position.y, 8);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.circle(position.x + 2, position.y + 2, 3);
                shapeRenderer.circle(position.x - 2, position.y - 1, 3);
                break;
            case CAMPFIRE:
                shapeRenderer.setColor(Color.GRAY); // Stones
                shapeRenderer.circle(position.x, position.y, 10);
                shapeRenderer.setColor(Color.ORANGE); // Fire
                shapeRenderer.circle(position.x, position.y, 6);
                shapeRenderer.setColor(Color.YELLOW); // Core
                shapeRenderer.circle(position.x, position.y, 3);
                break;
        }
    }

    public Vector2 getPosition() {
        return position;
    }
    
    public Type getType() {
        return type;
    }

    public boolean isDepleted() {
        return depleted;
    }
}
