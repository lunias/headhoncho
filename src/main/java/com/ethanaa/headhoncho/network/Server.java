package com.ethanaa.headhoncho.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ethanaa.headhoncho.network.message.DisconnectMessage;
import com.ethanaa.headhoncho.network.message.ForwardedMessage;
import com.ethanaa.headhoncho.network.message.ResetMessage;

public class Server {

	private ServerSocket serverSocket;
	
	private Thread serverThread;
	private volatile boolean isShutdown = false;
	private volatile boolean shouldAutoReset = false;
	
	private BlockingQueue<Message> incomingMessages;
	
	private int nextClientId = 0;
	
	private TreeMap<Integer, ConnectionToClient> clientConnections;
	
	public Server(int port) throws IOException {
		
		clientConnections = new TreeMap<>();
		incomingMessages = new LinkedBlockingQueue<>();
		
		serverSocket = new ServerSocket(port);
		
		serverThread = new Thread(new ServerRunnable());
		serverThread.setDaemon(true);
		serverThread.start();
		
		Thread processingThread = new Thread() {
			
			@Override
			public void run() {
				
				while(true) {
					
					try {
						Message message = incomingMessages.take();
						messageReceived(message.clientConnection, message.message);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}
			}			
		};
		
		processingThread.setDaemon(true);
		processingThread.start();
	}
	
	public void shutdown() {
		
		if (serverThread == null) {
			return;
		}
		
		incomingMessages.clear();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		serverThread = null;
		serverSocket = null;
		isShutdown = true;
		
		sendToAll(new DisconnectMessage());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { }
		
		for (ConnectionToClient conn : clientConnections.values()) {
			conn.close();
		}
	}
	
	public void restart(int port) throws IOException {
		
		shutdown();
		isShutdown = false;
		serverSocket = new ServerSocket(port);
		serverThread = new Thread(new ServerRunnable());
		serverThread.start();
	}
	
	protected void messageReceivedCallback(int id, Object message) {
		
		sendToAll(new ForwardedMessage(id, message));
	}
	
	protected void clientConnectedCallback(int id) {
		
	}
	
	protected void clientDisconnectedCallback(int id) {
		
	}
	
	protected boolean handshakeCallback(int id, ObjectInputStream inStream, ObjectOutputStream outStream) {
		
		return true;
	}
	
    synchronized private void messageReceived(ConnectionToClient fromConnection, Object message) {
    	
        messageReceivedCallback(fromConnection.getId(), message);
    }
	
	synchronized private void acceptConnection(ConnectionToClient newConnection) {
		
		clientConnections.put(newConnection.getId(), newConnection);
		clientConnectedCallback(newConnection.getId());		
	}
	
	synchronized private void denyConnection(ConnectionToClient newConnection) {

		clientDisconnected(newConnection.getId());
		System.out.println("Denied client connection.");
	}
	
	synchronized private void clientDisconnected(int id) {
		
		clientConnections.remove(id);
		clientDisconnectedCallback(id);
	}
	
	synchronized public boolean sendToOne(int id, Object message) {
		
        if (message == null)
            throw new IllegalArgumentException(
                    "Null cannot be sent as a message.");
        
        if (!(message instanceof Serializable))
            throw new IllegalArgumentException(
                    "Messages must implement the Serializable interface.");
        
        ConnectionToClient conn = clientConnections.get(id);
        if (conn == null) {
            return false;
            
        } else {
        	conn.send(message);
            return true;
        }
	}
	
	synchronized public void sendToAll(Object message) {
		
        if (message == null)
            throw new IllegalArgumentException(
                    "Null cannot be sent as a message.");
        
        if (!(message instanceof Serializable))
            throw new IllegalArgumentException(
                    "Messages must implement the Serializable interface.");
        
        for (ConnectionToClient conn : clientConnections.values()) {
            conn.send(message);	
        }
	}
	
	synchronized public void sendToOthers(int id, Object message) {
		
        if (message == null)
            throw new IllegalArgumentException(
                    "Null cannot be sent as a message.");
        
        if (!(message instanceof Serializable))
            throw new IllegalArgumentException(
                    "Messages must implement the Serializable interface.");
        
        for (ConnectionToClient conn : clientConnections.values()) {
        	
        	if (conn.getId() == id) {
        		continue;
        	}
        	
            conn.send(message);	
        }
	}
	
	private class ServerRunnable implements Runnable {

		@Override
		public void run() {			
			try {
				
				while(!isShutdown) {
					
					Socket connectionToServer = serverSocket.accept();
					
					if (isShutdown) {
						System.out.println("Listener socket has been shut down.");
						break;
					}
					
					new ConnectionToClient(incomingMessages, connectionToServer);
				}
				
			} catch (Exception e) {
				
				if (isShutdown) {
					System.out.println("Listener socket has been shut down.");
					
				} else {
					System.out.println("Listener socket has been shut down by error: " + e);
				}
			}
			
		}		
	}
	
	private class ConnectionToClient {
		
		private int id;
		
		private BlockingQueue<Message> incomingMessages;
		private BlockingQueue<Object> outgoingMessages;
		
		private ObjectOutputStream outStream;
		private ObjectInputStream inStream;
		
		private Socket connectionToServer;
		private volatile boolean isClosed;
		
		private Thread sendThread;
		private volatile Thread receiveThread;		
		
		ConnectionToClient(BlockingQueue<Message> incomingMessages, Socket connectionToServer) {
			
			this.incomingMessages = incomingMessages;
			this.connectionToServer = connectionToServer;
			
			this.outgoingMessages = new LinkedBlockingQueue<>();
			
			sendThread = new Thread(new WriterRunnable());
			sendThread.start();
		}		
		
		public int getId() {
			
			return id;
		}
		
		void close() {
			
			isClosed = true;
			
			sendThread.interrupt();
			if (receiveThread != null) {
				receiveThread.interrupt();
			}
			
			try {
				connectionToServer.close();
			} catch (IOException ioe) { };
		}
		
		void send(Object object) {
			
			if (object instanceof DisconnectMessage) {
				outgoingMessages.clear();
			}
			outgoingMessages.add(object);
		}
		
		private class WriterRunnable implements Runnable {

			@Override
			public void run() {
				
				try {
					outStream = new ObjectOutputStream(connectionToServer.getOutputStream());
					inStream = new ObjectInputStream(connectionToServer.getInputStream());
					
					String greeting = (String) inStream.readObject();
					
					if (!"ahoyhoy".equals(greeting)) {
						throw new Exception("Incorrect greeting received from client.");
					}
					
					synchronized(Server.this) {
						id = nextClientId++;
					}
					
					outStream.writeObject(id);
					outStream.flush();
					
					// conditionally deny the connection
					
					if (!handshakeCallback(id, inStream, outStream)) {
						
						denyConnection(ConnectionToClient.this);
						try {
							isClosed = true;
							connectionToServer.close();
						} catch (IOException ioe) { }
						
						return;
					}
					
					acceptConnection(ConnectionToClient.this);
					
					receiveThread = new Thread(new ReaderRunnable());
					receiveThread.start();
					
				} catch (Exception e) {
					
					e.printStackTrace();
					
					try {
						connectionToServer.close();
					} catch (IOException e1) {
						System.out.println("Error establishing initial connection : " + e1);
						e1.printStackTrace();
						return;
					}					
				}
				
				while(!isClosed) {
					
					try {
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
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		}
		
		private class ReaderRunnable implements Runnable {

			@Override
			public void run() {
								
				while(!isClosed) {
					
					try {						
						Object message = inStream.readObject();
						
						if (!(message instanceof DisconnectMessage)) {
							incomingMessages.put(new Message(ConnectionToClient.this, message));
							
						} else {
							outgoingMessages.clear();
							outStream.writeObject("farewell");
							outStream.flush();
							
							clientDisconnected(id);
							close();
						}
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		}
	}
	
	private class Message {
		
		ConnectionToClient clientConnection;
		Object message;
		
		public Message(ConnectionToClient clientConnection, Object message) {
			
			this.clientConnection = clientConnection;
			this.message = message;
		}
	}
	
}
