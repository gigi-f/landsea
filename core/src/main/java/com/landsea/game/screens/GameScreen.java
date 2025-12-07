package com.landsea.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.landsea.game.entities.Boat;
import com.landsea.game.entities.Player;
import com.landsea.game.environment.OceanRenderer;
import com.landsea.game.environment.WindManager;
import com.landsea.game.input.InputHandler;
import com.landsea.game.world.WorldManager;
import com.landsea.game.ui.Hud;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private InputHandler inputHandler;
    private Player player;
    private Boat boat;
    private WindManager windManager;
    private OceanRenderer oceanRenderer;
    private WorldManager worldManager;
    private Hud hud;
    
    // Visuals
    private Array<Vector2> wakeParticles;
    private float wakeTimer;

    public GameScreen() {
        // Create camera for 2D rendering
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();

        // Initialize shape renderer for basic drawing
        shapeRenderer = new ShapeRenderer();

        // Initialize input handling
        inputHandler = new InputHandler();
        
        // Initialize environment
        windManager = new WindManager();
        oceanRenderer = new OceanRenderer();
        worldManager = new WorldManager();
        hud = new Hud();
        
        // Initialize boat and player
        boat = new Boat(0, 0);
        player = new Player(boat);
        
        wakeParticles = new Array<>();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void render(float delta) {
        // Clear screen (Ocean color)
        Gdx.gl.glClearColor(0.0f, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        // Update camera to follow player
        camera.position.set(player.getWorldPosition(), 0);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Render
        draw();
    }
    private void update(float delta) {
        windManager.update(delta);
        oceanRenderer.update(delta, camera);
        worldManager.update(player.getWorldPosition());
        boat.update(delta, windManager, worldManager);
        player.update(delta, inputHandler.getHorizontal(), inputHandler.getVertical());
        player.update(delta, inputHandler.getHorizontal(), inputHandler.getVertical());
        
        if (inputHandler.isInteractPressed()) {
            player.interact();
        }
        
        // Update wake particles
        if (boat.getVelocity().len() > 10) {
            wakeTimer += delta;
            if (wakeTimer > 0.1f) {
                wakeTimer = 0;
                wakeParticles.add(boat.getRudderWorldPosition());
                if (wakeParticles.size > 50) wakeParticles.removeIndex(0);
            }
        }
    }

    private void draw() {
        // Draw Ocean (Whitecaps and Wind Lines)
        oceanRenderer.render(shapeRenderer, camera, windManager.getWindVector());
        
        // Draw World (Islands)
        worldManager.render(shapeRenderer, new Vector2(camera.position.x, camera.position.y), viewport.getWorldWidth(), viewport.getWorldHeight());
        
        // Draw wake
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
        for (Vector2 p : wakeParticles) {
            shapeRenderer.circle(p.x, p.y, 5);
        }
        shapeRenderer.end();
        
        // Draw boat (now passes wind manager)
        boat.render(shapeRenderer, windManager);
        // Draw player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.render(shapeRenderer);
        shapeRenderer.end();
        
        // Draw HUD
        hud.render(windManager);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        hud.dispose();
    }
}
