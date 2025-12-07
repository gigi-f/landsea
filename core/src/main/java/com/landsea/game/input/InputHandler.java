package com.landsea.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    
    private boolean up, down, left, right;

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                up = true;
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                down = true;
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                left = true;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                right = true;
                break;
            case Input.Keys.E:
                interact = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                up = false;
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                down = false;
                break;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                left = false;
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                right = false;
                break;
            case Input.Keys.E:
                interact = false;
                break;
        }
        return true;
    }

    public float getHorizontal() {
        return (right ? 1 : 0) - (left ? 1 : 0);
    }

    public float getVertical() {
        return (up ? 1 : 0) - (down ? 1 : 0);
    }
    
    public boolean isInteractPressed() {
        return interact;
    }
    
    private boolean interact;


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Handle mouse/touch press
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Handle mouse/touch release
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Handle mouse movement
        return true;
    }
}
