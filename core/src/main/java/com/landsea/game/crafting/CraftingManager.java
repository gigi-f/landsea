package com.landsea.game.crafting;

import com.landsea.game.inventory.Inventory;
import com.landsea.game.inventory.ItemType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingManager {
    private List<CraftingRecipe> recipes;

    public CraftingManager() {
        recipes = new ArrayList<>();
        initRecipes();
    }

    private void initRecipes() {
        // Rope: 2 Fiber
        Map<ItemType, Integer> rope = new HashMap<>();
        rope.put(ItemType.FIBER, 2);
        recipes.add(new CraftingRecipe(ItemType.ROPE, 1, rope));

        // Plank: 2 Wood
        Map<ItemType, Integer> plank = new HashMap<>();
        plank.put(ItemType.WOOD, 2);
        recipes.add(new CraftingRecipe(ItemType.PLANK, 1, plank));
        
        // Campfire: 4 Stone + 4 Wood
        Map<ItemType, Integer> campfire = new HashMap<>();
        campfire.put(ItemType.STONE, 4);
        campfire.put(ItemType.WOOD, 4);
        recipes.add(new CraftingRecipe(ItemType.CAMPFIRE, 1, campfire));
        
        // Axe: 2 Stone + 2 Wood + 1 Rope
        Map<ItemType, Integer> axe = new HashMap<>();
        axe.put(ItemType.STONE, 2);
        axe.put(ItemType.WOOD, 2);
        axe.put(ItemType.ROPE, 1);
        recipes.add(new CraftingRecipe(ItemType.AXE, 1, axe));
        
        // Pickaxe: 3 Stone + 2 Wood + 1 Rope
        Map<ItemType, Integer> pickaxe = new HashMap<>();
        pickaxe.put(ItemType.STONE, 3);
        pickaxe.put(ItemType.WOOD, 2);
        pickaxe.put(ItemType.ROPE, 1);
        recipes.add(new CraftingRecipe(ItemType.PICKAXE, 1, pickaxe));
    }

    public List<CraftingRecipe> getRecipes() {
        return recipes;
    }

    public boolean canCraft(Inventory inventory, CraftingRecipe recipe) {
        for (Map.Entry<ItemType, Integer> entry : recipe.getIngredients().entrySet()) {
            if (inventory.getItemCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean craft(Inventory inventory, CraftingRecipe recipe) {
        if (!canCraft(inventory, recipe)) return false;

        // Remove ingredients
        for (Map.Entry<ItemType, Integer> entry : recipe.getIngredients().entrySet()) {
            inventory.removeItem(entry.getKey(), entry.getValue());
        }

        // Add result
        inventory.addItem(recipe.getResult(), recipe.getResultCount());
        return true;
    }
}