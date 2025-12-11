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
import com.landsea.game.environment.TimeManager;
import com.landsea.game.environment.WeatherManager;
import com.landsea.game.environment.LightingSystem;
import com.landsea.game.input.InputHandler;
import com.landsea.game.world.WorldManager;
import com.landsea.game.ui.Hud;
import com.landsea.game.crafting.CraftingManager;
import com.landsea.game.crafting.CraftingRecipe;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private InputHandler inputHandler;
    private Player player;
    private Boat boat;
    private WindManager windManager;
    private TimeManager timeManager;
    private WeatherManager weatherManager;
    private LightingSystem lightingSystem;
    private OceanRenderer oceanRenderer;
    private WorldManager worldManager;
    private CraftingManager craftingManager;
    private Hud hud;
    private boolean showInventory = false;
    private boolean inventoryKeyPressed = false;
    private boolean showCrafting = false;
    private boolean craftingKeyPressed = false;
    
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
        timeManager = new TimeManager();
        weatherManager = new WeatherManager();
        lightingSystem = new LightingSystem();
        oceanRenderer = new OceanRenderer();
        worldManager = new WorldManager();
        craftingManager = new CraftingManager();
        hud = new Hud();
        
        // Find safe spawn point (water)
        Vector2 spawnPos = worldManager.findSafeSpawn();
        
        // Initialize boat and player
        boat = new Boat(spawnPos.x, spawnPos.y);
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
        timeManager.update(delta);
        weatherManager.update(delta, windManager);
        oceanRenderer.update(delta, camera);
        worldManager.update(player.getWorldPosition());
        boat.update(delta, windManager, worldManager);
        player.update(delta, inputHandler.getHorizontal(), inputHandler.getVertical(), worldManager);
        
        if (inputHandler.isInteractPressed()) {
            player.interact(worldManager);
        }
        
        if (inputHandler.isAttackPressed()) {
            player.attack(worldManager);
        }
        
        if (inputHandler.isPlacePressed()) {
            player.place(worldManager);
        }
        
        if (inputHandler.isConsumePressed()) {
            player.tryConsume();
        }
        
        if (inputHandler.isKickPressed()) {
            boat.kickOff(worldManager);
        }
        
        if (inputHandler.isInventoryPressed()) {
            if (!inventoryKeyPressed) {
                showInventory = !showInventory;
                showCrafting = false; // Close crafting if inventory opens
                inventoryKeyPressed = true;
            }
        } else {
            inventoryKeyPressed = false;
        }
        
        if (inputHandler.isCraftingPressed()) {
            if (!craftingKeyPressed) {
                showCrafting = !showCrafting;
                showInventory = false; // Close inventory if crafting opens
                craftingKeyPressed = true;
            }
        } else {
            craftingKeyPressed = false;
        }
        
        if (showCrafting) {
            int num = inputHandler.getAndClearNumberPressed();
            if (num > 0 && num <= craftingManager.getRecipes().size()) {
                CraftingRecipe recipe = craftingManager.getRecipes().get(num - 1);
                if (craftingManager.craft(player.getInventory(), recipe)) {
                    System.out.println("Crafted " + recipe.getResult().getName());
                } else {
                    System.out.println("Cannot craft " + recipe.getResult().getName());
                }
            }
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
        
        // Draw Lighting
        lightingSystem.render(timeManager, camera, worldManager);
        
        // Draw Weather (Rain)
        oceanRenderer.renderRain(shapeRenderer, camera, weatherManager);
        
        // Draw HUD
        hud.render(windManager, timeManager, weatherManager, player, camera, showInventory, showCrafting, craftingManager);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        lightingSystem.resize(width, height);
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
        lightingSystem.dispose();
    }
}
