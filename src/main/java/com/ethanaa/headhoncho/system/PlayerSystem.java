package com.ethanaa.headhoncho.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.player.Player;

@Wire
public class PlayerSystem extends EntityProcessingSystem {

	private ComponentMapper<Player> playerMapper;
	//private HandSystem handSystem;
	
	@SuppressWarnings("unchecked")
	public PlayerSystem() {
		super(Aspect.getAspectForOne(Player.class));
	}

	@Override
	protected void process(Entity e) {
		
		Player player = playerMapper.get(e);		
	}
	
}
