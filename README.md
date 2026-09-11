# Saunet Samurai

A Java 2D platform game with animated samurai combat, two tile-based levels, scrolling backgrounds, enemies, music, and sound effects. Built with Java AWT/Swing and the included `game2D` framework.

## Requirements

- JDK 17 or newer, with `java` and `javac` available in your terminal.
- A desktop environment to display the game window.

No external libraries or build tools are required.

## Run the game

Open a terminal in the project root (the folder containing `src`, `images`, `maps`, and `sounds`). Compile and launch:

```powershell
javac -d bin src/game2D/*.java src/Game.java
java -cp bin Game
```

Recompile after changing the Java source. Always launch from the project root so relative asset paths resolve correctly.

In VS Code with Java support installed, open `src/Game.java` and use Run above its `main` method. Keep the working directory set to the project root.

## Controls

| Key | Action |
| --- | --- |
| Left / Right arrow | Walk left / right |
| Up arrow | Jump |
| Space | Run right (release the arrow keys) |
| X / C / Z | Different sword attacks |
| B | Toggle collision debugging |
| R | Restart after completing the game |
| Escape | Quit |
| Mouse press | Play taunt sound |

Move through both levels to complete the game.

## Project layout

- `src/Game.java`: entry point, game rules, controls, and level setup.
- `src/game2D/`: game loop, animation, sprites, tile maps, and audio support.
- `images/`: character sprites and backgrounds.
- `maps/`: level layouts and tile images.
- `sounds/`: music and sound effects.
- `bin/`: generated classes; recreated by compilation and ignored by Git.

The Eclipse project configuration is included. `.gitignore` excludes compiled output, JVM crash logs, and operating-system metadata.
