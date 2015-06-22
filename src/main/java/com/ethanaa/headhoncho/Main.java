package com.ethanaa.headhoncho;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.ethanaa.headhoncho.entity.EntityFactorySystem;
import com.ethanaa.headhoncho.manager.ConfigManager;
import com.ethanaa.headhoncho.manager.GuiManager;
import com.ethanaa.headhoncho.system.CardSystem;
import com.ethanaa.headhoncho.system.DraftSystem;
import com.ethanaa.headhoncho.system.PlayerSystem;
import com.ethanaa.headhoncho.system.TableSystem;
import com.ethanaa.headhoncho.system.gui.LoadingBarSystem;
import com.ethanaa.headhoncho.util.ResourceUtil;

public class Main extends Application {

	public static final String VERSION = "0.0.1";
	private static final String APPLICATION_ICON = "icon.png";
	
	private Stage primaryStage;
	private World world;
	
	private GuiManager guiManager;
	private EntitySystem draftSystem;
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		Application.launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		Screen screen = Screen.getPrimary();
		Rectangle2D screenBounds = screen.getVisualBounds();		
		
		// setup JavaFX stage
		primaryStage = stage;
		primaryStage.setOnCloseRequest(we -> {
			Platform.exit();
			System.exit(0);
		});
		
		primaryStage.setX(0);
		primaryStage.setY(0);
		primaryStage.setWidth(screenBounds.getWidth() / 1.2);
		primaryStage.setHeight(screenBounds.getHeight() / 1.2);		
		
		primaryStage.setTitle(ResourceUtil.getString("primaryStage.title"));
		
		primaryStage.show();
		
		primaryStage.getIcons().add(ResourceUtil.getImage(APPLICATION_ICON));
		
		// setup Artemis world
		world = new World();
		world.setManager(new GroupManager());
		world.setManager(new TagManager());
		world.setManager(new PlayerManager());
		world.setManager(new ConfigManager());
		guiManager = world.setManager(new GuiManager(primaryStage));		
		
		world.setSystem(new EntityFactorySystem());
		world.setSystem(new PlayerSystem());
		world.setSystem(new LoadingBarSystem());
		world.setSystem(new CardSystem());
		world.setSystem(new DraftSystem());
		world.setSystem(new TableSystem());
		
		world.initialize();
		
		logger.debug("World initialized.");
		
		gameLoop();
	}

	private void gameLoop() {		
		
		Task<Integer> gameTask = new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {

				boolean gameOver = false;
				
				final double targetDelta = 0.0333;
				final double maxDelta = 0.05;
				long previousTime = System.nanoTime();
				
				while(!gameOver) {
					
					if (isCancelled()) {
						logger.debug("Game loop cancelled. Return 1.");
						return 1;
					}
					
					long currentTime = System.nanoTime();
					double deltaTime = (currentTime - previousTime) / 1_000_000_000.0;
					
					if (deltaTime > maxDelta) {
						deltaTime = maxDelta;
					}					
					
					// update the game state
					update(deltaTime);
					
					// render on the JavaFX app thread
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							render();	
						}

						private void render() {
							guiManager.render();
						}
						
					});
					
					previousTime = currentTime;
					
					double frameTime = (System.nanoTime() - currentTime) / 1_000_000_000.0;										
					
					if (frameTime < targetDelta) {
						try {
							long sleepTime = (long) ((targetDelta - frameTime) * 1000);
							Thread.sleep(sleepTime);							
						} catch (InterruptedException ie) {
							if (isCancelled()) {
								logger.debug("Game loop cancelled while sleeping. Return 2.");
								return 2;
							}
						}
					}
				}
				
				logger.debug("Game Over. Return 0.");
				return 0;
			}

			private void update(double deltaTime) {				
				Platform.runLater(() -> {
					world.setDelta((float) deltaTime);
					world.process();
				});				
			}			
		};
		
		Thread gameThread = new Thread(gameTask);
		gameThread.setDaemon(true);
		gameThread.start();
	}

}
