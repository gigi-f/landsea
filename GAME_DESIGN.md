# Landsea - Game Design Document

## Overview
**Landsea** is a 2D top-down exploration and survival game inspired by *The Legend of Zelda*, *Stardew Valley*, and the world of *A Wizard of Earthsea*. The player wakes up on a small boat with no memory and must navigate a vast, procedurally generated ocean, gathering resources to upgrade their vessel and survive.

## Core Concepts

### 1. The Boat (The "Raft")
*   **Grid-Based Construction**: The boat is not a single sprite but a grid of tiles. The player can walk freely on the deck.
*   **Modular Upgrades**: The player expands the boat by placing new deck tiles, walls, and functional modules (Sails, Rudders, Workbenches, Cannons).
*   **Physics-Based Navigation**: The boat is propelled by wind and ocean currents. The player does not control the boat directly with WASD; instead, they must interact with the **Sail** and **Rudder** to harness the wind.

### 2. The Player
*   **Independent Movement**: The player character moves independently of the boat. Walking to the edge of the boat allows for fishing or jumping into a lifeboat.
*   **Interaction**: The player interacts with ship components (raising sails, steering) and the environment.
*   **Survival**: Hunger and fatigue systems (Day/Night cycle) require the player to fish, cook, and sleep.

### 3. The World
*   **Procedural Ocean**: An infinite or near-infinite ocean populated with islands, reefs, and deep-sea biomes.
*   **Dynamic Weather**: Wind direction and intensity change over time, affecting travel speed and direction. Storms can damage the boat.
*   **Exploration Loop**:
    1.  Sail to an island.
    2.  Anchor and explore on foot (or via lifeboat).
    3.  Gather resources (Wood, Stone, Iron, Magic).
    4.  Craft upgrades to reach more dangerous waters.

## Technical Architecture

### Entities
*   **Boat**: The parent entity for the mobile base. Handles physics (velocity, drag, wind force).
*   **Player**: The avatar. Handles local movement on the boat grid and interaction logic.
*   **Sail**: Component that catches wind. Can be raised/lowered and rotates visually.
*   **Rudder**: Component for steering. Requires active player control.

### Systems
*   **WindManager**: Global system controlling wind vector.
*   **OceanRenderer**: Handles water visuals (waves, whitecaps, wake).
*   **ChunkManager** (Planned): For infinite world generation.

## Current Progress (as of Dec 6, 2025)
*   [x] Basic Boat (2x4 Raft)
*   [x] Player Movement (Relative to Boat)
*   [x] Sailing Physics (Wind + Drag)
*   [x] Visuals (Red Billowing Sail, Ocean Waves, Wake)
*   [x] Steering (Rudder Interaction)

## Roadmap
1.  **World Generation**: Implement Perlin noise for island placement.
2.  **Collision**: Boat vs. Island collision.
3.  **Resource Gathering**: Trees/Rocks on islands.
4.  **Crafting**: UI for building new boat tiles.
5.  **Survival**: Hunger/Day-Night cycle.
