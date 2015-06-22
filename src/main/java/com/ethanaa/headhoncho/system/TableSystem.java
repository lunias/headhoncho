package com.ethanaa.headhoncho.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.component.draft.Draftable;
import com.ethanaa.headhoncho.gui.table.Table;
import com.ethanaa.headhoncho.manager.GuiManager;

@Wire
public class TableSystem extends EntityProcessingSystem {

	GuiManager guiManager;
	
	ComponentMapper<Card> cm;	
	
	@SuppressWarnings("unchecked")
	public TableSystem() {
		
		super(Aspect.getAspectForAll(Card.class).exclude(Draftable.class));
	}
	
	@Override
	protected void process(Entity e) {		
		
		Table table = guiManager.getTable();		
		if (table == null) {
			return;
		}						
		
		Card card = cm.get(e);		
		
		if (card.isAwaitingPlacement) {
			
			System.out.println("Placing card @ " + card.location);
			
			table.placeCard(card);							
		}		
	}	
	
}
