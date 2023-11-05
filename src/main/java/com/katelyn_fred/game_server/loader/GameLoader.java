package com.katelyn_fred.game_server.loader;

import com.katelyn_fred.game_server.Constants;
import com.katelyn_fred.game_server.server.games.base.Game;
import com.katelyn_fred.game_server.server.helpers.GameHelper;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class GameLoader {
    private final File gameDirectory;

    public GameLoader(String directoryPath) {
        this.gameDirectory = new File(directoryPath);
    }

    public void loadGames() {
        if (!gameDirectory.exists() || !gameDirectory.isDirectory()) {
            System.err.println("Game directory does not exist or is not a directory. Creating Directory.");
            gameDirectory.mkdirs();
        }

        File[] pluginFiles = gameDirectory.listFiles((dir, name) -> name.endsWith(".java"));
        if (pluginFiles == null || pluginFiles.length == 0) {
            System.err.println("No game Java files found in the directory.");
            return;
        }

        try {
            JavaFileCompiler fileCompiler = new JavaFileCompiler(Constants.GAME_DIRECTORY, Constants.GAME_DIRECTORY);
            fileCompiler.compileJavaFiles();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            URLClassLoader classLoader = new URLClassLoader(new URL[]{gameDirectory.toURI().toURL()});

            for (File javaFile : pluginFiles) {
                String className = javaFile.getName().replace(".java", "");
                Class<?> gameClass = Class.forName(className, true, classLoader);

                if (Game.class.isAssignableFrom(gameClass)) {
                    Game game = (Game) gameClass.getDeclaredConstructor(String.class).newInstance("dummy");
                    GameHelper.addGame(game);
                }
            }

            fileManager.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
