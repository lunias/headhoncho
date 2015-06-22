package com.ethanaa.headhoncho.entity;

import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.ethanaa.headhoncho.component.Clickable;
import com.ethanaa.headhoncho.component.LoadingBar;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.component.draft.Draftable;
import com.ethanaa.headhoncho.component.player.Opponent;
import com.ethanaa.headhoncho.component.player.Player;
import com.ethanaa.headhoncho.component.zone.ZoneType;
import com.ethanaa.headhoncho.gui.splash.AssetLoadingBar;
import com.ethanaa.headhoncho.manager.GuiManager;
import com.ethanaa.headhoncho.manager.GuiManager.State;


@Wire
public class EntityFactorySystem extends AbstractEntityFactorySystem {

	private static final Logger logger = LoggerFactory.getLogger(EntityFactorySystem.class);	

	private static final int CARD_CREATION_DELAY = 70;
	
	private TagManager tagManager;
	private GroupManager groupManager;
	private GuiManager guiManager;

	public void initPlayers(int playerId, String playerName, String opponentName, boolean goingFirst) {

		int opponentId = playerId == 0 ? 1 : 0;		

		Player player = new Player(playerId, playerName, goingFirst);
		Player opponent = new Player(opponentId, opponentName, !goingFirst);

		Platform.runLater(() -> {
			tagManager.register(Tag.PLAYER, 
					world.createEntity().edit()
					.add(player)
					.getEntity());
			
			tagManager.register(Tag.OPPONENT, 
					world.createEntity().edit().add(opponent)
					.add(new Opponent())
					.getEntity());			
		});		
		
		guiManager.changeState(State.INIT_DRAFT);		
		
		try {
			initDraft();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}			
	}	

	public void initDraft() throws InterruptedException {

		for (int i = 0; i < 8; i++) {						
			createCard(ZoneType.DRAFT, new Clickable(), new Draftable());			
			Thread.sleep(CARD_CREATION_DELAY);
		}
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.FRONT_LINE_LEFT, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);
		}
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.FRONT_LINE_RIGHT, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);
		}
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.OPP_FRONT_LINE_LEFT, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);			
		}
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.OPP_FRONT_LINE_RIGHT, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);			
		}	
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.HAND, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);			
		}
		
		for (int i = 0; i < 4; i++) {						
			createCard(ZoneType.OPP_HAND, new Clickable());
			Thread.sleep(CARD_CREATION_DELAY);			
		}
		
	}
	
	public void createCard(ZoneType zoneType, Component... components) {				
					
		Platform.runLater(() -> {
			Entity cardEntity = world.createEntity().edit()
					.add(new Card(world, zoneType, true, zoneType.ordinal() < 10))
					.add(new Clickable())
					.getEntity();
			
			for (Component component : components) {
				cardEntity.edit().add(component);
			}
			
			groupManager.add(cardEntity, Group.CARD);			
		});		
	}

	@Override
	public Entity createEntity(EntityType entityType) {

		switch(entityType) {
		case ASSET_LOADING_BAR:
			return createAssetLoadingBar();
		default:
			logger.error("Attempted to create entity of unkown type!");
			throw new RuntimeException("Not sure how to create entity of type " + entityType + ".");
		}		
	}	

	private Entity createAssetLoadingBar() {

		final ProgressBar assetLoadingBar = new AssetLoadingBar();

		Callable<Boolean> onTick = () -> {

			if (assetLoadingBar.progressProperty().get() == 1.0) {
				return true;
			}

			return false;
		};

		Callable<Boolean> onFinish = () -> {

			return true; 
		};

		LoadingBar loadingBar = new LoadingBar(assetLoadingBar, 0.0, State.CONNECT, onTick, onFinish);		

		return world.createEntity().edit().add(loadingBar).getEntity();
	}	

	@Override
	protected void initialize() {

		super.initialize();	

		createEntity(EntityType.ASSET_LOADING_BAR);
	}

}
