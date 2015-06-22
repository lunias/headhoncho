package com.ethanaa.headhoncho.component.card;

import java.util.concurrent.Callable;

import com.artemis.Component;
import com.artemis.World;
import com.ethanaa.headhoncho.component.zone.ZoneType;
import com.ethanaa.headhoncho.gui.card.CardNode;

public class Card extends Component {

	public CardType type = CardType.TEMPLATE;
	public ZoneType location = ZoneType.DRAFT;
	public boolean isFaceDown = false;
	public boolean isMagnified = false;	
	public boolean isVisible = true;
	public boolean isAwaitingPlacement = true;
	public boolean isFlipping = false;
	public boolean isDirty = true;
	public double defaultWidth = 220.0;
	public double defaultHeight = 300.0;
	public double aspectX = 0.733;
	public double aspectY = 1.0;
	
	public CardNode cardNode;
	
	public Callable<Void> onDraft;
	
	public Card(World world, ZoneType location, boolean isFaceDown, boolean isFlipping) {		
		this(world, location);
		this.isFaceDown = isFaceDown;
		this.isFlipping = isFlipping;
	}	
	
	public Card(World world, ZoneType location, boolean isFaceDown) {		
		this(world, location);
		this.isFaceDown = isFaceDown;
	}	
	
	public Card(World world, ZoneType location) {
		this(world);
		this.location = location;		
	}
	
	public Card(World world) {
		cardNode = new CardNode(world, this);
	}		
	
	public void setIsFaceDown(boolean isFaceDown) {
		
		this.isFaceDown = isFaceDown;
		this.isDirty = true;
	}
	
	public boolean isInOpponentZone() {
		
		return location.ordinal() > 9; 
	}
	
}
