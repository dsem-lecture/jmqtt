package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.util.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

class SubscriberManager extends Thread {
	Connection conn;
	private static HashMap<String, ArrayList<Connection>> map;

	public SubscriberManager(Connection conn, HashMap<String, ArrayList<Connection>> map) {
		super();
		this.conn = conn;
		this.map = map;
	}

	public void run() {
		try {
			System.out.println("Client is connected.");

			String recvMessage = this.conn.receiveMessage();
			Message message = JSONManager.parseMessage(recvMessage);

			if ("mqtt4j//subscriber//join".equals(message.topic)) {
				if (map.containsKey(message.message)) {
					ArrayList<Connection> connList = map.get(message.message);
					connList.add(this.conn);

					System.out.println("New subscriber joined (topic : " + message.message + ")");
				} else {
					ArrayList<Connection> connList = new ArrayList<Connection>();
					connList.add(this.conn);
					map.put(message.topic, connList);

					System.out.println("New subscriber joined and new topic registered (topic : " + message.message + ")");
				}
			}

			PublishListener pl = new PublishListener(this.conn, map);
			pl.start();

		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.mqtt_broker.SubscriberManager.run()");
			System.out.println(e.getMessage());
		}
	}
}