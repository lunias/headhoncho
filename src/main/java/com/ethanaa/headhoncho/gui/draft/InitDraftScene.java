package com.ethanaa.headhoncho.gui.draft;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.ethanaa.headhoncho.component.card.Card;
import com.ethanaa.headhoncho.entity.Group;
import com.ethanaa.headhoncho.gui.table.Table;


public class InitDraftScene extends BorderPane {
	
	private Table table;
	
	private DraftModal draftModal;
	
	private World world;
	
	private GroupManager groupManager;		
	
	public InitDraftScene(World world) {
		
		super();
		
		this.world = world;
		this.groupManager = world.getManager(GroupManager.class);
		
		this.draftModal = new DraftModal(world);
		
		// register table entity
		
		setCenter(table = new Table(world));
		
		Button button = new Button("Show modal");
		button.setOnAction(ae -> {
			
			showDraftModal();
		});
		
		Button flipBtn = new Button("Flip cards");
		flipBtn.setOnAction(ae -> {			
			for (Entity e : groupManager.getEntities(Group.CARD)) {				
				Card card = e.getComponent(Card.class); 
				card.isFlipping = true;
				card.isDirty = true;
			}			
		});
		
		HBox buttonBox = new HBox(20);
		buttonBox.getChildren().addAll(button, flipBtn);
		
		setBottom(buttonBox);		
	}
	
	private void showDraftModal() {		
		
		draftModal.showModal();
	}
	
	public Table getTable() {
		
		return table;
	}
	
	public DraftModal getDraftModal() {
		
		return draftModal;
	}
	
}
