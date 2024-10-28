package dsem.mqtt4j.client;

import dsem.mqtt4j.global.*;

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
			if (this.conn.connect(broker_ip, broker_port)) 
				return true;
		} 
			
		return false;
	}

	public boolean disconnectBroker() {
		if (this.conn.getSocket() != null && this.conn.getSocket().isClosed()) {
			if (this.conn.disconnect()) 
				return true;
		}
		return false;
	}

	public boolean registerPublisher() {
		Message msg = new Message(Protocol.TOPIC_REGISTER_PUBLISHER, "Publisher registration");
		
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg)) 
			return true;
		
		return false;		
	}
	
	public boolean publishMessage(String topic, String message) {
		Message msg = new Message(topic, message);
		String jsonMsg = JSONManager.createJSONMessage(msg);
		if (conn.sendMessage(jsonMsg)) 
			return true;
		
		return false;		
	}

	public boolean joinSubscriber(String topic) {
		Message msg = new Message(Protocol.TOPIC_JOIN_SUBSCRIBER, topic);
				
		String jsonMsg = JSONManager.createJSONMessage(msg);
		
		if (conn.sendMessage(jsonMsg))
			return true;
		
		return false;
	}
	
	public String subscirbe() {
		String recvMsg = conn.receiveMessage();
		if (recvMsg == null) 
			return null;
		
		Message subMsg = JSONManager.parseMessage(recvMsg);
		if (subMsg == null)
			return null;
		
		return subMsg.message; 
	}
}
