package com.ethanaa.headhoncho.gui.splash;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.ethanaa.headhoncho.util.ResourceUtil;

public class SplashScene extends StackPane {

	private static final ImageView LOGO_IMG = new ImageView(ResourceUtil.getImage("logo.png"));
	
	public SplashScene() {
		
		super();
		
		getStyleClass().add("splash-scene");
		
		VBox vBox = new VBox(100);
		vBox.setAlignment(Pos.CENTER);
		
		vBox.getChildren().setAll(LOGO_IMG, new AssetLoadingBar());		
		
		getChildren().setAll(vBox);
	}
	
}
