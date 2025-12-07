package com.landsea.game;

import com.badlogic.gdx.Game;
import com.landsea.game.screens.GameScreen;

public class LandseaGame extends Game {
    private GameScreen gameScreen;

    @Override
    public void create() {
        gameScreen = new GameScreen();
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }
}
