package com.ethanaa.headhoncho.manager;

import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artemis.Manager;
import com.ethanaa.headhoncho.gui.connect.ConnectScene;
import com.ethanaa.headhoncho.gui.draft.DraftModal;
import com.ethanaa.headhoncho.gui.draft.InitDraftScene;
import com.ethanaa.headhoncho.gui.splash.SplashScene;
import com.ethanaa.headhoncho.gui.table.Table;

public class GuiManager extends Manager {

	public static final String STYLESHEET = "com/ethanaa/headhoncho/css/hh.css";
	public static final String MODAL_STYLESHEET = "com/ethanaa/headhoncho/css/modal.css";
	
	private Stage primaryStage;
	private Table table;
	private DraftModal draftModal;
	
	public enum State {
		SPLASH, CONNECT, WAIT_FOR_READY, INIT_DRAFT, DRAFT, TRIAL, RESULTS, NOOP;
	}
	
	private State state;
	private boolean dirty;
	
	private static final Logger logger = LoggerFactory.getLogger(GuiManager.class);	
	
	public GuiManager(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.state = State.SPLASH;
		this.dirty = true;
	}
	
	public void changeState(State newState) {
		
		this.state = newState;
		this.dirty = true;
	}
	
	public void dirty() {
		
		this.dirty = true;
	}

	public void render() {		
		
		if (!dirty) return;
		
		switch(state) {
		case CONNECT:
			renderConnectScene();			
			break;
		case DRAFT:
			break;
		case INIT_DRAFT:
			renderInitDraft();
			break;
		case RESULTS:
			break;
		case SPLASH:
			renderSplashScene();			
			break;
		case TRIAL:
			break;
		case WAIT_FOR_READY:
			break;
		case NOOP:
			break;
		default:
			logger.error("Cannot find GuiSystem state " + state + ".");
			throw new RuntimeException("Encountered unknown GuiSystem state.");
		}		
	}		

	private void renderSceneToStage(Parent parent) {
		
		Scene scene = primaryStage.getScene();
		
		if (scene == null) {
			scene = new Scene(parent, -1, -1, false, SceneAntialiasing.BALANCED);
			scene.setCamera(new PerspectiveCamera());
			scene.getStylesheets().add(STYLESHEET);			
			primaryStage.setScene(scene);
			
		} else {
			scene.setRoot(parent);
		}
		
		this.dirty = false;
	}
	
	private void renderConnectScene() {		
		
		renderSceneToStage(new ConnectScene(world));
	}	
	
	private void renderInitDraft() {

		InitDraftScene scene = new InitDraftScene(world);
		
		draftModal = scene.getDraftModal();
		table = scene.getTable();
		
		renderSceneToStage(scene);		
	}
	
	private void renderSplashScene() {				
		
		renderSceneToStage(new SplashScene());
	}
	
	public Stage getPrimaryStage() {
		
		return primaryStage;
	}
	
	public Table getTable() {
		
		return table;
	}
	
	public DraftModal getDraftModal() {
		
		return draftModal;
	}

}
