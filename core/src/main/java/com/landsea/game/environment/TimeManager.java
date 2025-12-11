package com.landsea.game.environment;

public class TimeManager {
    private float timeOfDay; // 0 to 24
    private float timeScale = 1.0f; // Game seconds per real second. 1.0 = realtime? No.
    // Let's say 1 day = 24 minutes? Then 1 game hour = 1 real minute.
    // 24 game hours = 1440 game minutes.
    // If we want 1 day = 12 minutes, then timeScale = 2.0 (if base is 1h=1m).
    
    // Let's make 1 day = 10 minutes (600 seconds).
    // 24 hours / 600 seconds = 0.04 hours per second.
    
    private int dayCount;
    
    public TimeManager() {
        this.timeOfDay = 8.0f; // Start at 8 AM
        this.dayCount = 1;
    }
    
    public void update(float delta) {
        // 24 hours in 600 seconds (10 mins)
        // speed = 24 / 600 = 0.04
        float speed = 0.04f; 
        
        timeOfDay += speed * delta;
        if (timeOfDay >= 24.0f) {
            timeOfDay -= 24.0f;
            dayCount++;
        }
    }
    
    public float getTime() {
        return timeOfDay;
    }
    
    public int getDay() {
        return dayCount;
    }

    public int getHour() {
        return (int) timeOfDay;
    }

    public int getMinute() {
        return (int) ((timeOfDay - (int)timeOfDay) * 60);
    }
    
    public String getTimeString() {
        int hour = (int) timeOfDay;
        int minute = (int) ((timeOfDay - hour) * 60);
        return String.format("Day %d - %02d:%02d", dayCount, hour, minute);
    }
    
    public float getAmbientLight() {
        // Simple curve: 
        // 6-18 is Day (1.0 light)
        // 20-4 is Night (0.1 light)
        // Transitions in between
        
        if (timeOfDay >= 6 && timeOfDay < 18) return 1.0f; // Day
        if (timeOfDay >= 20 || timeOfDay < 4) return 0.2f; // Night
        
        // Dusk (18-20)
        if (timeOfDay >= 18 && timeOfDay < 20) {
            float progress = (timeOfDay - 18) / 2.0f;
            return 1.0f - (progress * 0.8f); // 1.0 -> 0.2
        }
        
        // Dawn (4-6)
        if (timeOfDay >= 4 && timeOfDay < 6) {
            float progress = (timeOfDay - 4) / 2.0f;
            return 0.2f + (progress * 0.8f); // 0.2 -> 1.0
        }
        
        return 1.0f;
    }
    
    public boolean isNight() {
        return getAmbientLight() < 0.5f;
    }
}