package com.landsea.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.landsea.game.environment.WindManager;
import com.landsea.game.environment.TimeManager;
import com.landsea.game.environment.WeatherManager;
import com.landsea.game.entities.Player;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.landsea.game.inventory.Inventory;
import com.landsea.game.inventory.ItemStack;
import com.landsea.game.inventory.ItemType;
import com.landsea.game.crafting.CraftingManager;
import com.landsea.game.crafting.CraftingRecipe;
import java.util.Map;

public class Hud implements Disposable {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    public Hud() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
    }

    public void render(WindManager windManager, TimeManager timeManager, WeatherManager weatherManager, Player player, Camera camera, boolean showInventory, boolean showCrafting, CraftingManager craftingManager) {
        Vector2 wind = windManager.getWindVector();
        float speed = wind.len() / 10f; // Arbitrary scaling: 100 units -> 10 knots
        float angle = wind.angleDeg();
        
        String direction = getDirectionString(angle);
        
        batch.begin();
        font.draw(batch, String.format("Wind: %.1f knots %s", speed, direction), 20, Gdx.graphics.getHeight() - 20);
        
        // Draw Time and Weather
        int day = timeManager.getDay();
        int hour = timeManager.getHour();
        int minute = timeManager.getMinute();
        String weather = weatherManager.getCurrentState().toString();
        font.draw(batch, String.format("Day %d, %02d:%02d (%s)", day, hour, minute, weather), 20, Gdx.graphics.getHeight() - 50);
        
        String warning = player.getWarningMessage();
        if (warning != null) {
            Vector2 playerPos = player.getWorldPosition();
            Vector3 screenPos = camera.project(new Vector3(playerPos.x, playerPos.y, 0));
            
            font.setColor(Color.RED);
            font.draw(batch, warning, screenPos.x - 100, screenPos.y + 60);
            font.setColor(Color.WHITE);
        }
        
        batch.end();
        
        renderVitals(player);
        
        if (showInventory) {
            renderInventory(player.getInventory());
        }
        
        if (showCrafting) {
            renderCrafting(craftingManager, player.getInventory());
        }
    }
    
    private void renderVitals(Player player) {
        float barWidth = 200;
        float barHeight = 15;
        float startX = 20;
        float startY = 20;
        float padding = 5;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Stamina (Green)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(startX, startY, barWidth, barHeight);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(startX, startY, barWidth * (player.getStamina() / 100f), barHeight);
        
        // Thirst (Blue)
        startY += barHeight + padding;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(startX, startY, barWidth, barHeight);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(startX, startY, barWidth * (player.getThirst() / 100f), barHeight);

        // Hunger (Orange)
        startY += barHeight + padding;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(startX, startY, barWidth, barHeight);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(startX, startY, barWidth * (player.getHunger() / 100f), barHeight);

        // Health (Red)
        startY += barHeight + padding;
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(startX, startY, barWidth, barHeight);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(startX, startY, barWidth * (player.getHealth() / 100f), barHeight);
        
        shapeRenderer.end();
        
        // Labels
        batch.begin();
        font.getData().setScale(1.0f);
        font.draw(batch, "HP", startX + barWidth + 5, startY + 12);
        font.draw(batch, "Food", startX + barWidth + 5, startY - (barHeight + padding) + 12);
        font.draw(batch, "H2O", startX + barWidth + 5, startY - 2 * (barHeight + padding) + 12);
        font.draw(batch, "Stam", startX + barWidth + 5, startY - 3 * (barHeight + padding) + 12);
        font.getData().setScale(1.5f);
        batch.end();
    }

    private void renderInventory(Inventory inventory) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float width = 400;
        float height = 300;
        float x = (screenW - width) / 2;
        float y = (screenH - height) / 2;
        
        // Background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Items
        batch.begin();
        font.draw(batch, "INVENTORY", x + 20, y + height - 20);
        
        float itemY = y + height - 60;
        for (ItemStack stack : inventory.getSlots()) {
            String text = stack.getItem().getName() + ": " + stack.getCount();
            if (stack.getMaxDurability() > 0) {
                text += String.format(" (%.0f%%)", (stack.getDurability() / stack.getMaxDurability()) * 100f);
            }
            font.draw(batch, text, x + 20, itemY);
            itemY -= 30;
        }
        batch.end();
    }
    
    private void renderCrafting(CraftingManager craftingManager, Inventory inventory) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float width = 400;
        float height = 400;
        float x = (screenW - width) / 2;
        float y = (screenH - height) / 2;
        
        // Background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Recipes
        batch.begin();
        font.draw(batch, "CRAFTING (Press Number to Craft)", x + 20, y + height - 20);
        
        float itemY = y + height - 60;
        int index = 1;
        for (CraftingRecipe recipe : craftingManager.getRecipes()) {
            boolean canCraft = craftingManager.canCraft(inventory, recipe);
            font.setColor(canCraft ? Color.GREEN : Color.GRAY);
            
            StringBuilder sb = new StringBuilder();
            sb.append(index).append(". ").append(recipe.getResult().getName()).append(" x").append(recipe.getResultCount()).append(" [");
            
            for (Map.Entry<ItemType, Integer> entry : recipe.getIngredients().entrySet()) {
                sb.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append(" ");
            }
            sb.append("]");
            
            font.draw(batch, sb.toString(), x + 20, itemY);
            itemY -= 30;
            index++;
            font.setColor(Color.WHITE);
        }
        batch.end();
    }
    
    private String getDirectionString(float angle) {
        // Normalize angle to 0-360
        angle = (angle % 360 + 360) % 360;
        
        if (angle >= 337.5 || angle < 22.5) return "E";
        if (angle >= 22.5 && angle < 67.5) return "NE";
        if (angle >= 67.5 && angle < 112.5) return "N";
        if (angle >= 112.5 && angle < 157.5) return "NW";
        if (angle >= 157.5 && angle < 202.5) return "W";
        if (angle >= 202.5 && angle < 247.5) return "SW";
        if (angle >= 247.5 && angle < 292.5) return "S";
        if (angle >= 292.5 && angle < 337.5) return "SE";
        return "?";
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
