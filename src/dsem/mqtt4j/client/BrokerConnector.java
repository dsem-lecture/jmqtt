package dsem.mqtt4j.client;

import java.io.*;
import java.net.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

public class BrokerConnector {
	public Connection conn;
	public String broker_ip;
	public int broker_port;

	public BrokerConnector() {
		this(GlobalConfig.default_broker_ip, GlobalConfig.default_broker_port);
	}
	
	public BrokerConnector(String ip, int port) {
		this.conn = new Connection();
		this.broker_ip = ip;
		this.broker_port = port;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public boolean connectBroker() {
		if (this.conn.getSocket() == null || this.conn.getSocket().isClosed()) {
			this.conn.connect(broker_ip, broker_port);
			System.out.println("MQTTBroker connection is success.");
		} else {
			System.out.println("MQTTBroker connection is failed.");
		}
		
		return false;
	}

	public boolean disconnectBroker() {
		if (this.conn.getSocket() != null && this.conn.getSocket().isClosed()) {
			this.conn.connect(broker_ip, broker_port);
			System.out.println("MQTTBroker is connected successfully.");
		}
		
		return false;
	}

	public boolean registerPublisher() {
		Message msg = new Message("mqtt4j//publisher//register", "Publisher registration");
		
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
			System.out.println("publish (topic: " + msg.topic + ") : " + msg.message);
		}
		
		return true;
	}
	
	public boolean publishMessage(String topic, String message) {
		Message msg = new Message(topic, message);
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
			System.out.println("publish (topic: " + topic + ") : " + message);
		}
		
		return true;
	}

	public boolean joinSubscriber(String topic) {
		Message msg = new Message("mqtt4j//subscriber//join", topic);
		
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) {
			System.out.println("publish (topic: " + msg.topic + ") : " + msg.message);
		}
		
		return true;
	}
	
	public String subscirbe() {
		while (true) {
			String recvMsg = conn.receiveMessage();
			Message subMsg = JSONManager.parseMessage(recvMsg);

			System.out.println("subscribe (topic: " + subMsg.topic + ") : " + subMsg.message);
			
			return subMsg.message; 
		}
	}
}
