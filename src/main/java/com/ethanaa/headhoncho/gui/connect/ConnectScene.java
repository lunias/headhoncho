package com.ethanaa.headhoncho.gui.connect;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import com.artemis.World;
import com.ethanaa.headhoncho.manager.ConfigManager;
import com.ethanaa.headhoncho.manager.GuiManager;
import com.ethanaa.headhoncho.network.hh.HHClient;
import com.ethanaa.headhoncho.network.hh.HHServer;
import com.ethanaa.headhoncho.util.ResourceUtil;

public class ConnectScene extends BorderPane {

	private static final String DEFAULT_IP_ADDRESS = "127.0.0.1";
	private static final int DEFAULT_PORT = 50255;
	private static final String DEFAULT_PLAYER_NAME = "Test";
	
	private BooleanProperty isWaitingForJoin;
	private BooleanProperty isJoining;
	
	private BooleanProperty showConnectionStatus;
	private StringProperty connectionStatus;
	
	private BooleanProperty isConnected;
	
	private HHServer server;
	private HHClient client;	
	
	private World world;
	private ConfigManager config;
	
	public ConnectScene(World world) {

		super();
		
		this.world = world;
		config = world.getManager(ConfigManager.class);
		
		isWaitingForJoin = new SimpleBooleanProperty(false);
		isJoining = new SimpleBooleanProperty(false);
		
		showConnectionStatus = new SimpleBooleanProperty(false);
		connectionStatus = new SimpleStringProperty();
		
		isConnected = new SimpleBooleanProperty(false);
		
		// create the join host options
		VBox hostJoinBox = new VBox();
		hostJoinBox.setAlignment(Pos.CENTER);
		hostJoinBox.getStyleClass().add("hostjoin-box");
		
		Label hostJoinTitle = new Label(ResourceUtil.getString("connectScene.title"));
		hostJoinTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 40));
		
		GridPane hostJoinOptionsGrid = new GridPane();

		hostJoinOptionsGrid.setAlignment(Pos.CENTER);
		hostJoinOptionsGrid.setHgap(10);
		hostJoinOptionsGrid.setVgap(10);
		hostJoinOptionsGrid.setPadding(new Insets(25, 25, 25, 25));

		Label connectionInfoTitle = new Label("Host / Join");
		connectionInfoTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 20));

		Label playerNane = new Label("Name: ");
		TextField playerField = new TextField(DEFAULT_PLAYER_NAME);
		playerField.setPromptText("Player name");
		
		Label server = new Label("Server: ");
		TextField serverField = new TextField(DEFAULT_IP_ADDRESS);
		serverField.setPromptText("Server IP address");

		Label port = new Label("Port: ");
		TextField portField = new TextField(String.valueOf(DEFAULT_PORT));
		portField.setPromptText("Server port");

		Button hostButton = new Button("Host");
		Button joinButton = new Button("Join");
		joinButton.disableProperty().bind(isJoining.or(isWaitingForJoin));
		
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
		buttonBox.getChildren().addAll(hostButton, joinButton);				
		
		Text connectionError = new Text(ResourceUtil.getString("connectScene.error.connect"));
		connectionError.setFill(Color.RED);
		connectionError.visibleProperty().bind(showConnectionStatus);
		connectionError.textProperty().bind(connectionStatus);				
		
		hostJoinOptionsGrid.add(connectionInfoTitle, 0, 0, 2, 1);
		hostJoinOptionsGrid.add(playerNane, 0, 1);
		hostJoinOptionsGrid.add(playerField, 1, 1);		
		hostJoinOptionsGrid.add(server, 0, 2);
		hostJoinOptionsGrid.add(serverField, 1, 2);
		hostJoinOptionsGrid.add(port, 0, 3);
		hostJoinOptionsGrid.add(portField, 1, 3);
		hostJoinOptionsGrid.add(buttonBox, 1, 4);
		hostJoinOptionsGrid.add(connectionError, 0, 6, 2, 1);		
		
		hostJoinBox.getChildren().addAll(hostJoinTitle, hostJoinOptionsGrid);
		
		setCenter(hostJoinBox);
		
		hostButton.setOnAction(event -> {
			
			if (isWaitingForJoin.get()) {					
				
				isWaitingForJoin.set(false);
				hostButton.setText("Host");				
				showConnectionStatus.set(false);
				
				disconnectAndShutdownServer();
				
			} else {			
				
				connectionStatus.set("Creating server...");
				showConnectionStatus.set(true);
				
				int serverPort = -1;
				try {
					serverPort = Integer.parseInt(portField.getText());
					
				} catch (NumberFormatException nfe) { }
				
				if (serverPort < 0) {
					connectionStatus.set("Illegal port");
					portField.requestFocus();
					return;
				}
				
				try {
					this.server = new HHServer(serverPort, world);
					
				} catch (Exception e) {
					
					connectionStatus.set("Cannot listen on port " + serverPort);
					portField.requestFocus();
					return;
				}
				
				doClientConnection("127.0.0.1", serverPort, playerField.getText());
				
				isWaitingForJoin.set(true);				
				hostButton.setText("Cancel");
			}
		});		
		
		joinButton.setOnAction(event -> {			
			
			connectionStatus.set("Establishing connection...");
			showConnectionStatus.set(true);
			
			int serverPort = -1;
			try {
				serverPort = Integer.parseInt(portField.getText());
				
			} catch (NumberFormatException nfe) { }
			
			if (serverField.getText().isEmpty()) {
				connectionStatus.set("Server IP address required");
				serverField.requestFocus();
				return;
				
			} else if (serverPort < 0) {
				connectionStatus.set("Illegal port");
				portField.requestFocus();
				return;
			}			
			
			isJoining.set(true);								
			doClientConnection(serverField.getText(), serverPort, playerField.getText());
		});		
	}
	
	private void doClientConnection(final String host, final int port, final String playerName) {
		
		config.setPlayerName(playerName);
		
		new Thread() {
			
			public void run() {
				try {
					
					client = new HHClient(host, port, world);
					
					client.opponentProperty().addListener((observableValue, oldOpponent, newOpponent) -> {
						
						isConnected.set(true);												
					});
					
					Platform.runLater(new Runnable() {

						@Override
						public void run() {							
							
							if (isWaitingForJoin.get()) {
								connectionStatus.set("Waiting on client connection...");															
							}						
						}						
					});
					
				} catch (IOException ioe) {			
					
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							connectionStatus.set("Could not connect to server: \n    " + ioe.getMessage());
							showConnectionStatus.set(true);
							
							isConnected.set(false);
							isJoining.set(false);
							isWaitingForJoin.set(false);
						}						
					});			
				}
			}			
		}.start();		
	}
	
	private void disconnectAndShutdownServer() {
		
		client.disconnect();
		if (this.server != null) {
			this.server.shutdown();		
		}	
		
		isConnected.set(false);
	}	
	
}
