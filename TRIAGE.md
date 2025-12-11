# Landsea Implementation Triage

## Phase 1: The Foundation (Core Systems)
**Goal**: Move from hardcoded entities to dynamic systems.
*   [x] **Modular Boat Architecture**: Refactor `Boat.java` from a fixed grid to a dynamic collection of `BoatTile`s. This allows for "Hull Expansion" and custom shapes.
*   [x] **Module System**: Create a base class for placeable objects (Sails, Anchors, Crates, Workbenches) that can be attached to specific tiles.
*   [x] **Inventory Backend**: A simple system to track resources (Wood, Scrap, Food) held by the player or in crates.

## Phase 2: The Living World (Environment)
**Goal**: Make exploration visually and mechanically interesting.
*   [x] **Height Map Generation**: Upgrade `IslandGenerator` to create elevation (Beaches, Cliffs, Peaks).
*   [ ] **Biome Support**: Differentiate islands by type (Tropical, Rocky, Swamp).
*   [x] **Resource Nodes**: Make trees, rocks, and bushes interactable sources of materials.

## Phase 3: Survival Loop
**Goal**: Add pressure and purpose to gameplay.
*   [ ] **Player Vitals**: Implement Hunger, Thirst, and Stamina.
*   [ ] **Gathering & Consumption**: Logic for harvesting coconuts and drinking water.
*   [ ] **Basic Crafting UI**: A menu to combine items (e.g., Wood + Leaf = Rope).

## Phase 4: Advanced Features
*   [ ] **Underwater Layers**: Rendering and physics for diving.
*   [ ] **NPCs & AI**: Simple behavior for fish and birds.
*   [ ] **Weather System**: Wind shifts and rain.
