package com.ethanaa.headhoncho.gui.splash;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class AssetLoadingBar extends ProgressBar {

	public AssetLoadingBar() {
		
		super();
		
		setPrefSize(200, 24);		
		
		Timeline fakeLoad = new Timeline(
		        new KeyFrame(
		                Duration.ZERO,
		                "start",
		                new KeyValue(progressProperty(), 0.0)
		        ),
		        new KeyFrame(
		                Duration.seconds(1),
		                "tick",
		                new KeyValue(progressProperty(), 1.0)
		        )				
		);		
		
		fakeLoad.playFromStart();
	}
}
