package com.katelyn_fred.game_server.loader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaFileCompiler {
    private String sourceDirectory;
    private String outputDirectory;

    public JavaFileCompiler(String sourceDirectory, String outputDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
    }

    public void compileJavaFiles() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            System.out.println("Java Compiler is not available. Make sure you have a JDK installed.");
            System.exit(1);
        }

        File srcDir = new File(sourceDirectory);
        String[] javaFiles = srcDir.list((dir, name) -> name.endsWith(".java"));

        if (javaFiles != null) {
            for (int i = 0; i < javaFiles.length; i++) {
                javaFiles[i] = sourceDirectory + File.separator + javaFiles[i];
            }
        }

        if (javaFiles == null || javaFiles.length == 0) {
            System.out.println("No .java files found in the source directory.");
            System.exit(1);
        }

        List<String> compilationOptions = Arrays.asList("-d", outputDirectory);
        List<String> sourceFiles = Arrays.asList(javaFiles);

        List<String> combinedList = new ArrayList<>(compilationOptions);
        combinedList.addAll(sourceFiles);

        String[] combinedArray = combinedList.toArray(new String[0]);

        int compilationResult = compiler.run(null, null, null, combinedArray);

        if (compilationResult == 0) {
            System.out.println("Compilation completed successfully.");
        } else {
            System.out.println("Compilation failed.");
        }
    }
}
