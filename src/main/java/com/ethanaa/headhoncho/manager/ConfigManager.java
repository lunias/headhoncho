package com.ethanaa.headhoncho.manager;

import com.artemis.Manager;

public class ConfigManager extends Manager {

	private String playerName = "";

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}		
	
}
