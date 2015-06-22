package com.ethanaa.headhoncho.gui.table;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.artemis.World;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.gui.card.CardNode;

public class Table extends StackPane {

	private World world;
	
	private HBox opponentHand;
	private HBox opponentVillage;	
	private Node opponentFrontLine;
	private HBox opponentFrontLineLeft;
	private HBox opponentFrontLineRight;
	private Node opponentCommander;
	private Node invasion;
	private Node frontLine;
	private HBox frontLineLeft;
	private HBox frontLineRight;	
	private HBox commander;
	private HBox village;
	private HBox hand;
	
	private AnchorPane handAnchor;	
	private HBox zoomLayer;
	
	private static final double HIDDEN_HAND_OFFSET = -100.0;
	
	public Table(World world) {
		
		super();
		
		this.world = world;
		
		getChildren().addAll(createTableZones(), createHands(), createZoomLayer());		
		
		setAlignment(Pos.CENTER);
	}

	private VBox createTableZones() {

		VBox tableZones = new VBox(10);		
		
		tableZones.getChildren().setAll(
				opponentVillage = oppVillage(),
				opponentFrontLine = oppFrontLine(), 
				invasion = invasion(), 				
				frontLine = frontLine(),
				village = village());
		
		return tableZones;
	}
	
	private AnchorPane createHands() {
		
		handAnchor = new AnchorPane();			
		
		handAnchor.setPickOnBounds(false);
		handAnchor.setMaxWidth(600.0);
		
		handAnchor.getChildren().addAll(
				opponentHand = oppHand(),
				hand = hand());
		
		AnchorPane.setTopAnchor(opponentHand, HIDDEN_HAND_OFFSET);
		AnchorPane.setBottomAnchor(hand, HIDDEN_HAND_OFFSET);
		
		handAnchor.getStyleClass().add("hand-anchor");
		
		return handAnchor;		
	}
	
	private HBox createZoomLayer() {
		
		zoomLayer = new HBox();
		
		zoomLayer.setVisible(false);
		zoomLayer.setMouseTransparent(true);
		
		zoomLayer.setAlignment(Pos.CENTER);
		
		zoomLayer.getStyleClass().add("zoom-layer");
		
		return zoomLayer;		
	}

	private HBox oppHand() {		
		
		HBox oppHand = new HBox(20);
		oppHand.setAlignment(Pos.CENTER);		
		oppHand.setMaxHeight(50.0);
		
		HBox.setHgrow(oppHand, Priority.NEVER);
		
		oppHand.setOnMouseEntered(me -> {
			oppHand.setMaxHeight(Double.MAX_VALUE);
			AnchorPane.setTopAnchor(opponentHand, 0.0);
		});
		
		oppHand.setOnMouseExited(me -> {
			oppHand.setMaxHeight(50.0);
			AnchorPane.setTopAnchor(opponentHand, HIDDEN_HAND_OFFSET);			
		});		
		
		oppHand.getStyleClass().add("opponent-hand");
		
		return oppHand;
	}
	
	private HBox oppVillage() {
		
		HBox oppVillage = new HBox(20);
		oppVillage.setAlignment(Pos.CENTER);
		
		oppVillage.getStyleClass().add("opponent-village");
		
		VBox.setVgrow(oppVillage, Priority.ALWAYS);
		
		return oppVillage;		
	}

	private Node oppFrontLine() {
		
		HBox oppFrontLine = new HBox(20);
		oppFrontLine.setAlignment(Pos.CENTER);
		
		oppFrontLine.getChildren().setAll(
				opponentFrontLineLeft = oppFrontLineLeft(), 
				opponentCommander = oppCommander(),
				opponentFrontLineRight = oppFrontLineRight());
		
		oppFrontLine.getStyleClass().add("opponent-front-line");		
		
		return oppFrontLine;
	}
	
	private HBox oppFrontLineLeft() {		
		
		HBox oppFrontLineLeft = new HBox(20);
		oppFrontLineLeft.setAlignment(Pos.CENTER);		
		oppFrontLineLeft.getStyleClass().add("opponent-front-line-left");		
		
		return oppFrontLineLeft;		
	}
	
	private Node oppCommander() {
	
		Label oppCommanderLabel = new Label("Commander");
		
		HBox oppCommander = new HBox(20);
		oppCommander.setAlignment(Pos.CENTER);
		oppCommander.getChildren().add(oppCommanderLabel);
		oppCommander.getStyleClass().add("opponent-commander");		
		
		return oppCommander;		
	}	
	
	private HBox oppFrontLineRight() {		
		
		HBox oppFrontLineRight = new HBox(20);
		oppFrontLineRight.setAlignment(Pos.CENTER);		
		oppFrontLineRight.getStyleClass().add("opponent-front-line-right");		
		
		return oppFrontLineRight;		
	}	

	private Node invasion() {
		
		Label invasionLabel = new Label("Invasion");
		
		HBox invasion = new HBox(20);
		invasion.setAlignment(Pos.CENTER);		
		invasion.getChildren().add(invasionLabel);
		invasion.getStyleClass().add("invasion");
		
		VBox.setVgrow(invasion, Priority.ALWAYS);
		
		return invasion;
	}

