package com.ethanaa.headhoncho.gui.draft;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.component.player.Player;
import com.ethanaa.headhoncho.entity.Tag;
import com.ethanaa.headhoncho.gui.Modal;
import com.ethanaa.headhoncho.manager.GuiManager;

public class DraftModal extends Modal {		
	
	private TagManager tagManager;
	private GuiManager guiManager;
	
	private HBox topRow;
	private HBox bottomRow;
	
	private static final double SIZE_RATIO = 0.6;
	
	public DraftModal(World world) {
		
		super(world);
	}

	@Override
	protected Scene createModalScene(World world) {

		tagManager = world.getManager(TagManager.class);
		guiManager = world.getManager(GuiManager.class);
		
		Entity player = tagManager.getEntity(Tag.PLAYER);
		String playerName = player.getComponent(Player.class).name;
		
		Entity opponent = tagManager.getEntity(Tag.OPPONENT);
		String opponentName = opponent.getComponent(Player.class).name;
		
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		vBox.getStyleClass().add("modal-dialog");
		
		vBox.getChildren().addAll(opponentResources(opponentName), draftableCards(), playerResources(playerName), buttonBox());
		
		Scene draftModalScene = new Scene(vBox, guiManager.getPrimaryStage().getWidth() * SIZE_RATIO, guiManager.getPrimaryStage().getHeight() * SIZE_RATIO, false, SceneAntialiasing.BALANCED);
		draftModalScene.setCamera(new PerspectiveCamera());
		draftModalScene.getStylesheets().add(GuiManager.STYLESHEET);
		draftModalScene.getStylesheets().add(GuiManager.MODAL_STYLESHEET);
		draftModalScene.setFill(Color.TRANSPARENT);
		
		final Node root = draftModalScene.getRoot();
		final Delta dragDelta = new Delta();
		
		root.setOnMousePressed(me -> {
			dragDelta.x = getX() - me.getScreenX();
			dragDelta.y = getY() - me.getScreenY();
		});
		
		root.setOnMouseDragged(me -> {
			setX(me.getScreenX() + dragDelta.x);
			setY(me.getScreenY() + dragDelta.y);
		});
		
		return draftModalScene;
	}

	private Node opponentResources(String opponentName) {

		VBox opponentResources = new VBox(5);
		Label opponentNameLabel = new Label(opponentName);
		
		opponentResources.setAlignment(Pos.CENTER);
		
		opponentResources.getChildren().add(opponentNameLabel);
		opponentResources.getStyleClass().add("draft-modal-opponent-resources");
		
		return opponentResources;
	}

	private Node draftableCards() {

		VBox draftableCards = new VBox(10);		
		
		topRow = new HBox(10);
		topRow.setAlignment(Pos.CENTER);
		
		bottomRow = new HBox(10);		
		bottomRow.setAlignment(Pos.CENTER);
		
		draftableCards.getChildren().addAll(topRow, bottomRow);
		
		draftableCards.getStyleClass().add("draft-modal-draftable-cards");		
		
		VBox.setVgrow(draftableCards, Priority.ALWAYS);
		
		return draftableCards;
	}

	private Node playerResources(String playerName) {
		
		VBox playerResources = new VBox(5);
		Label playerNameLabel = new Label(playerName);
		
		playerResources.setAlignment(Pos.CENTER);
		
		playerResources.getChildren().add(playerNameLabel);
		playerResources.getStyleClass().add("draft-modal-player-resources");
		
		return playerResources;				
	}
	
	private Node buttonBox() {
	
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		
		Button closeButton = new Button("Close");
		closeButton.setOnAction(ae -> {
			hideModal();
		});
		
		buttonBox.getChildren().add(closeButton);		
		buttonBox.getStyleClass().add("draft-modal-button-box");
		
		return buttonBox;
	}
	
	public void addDraftCard(Card card) {
		
		int topCount = topRow.getChildren().size();
		
		if (topCount < 4) {
			topRow.getChildren().add(card.cardNode);
		} else {
			bottomRow.getChildren().add(card.cardNode);
		}
	}
}
