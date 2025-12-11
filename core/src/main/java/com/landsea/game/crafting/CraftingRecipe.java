package com.landsea.game.crafting;

import com.landsea.game.inventory.ItemType;
import java.util.Map;

public class CraftingRecipe {
    private ItemType result;
    private int resultCount;
    private Map<ItemType, Integer> ingredients;

    public CraftingRecipe(ItemType result, int resultCount, Map<ItemType, Integer> ingredients) {
        this.result = result;
        this.resultCount = resultCount;
        this.ingredients = ingredients;
    }

    public ItemType getResult() { return result; }
    public int getResultCount() { return resultCount; }
    public Map<ItemType, Integer> getIngredients() { return ingredients; }
}