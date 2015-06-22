package com.ethanaa.headhoncho.system.gui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ethanaa.headhoncho.component.LoadingBar;
import com.ethanaa.headhoncho.manager.GuiManager;

@Wire
public class LoadingBarSystem extends EntityProcessingSystem {	
	
	ComponentMapper<LoadingBar> lbm;
	
	@SuppressWarnings("unchecked")
	public LoadingBarSystem() {
		
		super(Aspect.getAspectForOne(LoadingBar.class));
	}

	@Override
	protected void process(Entity e) {
		
		LoadingBar loadingBar = lbm.get(e);
		
		try {
			
			loadingBar.complete = loadingBar.onTick.call();
			
		} catch (Exception e1) {
			loadingBar.errorOccured();
			e1.printStackTrace();
		}
		
		if (loadingBar.complete) {						
				
			GuiManager guiManager = e.getWorld().getManager(GuiManager.class);
			guiManager.changeState(loadingBar.finishedState);
			
			try {
				
				loadingBar.onFinish.call();
				
			} catch (Exception e1) {
				loadingBar.errorOccured();
				e1.printStackTrace();
			}
			
			e.deleteFromWorld();			
		}		
	}
	
}