	private Node frontLine() {		
		
		HBox frontLine = new HBox(20);
		frontLine.setAlignment(Pos.CENTER);	
		
		frontLine.getChildren().setAll(				
				frontLineLeft = frontLineLeft(), 
				commander = commander(), 
				frontLineRight = frontLineRight());
		
		frontLine.getStyleClass().add("front-line");		
		
		return frontLine;
	}
	
	private HBox frontLineLeft() {		
		
		HBox frontLineLeft = new HBox(20);
		frontLineLeft.setAlignment(Pos.CENTER);		
		frontLineLeft.getStyleClass().add("front-line-left");		
		
		return frontLineLeft;		
	}
	
	private HBox commander() {
		
		Label commanderLabel = new Label("Commander");
		
		HBox commander = new HBox(20);
		commander.setAlignment(Pos.CENTER);
		commander.getChildren().add(commanderLabel);
		commander.getStyleClass().add("commander");		
		
		return commander;			
	}
	
	private HBox frontLineRight() {
				
		HBox frontLineRight = new HBox(20);
		frontLineRight.setAlignment(Pos.CENTER);		
		frontLineRight.getStyleClass().add("front-line-right");		
		
		return frontLineRight;			
	}
	
	private HBox village() {		
		
		HBox village = new HBox(20);
		village.setAlignment(Pos.CENTER);
			
		VBox.setVgrow(village, Priority.ALWAYS);
		
		village.getStyleClass().add("village");		
			
		return village;		
	}

	private HBox hand() {
		
		HBox hand = new HBox(20);
		hand.setAlignment(Pos.CENTER);		
		hand.getStyleClass().add("hand");		
		hand.setMaxHeight(50.0);		
		
		HBox.setHgrow(hand, Priority.NEVER);		
		
		hand.setOnMouseEntered(me -> {
			hand.setMaxHeight(Double.MAX_VALUE);
			AnchorPane.setBottomAnchor(hand, 0.0);
		});
		
		hand.setOnMouseExited(me -> {
			hand.setMaxHeight(50.0);
			AnchorPane.setBottomAnchor(hand, HIDDEN_HAND_OFFSET);
		});		
				
		return hand;		
	}
	
	public boolean placeCard(Card card) {
		
		CardNode cardNode = card.cardNode;
		cardNode.setVisible(card.isVisible);
		card.isAwaitingPlacement = false;
		
		switch(card.location) {
		case COMMANDER:
			break;
		case DECK:
			break;
		case DISCARD:
			break;
		case FRONT_LINE_LEFT:
			
			return frontLineLeft.getChildren().add(cardNode);			
			
		case FRONT_LINE_RIGHT:
			
			return frontLineRight.getChildren().add(cardNode);
			
		case HAND:
			
			return hand.getChildren().add(cardNode);
			
		case INVASION:
			break;
		case OPP_DECK:
			break;
		case OPP_DISCARD:
			break;
		case OPP_FRONT_LINE_LEFT:
			
			return opponentFrontLineLeft.getChildren().add(cardNode);
			
		case OPP_FRONT_LINE_RIGHT:
			
			return opponentFrontLineRight.getChildren().add(cardNode);
			
		case OPP_HAND:						
			
			return opponentHand.getChildren().add(cardNode);
			
		case OPP_VILLAGE:
			break;
		case OPP_WORKER_POOL:
			break;
		case VILLAGE:
			break;
		case WORKER_POOL:
			break;
		default:
			break;		
		}
		
		return false;
	}	
	
	public void zoomIn(CardNode cardNode, double magnification) {
		
		cardNode.getStyleClass().add("drop-shadow");
		
		cardNode.setMaxSize(220.0 * magnification, 300.0 * magnification);
		cardNode.setPrefWidth(220.0 * magnification);
		
		zoomLayer.getChildren().add(cardNode);
		zoomLayer.setVisible(true);
	}
	
	public void zoomOut() {
		
		zoomLayer.getChildren().clear();
		zoomLayer.setVisible(false);
	}
	
	public HBox getZoomLayer() {
		return zoomLayer;
	}
	
	public Node getOpponentHand() {
		return opponentHand;
	}

	public Node getOpponentFrontLine() {
		return opponentFrontLine;
	}

	public Node getOpponentFrontLineLeft() {
		return opponentFrontLineLeft;
	}

	public Node getOpponentFrontLineRight() {
		return opponentFrontLineRight;
	}

	public Node getOpponentCommander() {
		return opponentCommander;
	}

	public Node getInvasion() {
		return invasion;
	}

	public Node getFrontLine() {
		return frontLine;
	}

	public Node getFrontLineLeft() {
		return frontLineLeft;
	}

	public Node getFrontLineRight() {
		return frontLineRight;
	}

	public Node getCommander() {
		return commander;
	}

	public Node getHand() {
		return hand;
	}	
	
}
