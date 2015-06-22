package com.ethanaa.headhoncho.component.player;

import com.artemis.Component;

public class Player extends Component {

	public int clientId;
	public String name;
	public boolean goingFirst;
	
	public Player() {
		// TODO Auto-generated constructor stub
	}
	
	public Player(int clientId, String name, boolean goingFirst) {
		this.clientId = clientId;
		this.name = name;
		this.goingFirst = goingFirst;
	}
	
}
