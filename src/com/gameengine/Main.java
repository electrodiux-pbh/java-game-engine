package com.gameengine;

import com.gameengine.util.Console;

/**
 * Simple main entry point for the Java Game Engine
 * This replaces the complex ClientMain with a straightforward initialization
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // Verify Java version
            if (!verifyRuntimeVersion()) {
                System.exit(1);
            }
            
            // Set up console output
            System.setOut(Console.out);
            System.setErr(Console.err);
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Console.out.println("Shutting down Java Game Engine...");
            }));
            
            // Initialize and start the engine
            Console.out.println("Starting Java Game Engine...");
            Engine.loadSimple();
            Engine.start();
            
        } catch (Exception e) {
            Console.err.println("Failed to start engine: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Verify if the Java Runtime is Java 8 or higher
     */
    private static boolean verifyRuntimeVersion() {
        try {
            float version = Float.parseFloat(System.getProperty("java.class.version"));
            if (version < 52.0F) { // Java 8 = 52.0
                System.err.println("Unsupported Java Runtime detected. Java 8 or higher is required.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            System.err.println("Could not determine Java version.");
            return false;
        }
    }
}