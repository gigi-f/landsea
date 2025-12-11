package com.landsea.game.environment;

import com.badlogic.gdx.math.MathUtils;

public class WeatherManager {
    public enum WeatherState {
        CLEAR,
        RAIN,
        STORM
    }
    
    private WeatherState currentState;
    private float timer;
    private float duration;
    
    public WeatherManager() {
        setWeather(WeatherState.CLEAR);
    }
    
    public void update(float delta, WindManager windManager) {
        timer += delta;
        if (timer >= duration) {
            pickNextWeather();
        }
        
        // Apply weather effects to wind
        switch (currentState) {
            case CLEAR:
                windManager.setIntensityMultiplier(1.0f);
                break;
            case RAIN:
                windManager.setIntensityMultiplier(1.5f);
                break;
            case STORM:
                windManager.setIntensityMultiplier(2.5f);
                break;
        }
    }
    
    private void pickNextWeather() {
        // Simple state machine
        float r = MathUtils.random();
        if (currentState == WeatherState.CLEAR) {
            if (r < 0.3f) setWeather(WeatherState.RAIN);
            else setWeather(WeatherState.CLEAR);
        } else if (currentState == WeatherState.RAIN) {
            if (r < 0.4f) setWeather(WeatherState.STORM);
            else if (r < 0.8f) setWeather(WeatherState.CLEAR);
            else setWeather(WeatherState.RAIN);
        } else { // STORM
            if (r < 0.5f) setWeather(WeatherState.RAIN);
            else setWeather(WeatherState.STORM);
        }
    }
    
    private void setWeather(WeatherState state) {
        currentState = state;
        timer = 0;
        // Random duration: 30s to 2 mins
        duration = MathUtils.random(30f, 120f);
        System.out.println("Weather changed to: " + state);
    }
    
    public WeatherState getCurrentState() {
        return currentState;
    }
}