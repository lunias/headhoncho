package com.ethanaa.headhoncho.component;

import java.util.concurrent.Callable;

import javafx.scene.control.ProgressBar;

import com.artemis.Component;
import com.ethanaa.headhoncho.manager.GuiManager.State;

public class LoadingBar extends Component {

	public double progress = 0.0;
	public boolean complete = false;
	public boolean error = false;
	public State finishedState = State.NOOP;
	
	public Callable<Boolean> onTick;
	public Callable<Boolean> onFinish;
	
	public ProgressBar progressBar;

	public LoadingBar() {
		
	}
	
	public LoadingBar(ProgressBar progressBar, double progress, State finishedState,
			Callable<Boolean> onTick, Callable<Boolean> onFinish) {

		this.progress = progress;
		this.finishedState = finishedState;		
		
		this.onTick = onTick;
		this.onFinish = onFinish;
		
		this.progressBar = progressBar;				
	}		
	
	public void errorOccured() {
		
		complete = true;
		error = true;
	}
	
	public void loadComplete() {
		
		complete = true;
		error = false;
		progress = 1.0;
		
	}
}
