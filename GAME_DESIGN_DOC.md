# Landsea: Evolution Roadmap & Design Document

## Core Philosophy
**"Every Tile Matters"**
Inspired by Minecraft, the world of Landsea should feel dense with utility. Exploring isn't just about finding new islands; it's about finding *resources*. Every object in the environment—from the sand on the beach to the kelp in the water—should be interactable and potentially useful.

**Setting: Solar Punk / Post-Eco-Apocalypse**
The world is set deep in the future, after an ecological collapse. The aesthetic is "Solar Punk"—sustainable, organic technology mixed with scavenged remnants of the Old World. High-tech concepts (solar stills, advanced materials) are realized with primitive or scavenged resources.

---

## 1. The Living World (Environment & Topography)
The current flat islands will be replaced with complex, generated terrain.

### Topography & Elevation
*   **Height Maps**: Islands will have elevation (beaches -> grasslands -> hills -> cliffs/peaks).
*   **Occlusion**: Hills and mountains block line-of-sight (affecting the Telescope) and wind (creating "wind shadows" for sailing).
*   **Island Types**:
    *   **Atolls**: Ring-shaped coral islands with calm lagoons.
    *   **Archipelagos**: Clusters of small islands requiring tight maneuvering.
    *   **Continental Fragments**: Massive landmasses with rivers and deep forests.
    *   **Volcanic**: High risk, high reward (obsidian, sulfur).
    *   **Plastic Reefs**: Islands formed from centuries of compacted, colorful plastic waste. Vibrant but toxic. The only source of "Ancient Plastic" for 3D printing.
    *   **Living Islands (Megafauna)**: Rare, massive creatures (Zaratan) that look like islands. Building a fire might wake them up.

### Dynamic World Features
*   **Tidal Cycles**:
    *   *Low Tide*: Reveals land bridges and sea cave entrances.
    *   *High Tide*: Floods beaches. Campfires near the water get doused; supplies float away.
*   **Wind Highways**: Visible, shimmering currents of air. Entering one grants massive speed but reduces steering control.

### Ecosystems (Flora & Fauna)
The world is a simulation, not just a backdrop. Animals have needs and schedules.

#### Biomes & Specific Flora
*   **Tropical Beach**:
    *   *Flora*: Coconut Palms, Sea Grapes.
    *   *Fauna*: Hermit Crabs, Sea Turtles, Seagulls.
*   **Mangrove Swamp**:
    *   *Flora*: Red Mangroves (roots block movement), Glow-Moss (bioluminescent at night).
    *   *Fauna*: Mudskippers, Mosquitos (spread sickness), Herons.
*   **Dense Jungle**:
    *   *Flora*: Mahogany (Hardwood), Banana Trees, Corpse Flowers (attract flies).
    *   *Fauna*: Howler Monkeys, Jaguars, Tree Frogs.
*   **Rocky Highlands**:
    *   *Flora*: Highland Lichen (grows on stone), Ironwood Shrubs.
    *   *Fauna*: Mountain Goats, Giant Eagles.

#### Complex Interactions (The "Circle of Life")
*   **Predator & Prey**:
    *   *Jaguars* hunt *Boars* and *Monkeys*. If the Jaguar population is too high, the Boar population collapses.
    *   *Eagles* swoop down to snatch *Fish* from the water or *Rabbits* from the grass.
    *   *Sharks* are attracted to blood in the water (e.g., from spearfishing).
*   **Dietary Dependencies**:
    *   *Mountain Goats* exclusively eat *Highland Lichen*. If you harvest all the lichen, the goats migrate or starve.
    *   *Monkeys* eat *Bananas*. They spread seeds by dropping peels, causing new trees to grow.
    *   *Bees* are required for *Fruit Trees* to produce harvestable fruit. No hives = no food.
*   **Environmental Impact**:
    *   *Boars* dig up roots. A herd of boars can turn a lush grassland into a mud pit, destroying medicinal herbs.
    *   *Beavers* (on large islands) build dams, creating freshwater ponds that attract other wildlife.
    *   *Termites* infest dead wood. If not cleared, they can spread to your boat if docked too close to an infested tree.

