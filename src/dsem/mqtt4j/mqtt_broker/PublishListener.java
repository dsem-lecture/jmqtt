package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.util.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.global.structure.*;

class PublishListener extends Thread {
	private Connection conn;
	private static HashMap<String, ArrayList<Connection>> map;

	public PublishListener(Connection conn, HashMap<String, ArrayList<Connection>> map) {
		super();
		this.conn = conn;
		this.map = map;
	}

	public synchronized void publishMessage(Message message) {
		ArrayList<Connection> connlist = map.get(message.topic);
		String sendMessage = JSONManager.createJSONMessage(message);
		
		for (int i=0; i<connlist.size(); i++) {
			Connection connection = connlist.get(i);
			try {
				connection.sendMessage(sendMessage);
				System.out.println("Send " + i + " > " + sendMessage);
			} catch (Exception e) {
				connection.disconnect();
				connlist.remove(i);
			}
		}		

	}
	
	public void run() {
		try {
			while(true) {
				String recvMessage = this.conn.receiveMessage();
				System.out.println("publish message received : " + recvMessage);

				Message message = JSONManager.parseMessage(recvMessage);
				publishMessage(message);
			}
		} catch (Exception e) {
			System.out.println("catch");
			System.out.println(e.getMessage());
		}
	}
}