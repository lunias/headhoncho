package com.ethanaa.headhoncho.network.hh;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.artemis.World;
import com.ethanaa.headhoncho.Main;
import com.ethanaa.headhoncho.component.player.Player;
import com.ethanaa.headhoncho.entity.EntityFactorySystem;
import com.ethanaa.headhoncho.manager.ConfigManager;
import com.ethanaa.headhoncho.network.Client;
import com.ethanaa.headhoncho.network.hh.message.DraftStartMessage;
import com.ethanaa.headhoncho.network.message.ForwardedMessage;
import com.ethanaa.headhoncho.network.message.HandshakeMessage;

public class HHClient extends Client {

	private ObjectProperty<Player> opponentProperty = new SimpleObjectProperty<>();
	
	private static EntityFactorySystem entityFactory;
	private static ConfigManager config;
	
	public HHClient(String host, int port, World world) throws IOException {
		
		super(createHClient(host, port, world));		
	}
	
	public static Entry<String, Integer> createHClient(String host, int port, World world) {
		
		entityFactory = world.getSystem(EntityFactorySystem.class);		
		config = world.getManager(ConfigManager.class);
		
		return new SimpleEntry<>(host, port);
	}
	
	@Override
	protected void preConnection() {
		
	}	
	
	@Override
	protected void handshakeCallback(ObjectInputStream inStream,
			ObjectOutputStream outStream) throws IOException {

		outStream.writeObject(new HandshakeMessage(config.getPlayerName(), Main.VERSION));
		outStream.flush();
	}
	
	@Override
	protected void clientConnectedCallback(int id) {
		// TODO Auto-generated method stub
		super.clientConnectedCallback(id);
	}
	
	@Override
	protected void clientDisconnectedCallback(int id) {
		// TODO Auto-generated method stub
		super.clientDisconnectedCallback(id);
	}

	@Override
	protected void messageReceived(Object message) {
		
		int clientId = -1;
		Object readableMessage;
		
		if (message instanceof ForwardedMessage) {
			
			clientId = ((ForwardedMessage) message).clientId;
			readableMessage = ((ForwardedMessage) message).message;
			
		} else {
			readableMessage = message;
		}
		
		if (readableMessage instanceof DraftStartMessage) {			
			
			DraftStartMessage dsm = ((DraftStartMessage) readableMessage);
			
			entityFactory.initPlayers(getConnectionId(), config.getPlayerName(), dsm.opponentName, dsm.goingFirst);
		}
		
	}
	
	public ObjectProperty<Player> opponentProperty() {
		return opponentProperty;
	}

}
