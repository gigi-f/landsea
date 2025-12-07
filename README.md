# Landsea

A top-down 2D game built with LibGDX and Java.

## Project Structure

- `core/` - Core game logic and rendering
- `desktop/` - Desktop launcher using LWJGL3

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew :desktop:run
```

## Game Configuration

- Resolution: 1280x720
- Frame Rate: 60 FPS
- VSync: Enabled

## Development

### Adding Assets

Place game assets (images, sounds, etc.) in:
```
core/src/main/resources/assets/
```

### Project Structure

- `entities/` - Game entities (player, enemies, etc.)
- `screens/` - Game screens
- `input/` - Input handling
- `assets/` - Game assets

## Requirements

- Java 11+
- Gradle (included via wrapper)
