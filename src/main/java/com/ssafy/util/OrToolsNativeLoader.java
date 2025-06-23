package com.ssafy.util;

//OrToolsNativeLoader.java
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class OrToolsNativeLoader {
 static {
     try {
         InputStream in = OrToolsNativeLoader.class.getResourceAsStream("/jniortools.dll");
         if (in == null) {
             throw new RuntimeException("jniortools.dll not found in classpath (resources directory)");
         }
         File tempFile = File.createTempFile("jniortools", ".dll");
         tempFile.deleteOnExit();
         Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         System.load(tempFile.getAbsolutePath());
     } catch (Exception e) {
         throw new RuntimeException("Failed to load jniortools.dll", e);
     }
 }

 public static void init() {
     // trigger static block
 }
}