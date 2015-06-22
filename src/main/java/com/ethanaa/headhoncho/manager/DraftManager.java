package com.ethanaa.headhoncho.manager;

import com.artemis.Manager;

public class DraftManager extends Manager {

	private boolean isDaftComplete = false;
	private boolean isDraftModalVisible = true;
	private int cardsShownPerRound = 8;
	private int cardsChosenPerRound = 1;	
	
	
	@Override
	protected void initialize() {
		
		super.initialize();
		
		
	}
	
}
