package com.ethanaa.headhoncho.system;

import javafx.stage.Stage;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.manager.GuiManager;

@Wire
public class CardSystem extends EntityProcessingSystem {

	GuiManager guiManager;	
	
	ComponentMapper<Card> cm;
	
	@SuppressWarnings("unchecked")
	public CardSystem() {
		
		super(Aspect.getAspectForAll(Card.class));
	}

	@Override
	protected void process(Entity e) {

		Stage primaryStage = guiManager.getPrimaryStage();		
		
		Card card = cm.get(e);		
				
		// update card dimensions
		double maxCardHeight = primaryStage.getHeight() / 5.0;
		double maxCardWidth = primaryStage.getWidth() / 11.0;
		
		if (maxCardHeight > maxCardWidth) {
			maxCardWidth = maxCardHeight * card.aspectX;
		} else {
			maxCardHeight = maxCardWidth * card.aspectY;
		}
		
		card.cardNode.setPrefWidth(maxCardWidth);
		card.cardNode.setPrefHeight(maxCardHeight);
		
		if (card.isDirty) {
			
			if (card.isFaceDown) {
				card.cardNode.getChildren().stream().forEach(n -> {
					n.setVisible(false);
				});	
			} else {
				card.cardNode.getChildren().stream().forEach(n -> {
					n.setVisible(true);
				});				
			}
			
			if (card.isFlipping) {
				card.cardNode.flip(card);	
			}
			
			card.isDirty = false;
		}			
	}	
	
}
