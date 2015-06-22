package com.ethanaa.headhoncho.network.hh;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.artemis.World;
import com.ethanaa.headhoncho.Main;
import com.ethanaa.headhoncho.network.Server;
import com.ethanaa.headhoncho.network.hh.message.DraftStartMessage;
import com.ethanaa.headhoncho.network.message.HandshakeMessage;

public class HHServer extends Server {

	private int playersConnected = 0;
	
	private List<String> players = new ArrayList<>();
	
	private World world;
	
	public HHServer(int port, World world) throws IOException {
		
		super(port);	
		this.world = world;
	}
	
	@Override
	protected boolean handshakeCallback(int id, ObjectInputStream inStream,
			ObjectOutputStream outStream) {
		
		try {
			HandshakeMessage handshake = (HandshakeMessage) inStream.readObject();
			
			if (++playersConnected > 2) {
				return false;
			}
			
			if (!Main.VERSION.equals(handshake.getVersion())) {
				return false;
			}
			
			players.add(handshake.getName());
			
		} catch (ClassNotFoundException | IOException e) {
			
			e.printStackTrace();
			return false;
		}		
		
		return true;
	}
	
	@Override
	protected void clientConnectedCallback(int id) {
		
		if (playersConnected == 2) {
			
			boolean goesFirst = new Random().nextBoolean();
			
			sendToOne(0, new DraftStartMessage(players.get(1), goesFirst));
			sendToOne(1, new DraftStartMessage(players.get(0), !goesFirst));
		}
	}
	
	@Override
	protected void clientDisconnectedCallback(int id) {
		super.clientDisconnectedCallback(id);
	}
	
	@Override
	protected void messageReceivedCallback(int id, Object message) {
		super.messageReceivedCallback(id, message);
	}

}
