package com.ethanaa.headhoncho.entity;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;

public abstract class AbstractEntityFactorySystem extends VoidEntitySystem {
	
	public abstract Entity createEntity(EntityType entityType);
	
	@Override
	protected void processSystem() {		
	}
	
}