### Weather & Atmosphere
*   **Dynamic Weather**:
    *   *Calm*: Clear skies, steady trade winds.
    *   *Squall*: Sudden rain, shifting winds. Good for water collection.
    *   *Gale*: High waves, risk of capsizing.
    *   *Fog*: Reduces visibility to near zero. Requires compass navigation.
*   **Wind Shadows**: Large islands block wind, creating dead zones on the leeward side.

---

## 2. Survival Mechanics
To drive exploration, the player has biological needs.

### Hunger & Thirst
*   **Thirst**: The most pressing need.
    *   *Solar Teas*: Combine water + herbs + sunlight in a jar. Grants sensory buffs (e.g., *Storm-Sight Tea* reveals pressure drops).
*   **Hunger (The "Gut Biome")**:
    *   *Dietary Diversity*: Eating the same food repeatedly gives diminishing returns and eventually "Nausea".
    *   *Incentive*: Must vary diet (fruit, meat, fish) to maintain "Clarity" (XP/Crafting buff).

### Cooking & Crafting
*   Raw food spoils or gives sickness.
*   **Composting Loop**: Spoiled food isn't trash. Throw it in a **Worm Bin** to produce "Rich Soil" (for planters) and "Bait" (for fishing).
*   **Preservation**: Salting fish/meat for long voyages.

---

## 3. The Depths (Underwater Exploration)
The ocean is not just a surface to sail on; it is a volume to explore.

### Visual Perspective
*   **Top-Down Layered View**: The camera remains top-down. As the player descends, upper layers fade out and lower layers fade in.
*   **Accessibility**:
    *   *Sandbars/Shallows*: Accessible by swimming.
    *   *Deep Ocean*: Requires diving gear or submersibles.

### Depth Layers
1.  **Surface**: Sailing, fishing.
2.  **Shallows (0-10m)**: *Snorkeling*. Coral reefs, shallow wrecks, clams.
    *   *Gear*: Goggles, Fins.
3.  **Twilight Zone (10-50m)**: *Diving*. Large predators, caves, rare minerals.
    *   *Gear*: Diving Bell, Air Hose (tethered to boat).
4.  **The Abyss (50m+)**: *Submersible*. Bioluminescence, ancient ruins, sea monsters.
    *   *Gear*: Solar-Electric Submarine.

### Mechanics
*   **Oxygen Plants ("Air Gardens")**: Giant bubble-algae found in the deep. Can be harvested for a breath refill or cultivated on the hull to create natural diving bells.
*   **Pressure-Locked Loot**: Some chests/clams are clamped shut by deep pressure. Attach a "Lift Bag" (balloon) to float them to the surface where they pop open.
*   **Bioluminescent Communication**:
    *   Creatures in the Abyss communicate via light patterns.
    *   *Puzzle*: Mimic patterns with your sub's lights to calm predators or open ancient ruins.
*   **The Slipstream**: Underwater currents that act as fast-travel highways. Dangerous to enter/exit.

---

## 4. Maritime Life & Conflict
The world is inhabited by others, some friendly, some hostile.

### Factions & Culture
*   **Driftwood Nomads**:
    *   A peaceful culture living on massive floating cities of lashed-together ships.
    *   *Quirk*: Taboo against touching land. Only trade at sea. Source of rare charts and rumors.
*   **The "Rust Bucket" Threat (Ghost Ships)**:
    *   Automated Old World warships running on corrupted logic.
    *   *Behavior*: Patrol specific routes enforcing ancient "perimeters."
    *   *Loot*: Only source of high-tech "Logic Boards."

### The "Living" Ocean (Karma System)
*   **Nature Reacts**: The ocean remembers your actions.
    *   *Exploitation*: Over-fishing or killing whales causes "Rough Seas" (more storms, aggressive sharks).
    *   *Stewardship*: Planting mangroves and rescuing turtles grants "Tailwinds" and friendly dolphins that herd fish to you.
*   **Message in a Bottle**:
    *   Find notes from other "survivors" (lore or async multiplayer) with coordinates to hidden stashes.

---

## 5. Tools & Technology
Progression is defined by what you can build.

### Navigation & Mapping
*   **Dynamic Map**:
    *   The map starts blank. As the player explores, the "Fog of War" lifts, revealing coastlines and islands.
    *   **Cartography Table**: A station on the boat where the player can combine their observations with found map fragments.
