package dsem.mqtt4j.client;

import java.io.*;
import java.net.*;

import dsem.mqtt4j.global.GlobalConfig;

class ConnectionManager {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String broker_ip;
	private int broker_port;
	
	public ConnectionManager() {
		this(GlobalConfig.default_ip, GlobalConfig.default_port);
	}
	
	public ConnectionManager(String broker_ip, int broker_port) {
		this.broker_ip = broker_ip;
		this.broker_port = broker_port;
	}
	
	public boolean connect() {
		try {
			Socket socket = new Socket(broker_ip, broker_port);
	        System.out.println("MQTTBroker connection successful.");
	        
	        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		}catch (Exception e) {
			System.out.println("BrokerConnector.connect()");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean disconnect() {
		try {
			this.reader.close();
			this.writer.close();
			this.socket.close();
		}catch (Exception e) {
			System.out.println("BrokerConnector.disconnect()");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	public String receiveMessage() {
		
	}
}
