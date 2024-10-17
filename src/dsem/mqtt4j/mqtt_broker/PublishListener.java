package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.util.*;

import dsem.mqtt4j.global.*;
import dsem.mqtt4j.structure.*;

class PublishListener extends Thread {
	BufferedReader reader;
	private static HashMap<String, ArrayList<PrintWriter>> map;

	public PublishListener(BufferedReader reader, HashMap<String, ArrayList<PrintWriter>> map) {
		super();
		this.reader = reader;
		this.map = map;
	}

	public void publishMessage(Message msg) {
		ArrayList<PrintWriter> pwlist = map.get(msg.topic);
		String sendMessage = JSONManager.createJSONMessage(msg);
		
		for (int i=0; i<pwlist.size(); i++) {
			PrintWriter pw = pwlist.get(i);
			try {
				pw.println(sendMessage);
				System.out.println("Send " + i + " > " + sendMessage);
			} catch (Exception e) {
				pwlist.remove(i);
			}
		}		

	}
	
	public void run() {
		try {
			while(true) {
				Message msg = JSONManager.receiveJSONMessage(reader);
				
				publishMessage(msg);
			}
		} catch (Exception e) {
			System.out.println("catch");
			System.out.println(e.getMessage());
		}
	}
}