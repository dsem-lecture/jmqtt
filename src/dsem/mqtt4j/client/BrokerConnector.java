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
	
	
}
