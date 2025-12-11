package com.landsea.game.inventory;

public enum ItemType {
    WOOD("Wood", "Basic building material"),
    SCRAP("Scrap", "Metal debris from the old world"),
    PLASTIC("Plastic", "Common waste, useful for crafting"),
    STONE("Stone", "Heavy rock"),
    FIBER("Fiber", "Plant material for rope"),
    FOOD("Food", "Generic sustenance"),
    COCONUT("Coconut", "Hydrating fruit"),
    BERRY("Berry", "Small sweet fruit"),
    WATER("Water", "Fresh water"),
    ROPE("Rope", "Twisted fiber"),
    PLANK("Plank", "Processed wood"),
    CAMPFIRE("Campfire", "A warm fire"),
    AXE("Axe", "Chops wood efficiently"),
    PICKAXE("Pickaxe", "Breaks rocks efficiently");

    private final String name;
    private final String description;

    ItemType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
