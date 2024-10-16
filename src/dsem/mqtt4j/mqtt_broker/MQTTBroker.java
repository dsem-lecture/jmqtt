package dsem.mqtt4j.mqtt_broker;

import dsem.mqtt4j.global.*;
import dsem.mqtt4j.structure.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class MQTTBroker {
	private String ip = GlobalConfig.ip;
	private int port = GlobalConfig.port;
	private static HashMap<String, ArrayList<PrintWriter>> map;

	public MQTTBroker() {		
		map = new HashMap<String, ArrayList<PrintWriter>>();
	}

	
	public static void main(String[] args) {
		new MQTTBroker().start();
	}

	public void start() {
		try (ServerSocket serverSocket = new ServerSocket(this.port)) {
			System.out.println("MQTT Broker started");

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("Connection Requested.");
								
				
				ClientManager mm = new ClientManager(socket);
				mm.run();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private class ClientManager extends Thread {
		Socket socket;
		
		public ClientManager(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true)) {
				System.out.println("Client is connected.");

				Message msg = JSONManager.receiveJSONMessage(reader);
				
				if ("publisher".equals(msg.topic)) {
					PublishListener pl = new PublishListener(reader);
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
	
	
	private class PublishListener extends Thread {
		BufferedReader reader;

		public PublishListener(BufferedReader reader) {
			super();
			this.reader = reader;
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
}
