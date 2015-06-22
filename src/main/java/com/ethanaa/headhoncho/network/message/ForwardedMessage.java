package com.ethanaa.headhoncho.network.message;

import java.io.Serializable;

public class ForwardedMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final int clientId;
	public final Object message;

	public ForwardedMessage(int clientId, Object message) {
		
		this.clientId = clientId;
		this.message = message;
	}
	
}
