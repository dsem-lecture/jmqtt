package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.util.*;
import dsem.mqtt4j.global.*;
import dsem.mqtt4j.structure.*;

class SubscriberManager extends Thread {
	Socket socket;
	private static HashMap<String, ArrayList<PrintWriter>> map;
	
	public SubscriberManager(Socket socket, HashMap<String, ArrayList<PrintWriter>> map) {
		this.socket = socket;
		this.map = map;
	}
	
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true)) {
			System.out.println("Client is connected.");

			Message msg = JSONManager.receiveJSONMessage(reader);
			
			if ("publisher".equals(msg.topic)) {
				PublishListener pl = new PublishListener(reader, map);
				pl.start();
			} else if ("subscriber".equals(msg.topic)) {
				if (map.containsKey(msg.topic)) {
					ArrayList<PrintWriter> writerList = map.get(msg.topic);
					writerList.add(writer);
					
				} else {
					ArrayList<PrintWriter> writerList = new ArrayList<PrintWriter>();
					writerList.add(writer);
					map.put(msg.topic, writerList);
				}
			}
			System.out.println("Client is disconnected.");
			this.socket.close();
		} catch (Exception e) {
			System.out.println("ClientManager.run");
			System.out.println(e.getMessage());
		}
	}
}