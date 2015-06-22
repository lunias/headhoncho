package com.ethanaa.headhoncho.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.component.draft.Draftable;
import com.ethanaa.headhoncho.gui.draft.DraftModal;
import com.ethanaa.headhoncho.manager.GuiManager;

@Wire
public class DraftSystem extends EntityProcessingSystem {

	GuiManager guiManager;
	
	ComponentMapper<Card> cm;	
	
	@SuppressWarnings("unchecked")
	public DraftSystem() {
		
		super(Aspect.getAspectForAll(Card.class, Draftable.class));
	}
	
	@Override
	protected void process(Entity e) {
					
		DraftModal draftModal = guiManager.getDraftModal();
		if (draftModal == null) {
			return;
		}		
		
		Card card = cm.get(e);		
		

		if (card.isAwaitingPlacement) {
			draftModal.addDraftCard(card);
			card.isAwaitingPlacement = false;
		}	
	}
}
