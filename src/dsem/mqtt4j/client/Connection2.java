package dsem.mqtt4j.client;

import java.io.*;
import java.net.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

class Connection2 {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String broker_ip;
	private int broker_port;
	
	public Connection2() {
		this(GlobalConfig.default_broker_ip, GlobalConfig.default_broker_port);
	}
	
	public Connection2(String broker_ip, int broker_port) {
		this.broker_ip = broker_ip;
		this.broker_port = broker_port;
	}
	
	public boolean isSocketNull() {
		if (socket==null)
			return true;
		return false;
	}
	
	public boolean connect() {
		try {
			this.socket = new Socket(broker_ip, broker_port);
	        System.out.println("MQTTBroker connection successful.");
	        
	        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	        this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		}catch (Exception e) {
			System.out.println("ConnectionManager.connect()");
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
			System.out.println("ConnectionManager.disconnect()");
			System.out.println(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean sendMessage(Message message) {
		try {
			String jsonmsg = JSONManager.createJSONMessage(message);
			this.writer.println(jsonmsg);
		} catch (Exception e) {
			System.out.println("ConnectionManager.sendMessage()");
			System.out.println(e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public Message receiveMessage() {
		Message message = null;
		try {
			message = JSONManager.receiveJSONMessage(reader);
			System.out.println("Received : " + JSONManager.createJSONMessage(message));
		} catch (Exception e) { 
			System.out.println("ConnectionManager.sendMessage()");
			System.out.println(e.getMessage());
		}
		
		return message;		
	}
}