*   **Telescope**:
    *   Mechanic: Shifts the camera far in a direction.
    *   Constraint: Line-of-sight is blocked by fog, storms, and island elevation.

### Communication & Lore
*   **The Radio**:
    *   A scavenged piece of Old World tech.
    *   **Ticker Tape**: Displays mysterious news bulletins, coordinates for "Ghost Ships," or warnings about mega-storms.
    *   **Music**: Plays lo-fi, static-heavy tunes from automated beacons.
*   **Weather Forecasts**:
    *   **Weather Merchants**: Nomadic traders who sell accurate 3-day forecasts.
    *   **Barometer**: A craftable tool that gives a short-term warning of pressure drops (storms).

### Specialized Gadgets
*   **The Wind-Harp**:
    *   A buildable structure that hums different notes based on wind speed/direction. Allows navigation through fog by sound.
*   **Bioluminescent Paint**:
    *   Crafted from glowing jellyfish. Paint your hull to attract specific fish or scare away light-sensitive predators at night.
*   **Grappling Launcher**:
    *   Multi-purpose tool: Swing across island gaps, snag floating debris, or tether to a whale for a dangerous "Nantucket Sleighride."
*   **Automated Golems**:
    *   Late-game, clockwork/bio-mechanical drones. Can be assigned to "Water Duty" or "Fishing Duty." Cute but fragile.

### Boat Evolution: Modular Construction
Instead of buying pre-made ships, the player's boat is a growing, customizable home base.

*   **The Core (The "Heart")**:
    *   The player starts with a mysterious, ancient "Core" (Solar Punk tech).
    *   **Function**: It is the central power source and brain of the ship.
    *   **Bond**: It is bio-metrically locked to the player.
    *   **Recall Ability**: If the player is stranded on an island, they can signal the Core to autopilot the boat to their location (pathfinding permitting). This explains why you never abandon *your* ship.
*   **Modular Building**:
    *   **Hull Expansion**: Craft and place "Hull Tiles" to expand the deck size (2x2 -> 3x5 -> etc.).
    *   **Shape**: Player defines the shape (Catamaran style for speed, wide barge for storage).
    *   **Modules**:
        *   *Storage Crates*: Increases inventory.
        *   *Rain Catchers*: Passive water generation.
        *   *Planter Boxes*: Grow food on board.
        *   *Anchor Winch*: Faster raising/lowering.
*   **Submersible Dock**: Eventually, the boat becomes large enough to house a "Moon Pool" or crane to deploy the submersible.

---

## 6. Crafting & Industry
Crafting is tactile and process-driven, not just menu-clicking.

### The "Maker" Philosophy
*   **Clear Recipes**: No guessing. If you have a "Blueprint" (found or unlocked), the UI clearly shows: "Requires 2 Planks + 1 Rope".
*   **Tactile Crafting**: You don't just click "Craft". You place resources on a specific station (Workbench, Loom, Stove).
*   **Scavenge & Synthesize**: The economy is based on combining natural resources with Old World scrap.

### Master Crafting Index (The "Wishlist")
A comprehensive look at what players can build, categorized by function.

#### 1. Survival & Consumables
*   **Hydration**:
    *   *Coconut Flask*: Basic water storage.
    *   *Solar Tea Jar*: Brews buff-granting teas (Storm-Sight, Deep-Eye).
    *   *Canteen*: Durable, high-capacity metal flask.
*   **Medical**:
    *   *Aloe Balm*: Cures Sunburn.
    *   *Herbal Poultice*: Stops bleeding.
    *   *Antidote Vial*: Cures poison (Jellyfish/Snake bites).
    *   *Splint*: Fixes broken bones (restores movement speed).
*   **Utility**:
    *   *Glow-Stick*: Bioluminescent goo in a glass jar. Throw to light up underwater caves.
    *   *Sunscreen*: Fat + Zinc. Prevents stamina drain in midday sun.

#### 2. Tools & Equipment
*   **Harvesting**:
    *   *Stone Knife* -> *Obsidian Dagger* -> *Steel Machete* (Faster cutting).
    *   *Crude Hammer* -> *Builder's Mallet* -> *Power Drill* (Old World relic, requires battery).
    *   *Shovel*: Dig for clams, treasure, or terraform sandbars.
