package com.ethanaa.headhoncho.network.message;

import java.io.Serializable;

public class HandshakeMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String version;
	
	public HandshakeMessage(String name, String version) {
		
		this.name = name;
		this.version = version;
	}	
	
	public String getName() {
		
		return name;
	}
	
	public String getVersion() {
		
		return version;
	}	

}
