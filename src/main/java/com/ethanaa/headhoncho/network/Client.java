package com.ethanaa.headhoncho.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ethanaa.headhoncho.network.message.DisconnectMessage;
import com.ethanaa.headhoncho.network.message.ResetMessage;
import com.ethanaa.headhoncho.network.message.StatusMessage;

public abstract class Client {

	private final ConnectionToServer connection;
	
	private volatile boolean shouldAutoReset = false;
	
	public Client(Entry<String, Integer> hostAndPort) throws IOException {
		
		preConnection();
		
		this.connection = new ConnectionToServer(hostAndPort.getKey(), hostAndPort.getValue());
	}
	
	protected abstract void preConnection();
	
	protected abstract void messageReceived(Object message);
	
	protected void clientConnectedCallback(int id) {
		
	}
	
	protected void clientDisconnectedCallback(int id) {	
		
	}
	
	protected void shutdownCallback(Object message) {
		
	}
	
	protected void handshakeCallback(ObjectInputStream inStream, ObjectOutputStream outStream) throws IOException {
		
	}
	
	public int getConnectionId() {
		
		return connection.getId();
	}
	
	public void send(Object message) {
		if (message == null) {
			throw new IllegalArgumentException(
					"Null cannot be sent as a message.");	
		}
		
		if (!(message instanceof Serializable)) {
			throw new IllegalArgumentException(
					"Messages must implement the Serializable interface.");
		}
		
		if (connection.isClosed) {
			throw new IllegalStateException(
					"Message cannot be sent because the connection is closed.");
		}
		
		connection.send(message);
	}
	
	public void disconnect() {
		if (!connection.isClosed) {
			connection.send(new DisconnectMessage());
		}
	}
	
	private class ConnectionToServer {
		
		private int id;
		
		private BlockingQueue<Object> outgoingMessages;
		
		private ObjectOutputStream outStream;
		private ObjectInputStream inStream;
		
		private Socket connectionToServer;
		private volatile boolean isClosed;
		
		private Thread sendThread;
		private Thread receiveThread;
		
		ConnectionToServer(String host, int port) throws IOException {
			
			this.outgoingMessages = new LinkedBlockingQueue<Object>();
			this.connectionToServer = new Socket(host, port);
			
			this.outStream = new ObjectOutputStream(connectionToServer.getOutputStream());
			this.inStream = new ObjectInputStream(connectionToServer.getInputStream());						
						
			outStream.writeObject("ahoyhoy");
			outStream.flush();
			
			try {
				Object response = inStream.readObject();
				id = ((Integer) response).intValue();
			} catch (Exception e) {
				throw new IOException("Illegal id response from server.");
			}
			
			handshakeCallback(inStream, outStream);
						
			sendThread = new Thread(new WriterRunnable());
			receiveThread = new Thread(new ReaderRunnable());
			
			sendThread.start();
			receiveThread.start();
		}
		
		public int getId() {
			
			return id;
		}
		
		void close() {			
			
			isClosed = true;
			
			sendThread.interrupt();
			receiveThread.interrupt();
			
			try {
				connectionToServer.close();
			} catch (IOException ioe) { };
		}
		
		void send(Object message) {
			
			outgoingMessages.add(message);
		}
		
		private class WriterRunnable implements Runnable {

			@Override
			public void run() {
				
				try {
					while(!isClosed) {
						
						Object message = outgoingMessages.take();
						if (message instanceof ResetMessage) {
							outStream.reset();
							
						} else {							
							if (shouldAutoReset) {
								outStream.reset();
							}
							
							outStream.writeObject(message);
							outStream.flush();
							
							if (message instanceof DisconnectMessage) {
								close();
							}
						}
					}
					
				} catch (Exception e) {
					if (!isClosed) {
						// log error
					}
					
				} finally {
					// log termination / cleanup
				}
			}						
		}
		
		private class ReaderRunnable implements Runnable {

			@Override
			public void run() {

				try {
					while(!isClosed) {
						
						Object message = inStream.readObject();
						
						if (message instanceof DisconnectMessage) {
							close();
							shutdownCallback(message);
						
						} else if (message instanceof StatusMessage) {
							// handle status message
							
						} else {
							messageReceived(message);
						}
					}
					
				} catch (Exception e) {
					if (!isClosed) {
						// log error
					}
					
				} finally {
					// log termination / cleanup
				}
			}			
		}
	}
	
}
