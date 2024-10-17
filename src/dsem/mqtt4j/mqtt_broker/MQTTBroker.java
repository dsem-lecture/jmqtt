package dsem.mqtt4j.mqtt_broker;

import dsem.mqtt4j.global.*;
import dsem.mqtt4j.structure.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class MQTTBroker {
	private String ip;
	private int port;
	private static HashMap<String, ArrayList<PrintWriter>> map;

	public MQTTBroker() {		
		ip = GlobalConfig.default_ip;
		port = GlobalConfig.default_port;

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
								
				
				SubscriberManager mm = new SubscriberManager(socket, map);
				mm.run();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
