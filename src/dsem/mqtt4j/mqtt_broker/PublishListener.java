package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.util.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

class PublishListener extends Thread {
	private Connection conn;
	private static HashMap<String, ArrayList<Connection>> topicSuberMap;

	public PublishListener(Connection conn, HashMap<String, ArrayList<Connection>> map) {
		super();
		this.conn = conn;
		topicSuberMap = map;
	}

	public synchronized void publishMessage(Message message) {
		System.out.println("PublishListener> publish message entered.");
		
		ArrayList<Connection> connlist = topicSuberMap.get(message.topic);

		if (connlist == null) {
			System.out.println("PublishListener> there is no subscriber(topic : " + message.topic + ")");
			return;
		}	
		
		String sendMessage = JSONManager.createJSONMessage(message);

		System.out.println("PublishListener> sendMessage : " + sendMessage);
		if (sendMessage == null) return;
		

		
		for (int i=0; i<connlist.size(); i++) {
			Connection connection = connlist.get(i);
			try {
				connection.sendMessage(sendMessage);
				System.out.println("PublishListener> Send (topic : " + message.topic + ") #" + i + " > " + sendMessage);
			} catch (Exception e) {
				connection.disconnect();
				connlist.remove(i);
				System.out.println("Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.publishMessage()");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}		

	}
	
	public void run() {
		try {
			while(true) {
				String recvMessage = this.conn.receiveMessage();
				System.out.println("PublishListener> publish message received : " + recvMessage);

				Message message = JSONManager.parseMessage(recvMessage);
				
				publishMessage(message);
			}
		} catch (Exception e) {
			System.out.println("Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.run()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}