package com.ethanaa.headhoncho.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

import javax.imageio.ImageIO;

import com.ethanaa.headhoncho.Main;

/**
 * 
 * @author lunias
 *
 */
public class ResourceUtil {

	static ResourceBundle resources = ResourceBundle
			.getBundle("com.ethanaa.headhoncho.text");

	/**
	 * Gets the String for a given key in the text resource bundle for the
	 * application
	 * 
	 * @param key
	 *            name of the resource item in the text.properties file
	 * @return the String value of the keyed text
	 */
	public static String getString(String key) {
		return resources.getString(key);
	}

	/**
	 * Get a resource relative to the application class
	 * 
	 * @param path
	 * @return the String location
	 */
	static String getResource(String path) {
		return Main.class.getResource(path).toExternalForm();
	}
	
	public static URL getLocalWebResource(String htmlFile) {
		return Main.class.getResource("/com/ethanaa/headhoncho/html/" + htmlFile);
	}

	/**
	 * Get an image in an img/ path relative to the application class
	 * 
	 * @param imageFilename
	 * @return the {@link Image} at the path
	 */
	public static Image getImage(String imageFilename) {
		return new Image(ResourceUtil.getResource("img/" + imageFilename));
	}
	
	/**
	 * Copies an {@link ImageView} to a new {@link ImageView}
	 * 
	 * @param templateImageView
	 * @return a copy of the provided {@link ImageView}
	 */
    public static ImageView copyImageView(ImageView templateImageView) {
    	
        ImageView xerox = new ImageView();
        xerox.setFitHeight(templateImageView.getFitHeight());
        xerox.setPreserveRatio(templateImageView.isPreserveRatio());
        xerox.imageProperty().bind(templateImageView.imageProperty());
        xerox.setViewport(templateImageView.getViewport());
        
        return xerox;
    }	

	/**
	 * Convert an AWT image into a JavaFX image
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @param resize
	 * @param smooth
	 * @return a JavaFX {@link Image}
	 * @throws IOException
	 */
	public static javafx.scene.image.Image bufferedImageToFXImage(
			java.awt.Image image, double width, double height, boolean resize,
			boolean smooth) throws IOException {
		
		if (!(image instanceof RenderedImage)) {
			
			BufferedImage bufferedImage = new BufferedImage(
					image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			image = bufferedImage;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write((RenderedImage) image, "png", out);
		out.flush();
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		return new javafx.scene.image.Image(in, width, height, resize, smooth);
	}
	
	public static AudioClip getAudioClip(String audioClipFilename) {		
		return new AudioClip(ResourceUtil.getResource("sfx/" + audioClipFilename));
	}

}
