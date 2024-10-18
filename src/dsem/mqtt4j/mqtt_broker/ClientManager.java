package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.util.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

class ClientManager extends Thread {
	Connection conn;
	private static HashMap<String, ArrayList<Connection>> topicSuberMap;

	public ClientManager(Connection conn, HashMap<String, ArrayList<Connection>> map) {
		super();
		this.conn = conn;
		topicSuberMap = map;
	}

	@Override
	public void run() {
		try {
			System.out.println("ClientManager> Client is connected.");

			String recvMessage = this.conn.receiveMessage();
			if (recvMessage == null) return;
			
			Message message = JSONManager.parseMessage(recvMessage);
			if (message == null) return;

//			System.out.println("ClientManager> Message received. " + recvMessage);
//			System.out.println("ClientManager> recived topic : " + message.topic);
//			System.out.println("ClientManager> recived message : " + message.message);
			
			if ("mqtt4j/subscriber/join".equals(message.topic)) {
				if (topicSuberMap.containsKey(message.message)) {
					ArrayList<Connection> connList = topicSuberMap.get(message.message);
					connList.add(this.conn);

					System.out.println("ClientManager> New subscriber joined (topic : " + message.message + ")");
				} else {
					ArrayList<Connection> connList = new ArrayList<Connection>();
					connList.add(this.conn);
					topicSuberMap.put(message.topic, connList);

					System.out.println("ClientManager> New subscriber joined and new topic registered (topic : " + message.message + ")");
				}
			} else if ("mqtt4j/publisher/register".equals(message.topic)) {
				PublishListener pl = new PublishListener(this.conn, topicSuberMap);
				pl.start();
			} else {
				System.out.println("ClientManager> topic is not valid.");
			}
		} catch (Exception e) {
			System.out.println("Exception occurred> dsem.mqtt4j.mqtt_broker.SubscriberManager.run()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}