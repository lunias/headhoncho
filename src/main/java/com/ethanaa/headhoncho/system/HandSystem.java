package com.ethanaa.headhoncho.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.player.Hand;
import com.ethanaa.headhoncho.component.player.Player;

public class HandSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public HandSystem() {
		super(Aspect.getAspectForAll(Player.class, Hand.class));
	}

	@Override
	protected void process(Entity e) {
		
	}

}
