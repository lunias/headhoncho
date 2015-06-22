package com.ethanaa.headhoncho.gui.card;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import com.artemis.World;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.gui.table.Table;
import com.ethanaa.headhoncho.manager.GuiManager;


public class CardNode extends StackPane {
	
	private GuiManager guiManager;	
	
	public CardNode(World world, Card card) {

		super();		
		
		guiManager = world.getManager(GuiManager.class);
		
		VBox verticalLayout = new VBox(30);
		verticalLayout.setAlignment(Pos.CENTER);
		
		verticalLayout.getChildren().setAll(banishBox(), artBox(), effectBox());
		
		HBox horizontalLayout = new HBox(0);		
		
		horizontalLayout.getChildren().setAll(statsBox(), verticalLayout);				
		
		getChildren().setAll(horizontalLayout);
		getStyleClass().add("card");
		
		setOnMouseEntered(me -> {
			
			if (me.isControlDown()) {
				
				Table table = guiManager.getTable();
				
				if (table == null) return;
				
				if (!card.isFaceDown) {
					table.zoomIn(new CardNode(world, card), 1.5);	
				}
			}
		});
		
		setOnMouseExited(me -> {
			
			Table table = guiManager.getTable();
			
			if (table == null) return;
			
			table.zoomOut();			
		});		
	}	

	private Node statsBox() {

		Label label = new Label("Stats");
		label.setRotate(90.0);
		
		VBox stats = new VBox();		
		stats.getChildren().add(label);
		stats.getStyleClass().add("card-stats");		
		
		return stats;
	}

	private Node banishBox() {

		Label label = new Label("Banish");
		
		HBox banish = new HBox(10);
		banish.getChildren().add(label);
		banish.getStyleClass().add("card-banish");
		
		return banish;
	}	

	private Node artBox() {
		
		Label label = new Label("Art");
		
		VBox art = new VBox(10);
		art.getChildren().add(label);
		art.getStyleClass().add("card-art");
		
		return art;
	}
	
	private Node effectBox() {
		
		Label label = new Label("Effects");
		
		VBox effects = new VBox(5);
		effects.getChildren().add(label);
		effects.getStyleClass().add("card-effects");		
		
		return effects;
	}
	
	public void flip(Card card) {							
		
        RotateTransition flipTo90 = new RotateTransition(Duration.millis(400), card.cardNode);
        flipTo90.setAxis(Rotate.Y_AXIS);
        flipTo90.setFromAngle(0);
        flipTo90.setToAngle(card.isInOpponentZone() ? -90 : 90);
        flipTo90.setInterpolator(Interpolator.EASE_IN);
        flipTo90.setCycleCount(1);
        
        RotateTransition flipRemaining = new RotateTransition(Duration.millis(400), card.cardNode);
        flipRemaining.setAxis(Rotate.Y_AXIS);
        flipRemaining.setFromAngle(card.isInOpponentZone() ? 90 : -90);
        flipRemaining.setToAngle(0);
        flipRemaining.setInterpolator(Interpolator.EASE_OUT);
        flipRemaining.setCycleCount(1);        
        
    	Timeline timeline = new Timeline(
    			new KeyFrame(
    					new Duration(0),
    					new EventHandler<ActionEvent>() {
    						
    						@Override
    						public void handle(ActionEvent event) {
    							
    							flipTo90.play();
    						}
    					}),
    					new KeyFrame(
    							new Duration(400),
    							new EventHandler<ActionEvent>() {
    								
    								@Override
    								public void handle(ActionEvent event) {    									    									
    									
    									card.isFaceDown = !card.isFaceDown;
    									card.isFlipping = false;
    									card.isDirty = true;
    									
    									flipRemaining.play();        									    									
    								}
    							}));        
    	
    	timeline.play();				
	}	
	
}
