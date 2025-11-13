package com.espaneg.utils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

// This class helps load resources safely from the application bundle
public class ResourceLoader {

    // Define the standard size for all toolbar and UI icons
    private static final int ICON_SIZE = 32;

    /**
     * Loads an image from the classpath using ImageIO for more robust decoding.
     * It also resizes the image to a standard ICON_SIZE (32x32).
     * * * IMPORTANT: This assumes images are located in a 'resources/images'
     * directory in your project root.
     * * @param fileName The name of the file (e.g., "UNDO.png").
     * @return A new ImageIcon, or null if the file is not found or cannot be read.
     */
    public static ImageIcon loadIcon(String fileName) {
        // 1. Define the internal path (e.g., /images/HOME.png)
        String path = "/images/" + fileName;

        // 2. Use the ClassLoader to find the resource URL in the JAR/Classpath
        URL iconURL = ResourceLoader.class.getResource(path);

        if (iconURL != null) {
            try {
                // 3. Use ImageIO to read the image data.
                BufferedImage originalImage = ImageIO.read(iconURL);

                if (originalImage != null) {
                    // 4. Scale the image to the standard size (ICON_SIZE x ICON_SIZE)
                    Image scaledImage = getScaledImage(originalImage, ICON_SIZE, ICON_SIZE);

                    return new ImageIcon(scaledImage);
                } else {
                    System.err.println("ImageIO could not decode file (returned null): " + path);
                    return null;
                }
            } catch (IOException e) {
                // Log the exception if reading fails
                System.err.println("Error reading image file: " + path);
                e.printStackTrace();
                return null;
            }
        } else {
            // Log an error if the file isn't found
            System.err.println("Icon not found on classpath: " + path);
            return null;
        }
    }

    /**
     * Resizes a BufferedImage using high-quality rendering hints.
     * @param originalImage The source image.
     * @param width The target width.
     * @param height The target height.
     * @return The scaled Image object.
     */
    private static Image getScaledImage(BufferedImage originalImage, int width, int height) {
        // Create a new BufferedImage for the scaled image
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImage.createGraphics();

        // Set rendering hints for smooth, high-quality scaling
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the original image onto the new resized canvas
        g2.drawImage(originalImage, 0, 0, width, height, null);
        g2.dispose();

        return resizedImage;
    }
}