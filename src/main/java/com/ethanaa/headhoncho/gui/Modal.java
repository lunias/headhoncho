package com.ethanaa.headhoncho.gui;

import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.artemis.World;
import com.ethanaa.headhoncho.manager.GuiManager;

public abstract class Modal extends Stage {

	private final GuiManager guiManager;
	
	public Modal(World world) {
		
		super(StageStyle.TRANSPARENT);

		guiManager = world.getManager(GuiManager.class);
		
		initModality(Modality.WINDOW_MODAL);
		initOwner(guiManager.getPrimaryStage());
		setScene(createModalScene(world));		
	}
	
	protected abstract Scene createModalScene(World world);
	
	private void blurParent(boolean blur) {
		
		Window owner = getOwner();
		
		if (blur) {
			owner.getScene().getRoot().setEffect(new BoxBlur());
		} else {
			owner.getScene().getRoot().setEffect(null);
		}
	}
	
	private void blurParent() {
		
		blurParent(true);
	}
	
	public void showModal(boolean recenter) {
		
		blurParent();
		show();
		if (recenter) {
			recenter();	
		}
	}
	
	public void showModal() {
		
		showModal(true);
	}
	
	public void hideModal() {
		
		blurParent(false);
		close();
	}
	
	protected void recenter() {
	
		sizeToScene();
		
		Window owner = getOwner();

//		System.out.println("(X, Y): (" + getX() + ", " + getY() + ")");
//		System.out.println("(W, H): (" + getWidth() + ", " + getHeight() + ")");
//		System.out.println("OWNER (X, Y): (" + owner.getX() + ", " + owner.getY() + ")");
//		System.out.println("OWNER (W, H): (" + owner.getWidth() + ", " + owner.getHeight() + ")");
		
		setX(owner.getX() + owner.getWidth() / 2 - getWidth() / 2);
		setY(owner.getY() + owner.getHeight() / 2 - getHeight() / 2);		
	}
	
	protected class Delta {
		
		public double x;
		public double y;

		public Delta() {

		}
	}	
}
