# Landsea - Setup Instructions

## Prerequisites

The project requires:
- Java 21+ (recommend OpenJDK 21)
- Gradle 8.5+

## Java Setup

If you're using a system with multiple Java versions, you can set the correct version:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

Add this to your `~/.bashrc` to make it permanent:
```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## Building the Project

From the project root directory:

```bash
gradle build
```

Or using the included wrapper:

```bash
./gradlew build
```

## Running the Game

```bash
gradle :desktop:run
```

Or:

```bash
./gradlew :desktop:run
```

## VS Code Setup

The project includes VS Code configuration files:
- `.vscode/settings.json` - Java extension settings
- `.vscode/launch.json` - Debug configuration
- `.vscode/tasks.json` - Build and run tasks

You can run tasks from VS Code with Ctrl+Shift+B (build) or through the command palette.

## Project Structure

```
landsea/
├── core/                          # Core game logic
│   ├── src/main/java/com/landsea/game/
│   │   ├── LandseaGame.java      # Main game class
│   │   ├── screens/
│   │   │   └── GameScreen.java   # Main game screen
│   │   ├── entities/              # Game entities (add here)
│   │   └── input/
│   │       └── InputHandler.java  # Input handling
│   ├── src/main/resources/assets/ # Game assets (images, sounds, etc.)
│   └── build.gradle
├── desktop/                        # Desktop launcher
│   ├── src/main/java/com/landsea/game/desktop/
│   │   └── DesktopLauncher.java  # Desktop entry point
│   └── build.gradle
├── build.gradle                   # Root build configuration
├── settings.gradle                # Gradle settings
└── gradle/wrapper/                # Gradle wrapper

```

## Game Configuration

Edit `DesktopLauncher.java` to configure:
- Window resolution (currently 1280x720)
- VSync settings
- Frame rate limit (currently 60 FPS)
- Window title

## Next Steps

1. **Add Game Logic**: Modify `GameScreen.java` to add your game mechanics
2. **Create Entities**: Create new classes in `entities/` for game objects
3. **Add Assets**: Place images and sounds in `core/src/main/resources/assets/`
4. **Input Handling**: Extend `InputHandler.java` for keyboard/mouse controls
5. **Add Physics**: Consider adding box2d by adding to `build.gradle`

## Useful LibGDX Resources

- [LibGDX Wiki](https://libgdx.badlogicgames.com/wiki/)
- [LibGDX JavaDocs](https://libgdx.badlogicgames.com/docs/api/)
- [LibGDX GitHub](https://github.com/libgdx/libgdx)
