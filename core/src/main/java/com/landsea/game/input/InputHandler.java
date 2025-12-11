package com.landsea.game.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    
    private boolean up, down, left, right;
    private boolean interact, kick, inventory, consume, crafting, attack, place;

    private int lastNumberPressed = -1;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9) {
            lastNumberPressed = keycode - Input.Keys.NUM_1 + 1;
        }
        
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
            case Input.Keys.SPACE:
                attack = true;
                break;
            case Input.Keys.C:
                consume = true;
                break;
            case Input.Keys.B:
                crafting = true;
                break;
            case Input.Keys.P:
                place = true;
                break;
            case Input.Keys.K:
                kick = true;
                break;
            case Input.Keys.I:
            case Input.Keys.TAB:
                inventory = true;
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
            case Input.Keys.SPACE:
                attack = false;
                break;
            case Input.Keys.C:
                consume = false;
                break;
            case Input.Keys.B:
                crafting = false;
                break;
            case Input.Keys.P:
                place = false;
                break;
            case Input.Keys.K:
                kick = false;
                break;
            case Input.Keys.I:
            case Input.Keys.TAB:
                inventory = false;
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
    
    public boolean isAttackPressed() {
        return attack;
    }
    
    public boolean isPlacePressed() {
        return place;
    }

    public boolean isConsumePressed() {
        return consume;
    }

    public boolean isCraftingPressed() {
        return crafting;
    }

    public boolean isKickPressed() {
        return kick;
    }
    
    public int getAndClearNumberPressed() {
        int num = lastNumberPressed;
        lastNumberPressed = -1;
        return num;
    }
    
    public boolean isInventoryPressed() {
        return inventory;
    }
    
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
