package com.landsea.game.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import com.landsea.game.entities.ResourceNode;
import com.landsea.game.world.WorldManager;
import java.util.List;

public class LightingSystem implements Disposable {
    private FrameBuffer fbo;
    private SpriteBatch batch;
    private TextureRegion fboRegion;
    private Texture lightTexture; // A soft circle texture
    
    public LightingSystem() {
        batch = new SpriteBatch();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        createLightTexture();
    }
    
    private void createLightTexture() {
        int size = 128;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        // Draw a radial gradient circle
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = x - size/2f;
                float dy = y - size/2f;
                float dist = (float)Math.sqrt(dx*dx + dy*dy) / (size/2f);
                
                if (dist <= 1.0f) {
                    // Alpha fades out
                    float alpha = 1.0f - dist;
                    // Use smoothstep or similar for better look
                    alpha = alpha * alpha; 
                    pixmap.setColor(1f, 1f, 1f, alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        lightTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    public void resize(int width, int height) {
        if (fbo != null) fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true); // FBOs are flipped
    }
    
    public void render(TimeManager timeManager, Camera camera, WorldManager worldManager) {
        float ambient = timeManager.getAmbientLight();
        if (ambient >= 0.99f) return; // Don't render if it's bright day
        
        fbo.begin();
        
        // Clear to ambient light level
        // Night: 0.2, 0.2, 0.3
        // Day: 1.0, 1.0, 1.0
        Gdx.gl.glClearColor(ambient, ambient, ambient + 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw lights (Additive)
        // We want to ADD light to the ambient level.
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        
        List<ResourceNode> nodes = worldManager.getVisibleResourceNodes(
            new Vector2(camera.position.x, camera.position.y), 
            camera.viewportWidth, 
            camera.viewportHeight
        );
        
        renderLights(batch, nodes);
        
        batch.end();
        fbo.end();
        
        // Draw FBO over screen (Multiply)
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO); // Multiply
        batch.draw(fboRegion, 0, 0);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // Reset
        batch.end();
    }
    
    public void renderLights(SpriteBatch batch, List<ResourceNode> nodes) {
        for (ResourceNode node : nodes) {
            if (node.getType() == ResourceNode.Type.CAMPFIRE && !node.isDepleted()) {
                // Draw light
                float size = 200f;
                // Flicker
                size += (float)Math.random() * 10f;
                
                batch.setColor(1f, 0.8f, 0.6f, 0.8f); // Warm light
                batch.draw(lightTexture, node.getPosition().x - size/2, node.getPosition().y - size/2, size, size);
            }
        }
    }

    @Override
    public void dispose() {
        if (fbo != null) fbo.dispose();
        if (batch != null) batch.dispose();
        if (lightTexture != null) lightTexture.dispose();
    }
}