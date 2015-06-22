package com.ethanaa.headhoncho.network.hh.message;

import java.io.Serializable;

public class DraftStartMessage implements Serializable {

	private static final long serialVersionUID = 169562586350219249L;
	
	public final String opponentName;
	public final boolean goingFirst;
	
	public DraftStartMessage(String opponentName, boolean goingFirst) {

		this.opponentName = opponentName;
		this.goingFirst = goingFirst;
	}		
}
