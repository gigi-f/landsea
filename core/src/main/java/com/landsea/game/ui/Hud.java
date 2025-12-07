package com.landsea.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.landsea.game.environment.WindManager;

public class Hud implements Disposable {
    private SpriteBatch batch;
    private BitmapFont font;

    public Hud() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
    }

    public void render(WindManager windManager) {
        Vector2 wind = windManager.getWindVector();
        float speed = wind.len() / 10f; // Arbitrary scaling: 100 units -> 10 knots
        float angle = wind.angleDeg();
        
        String direction = getDirectionString(angle);
        
        batch.begin();
        font.draw(batch, String.format("Wind: %.1f knots %s", speed, direction), 20, Gdx.graphics.getHeight() - 20);
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
        font.dispose();
    }
}
