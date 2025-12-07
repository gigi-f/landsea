package com.landsea.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.landsea.game.LandseaGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setTitle("Landsea");
        config.setForegroundFPS(60);
        config.useVsync(true);
        
        new Lwjgl3Application(new LandseaGame(), config);
    }
}