*   **Hunting/Fishing**:
    *   *Wooden Spear* -> *Bone Harpoon* -> *Pneumatic Speargun*.
    *   *Fishing Rod* -> *Throwing Net* -> *Magnetic Trawler*.
*   **Exploration**:
    *   *Spyglass* -> *Binoculars* -> *Rangefinder*.
    *   *Compass* -> *Gyroscope* (Stabilized navigation).
    *   *Glider*: Canvas wings for jumping from high peaks.

#### 3. Boat Modules (The Floating Home)
*   **Structure**:
    *   *Log Raft* (Base) -> *Plank Deck* (Walkable) -> *Reinforced Hull* (Ramming) -> *Glass Bottom* (Viewing).
*   **Propulsion**:
    *   *Tattered Sail* -> *Woven Mat Sail* -> *Canvas Sail* -> *Solar Sail* (High tech).
    *   *Pedal Drive*: Stamina-based backup for calm days.
    *   *Electric Outboard*: Silent, fast, requires battery.
*   **Living**:
    *   *Hammock*: Skip night (if safe).
    *   *Lantern Post*: Deck lighting.
    *   *Rug/Tapestry*: Decor that increases "Comfort" (Restores sanity/morale).
    *   *Gramophone*: Plays vinyl records found in ruins.
*   **Production Stations**:
    *   *Rain Catcher*: Passive water.
    *   *Drying Rack*: Preserves food.
    *   *Grill*: Cooks food.
    *   *Loom*: Weaves fiber into rope/cloth.
    *   *Recycler*: Plastic -> Filament.
    *   *3D Printer*: Filament -> Gears/Casings.
    *   *Hydroponics Bay*: High-efficiency farming.

#### 4. Tech & Solar Punk (Tier 4)
*   **Diving**:
    *   *Diving Bell*: Heavy, tethered air pocket.
    *   *Rebreather*: Scrub CO2, extends dive time.
    *   *Gill Suit*: Synthetic biology, allows water breathing.
*   **Power**:
    *   *Solar Panel*: Generates charge.
    *   *Bio-Generator*: Burns organic matter for power.
    *   *Battery Bank*: Stores power for night use.
*   **Advanced**:
    *   *Desalination Unit*: Unlimited fresh water (high power cost).
    *   *Sonar Screen*: Visualizes fish/terrain below.
    *   *Drone Station*: Houses and repairs Golems.

### Recycling Loop
*   **Plastic Waste**: A common "trash" item found in the ocean.
*   **Recycler**: A machine that turns Plastic Waste into "Filament".
*   **3D Printer**: Late-game station that prints complex parts (gears, casings) using Filament.

---

## 7. Health & Combat
Survival isn't just about eating; it's about defending yourself.

### Health System
*   **Health Points (HP)**: Standard bar. Regenerates slowly if Hunger/Thirst are full.
*   **Status Effects**:
    *   *Bleeding*: Requires Bandage.
    *   *Poisoned*: From jellyfish or snakes. Requires Antidote.
    *   *Broken Bone*: Reduces movement speed. Requires Splint.

### Combat Mechanics
*   **Hand-to-Hand (Man vs. Nature/Man)**:
    *   *Weapons*: Spear (reach), Machete (speed), Bow (range).
    *   *Mechanics*: Stamina-based. Block, Dodge, Strike.
*   **Boat-to-Boat (Naval Combat)**:
    *   *Not about Cannons*: In a Solar Punk world, combat is about disabling, not destroying.
    *   *Harpoons*: Tether enemy boats to board them.
    *   *Ramming*: High-risk maneuver with a reinforced hull.
    *   *Boarding*: The core of naval combat. Jump onto their deck and fight the crew.

---

## 8. Technical Implementation Ideas (For Discussion)
*   **Grid Interaction**: Right-clicking a tile with a tool performs an action (Shovel + Sand Tile = Sand Item).
*   **Layered Rendering**: When diving, the "Surface" layer fades out and the "Underwater" layer fades in. The boat becomes a shadow above.
*   **Wind Shadows**: Raycasting from island peaks to determine wind blockage.
