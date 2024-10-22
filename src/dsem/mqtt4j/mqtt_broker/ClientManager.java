package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;
import java.util.*;
import dsem.mqtt4j.global.*;

class ClientManager extends Thread {
	Connection conn;
	String clientName;

	public ClientManager(Connection conn) {
		super();
		this.conn = conn;
		this.clientName = "Client " + currentThread().getId();
	}

//	public void checkConnList(String topic) {
//		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(topic);
//
//		if (connlist == null) {
//			System.out.println("ClientManager> there is no subscriber(topic : " + topic + ")");
//			return;
//		}
//
//		System.out.println("ClientManager> connlist size : " + connlist.size());
//	}

	public void checkConnectionList(String topic) {
		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(topic);

		System.out.println(clientName + " ClientManager> Check all connections for (topic : " + topic + ")");

		if (connlist != null) {
			System.out.println(clientName + " ClientManager> Client size : " + connlist.size());

			for (int i = 0; i < connlist.size(); i++) {
				Connection connection = connlist.get(i);
//				if (connection.testConnection()) {
//					System.out.println("ClientManager> connection check Client #" + i + " : " + "success");
//				} else {
//					System.out.println("ClientManager> connection check Client #" + i + " : " + "failure");
//					connection.disconnect();
//					System.out.println("ClientManager> Client #" + i + " : " + "removed");
//					connlist.remove(i);
//					i--;
//				}
			}
		}
	}

	public synchronized void publishMessage(Message message) {
		if (message == null)
			return;
		
//		checkConnectionList(message.topic);
		
		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(message.topic);

		if (connlist == null) {
			System.out.println(clientName + " ClientManager> there is no subscriber(topic : " + message.topic + ")");
			return;
		} 

		String sendMessage = JSONManager.createJSONMessage(message);
		System.out.println(clientName + " ClientManager> Publish message : " + sendMessage);
		if (sendMessage == null)
			return;

		for (int i = 0; i < connlist.size(); i++) {
			Connection connection = connlist.get(i);
			try {
				System.out.println(clientName + " ClientManager> here comes test connection by publishMessage()");
				connection.sendMessage(sendMessage);
				System.out.println(clientName + " ClientManager> Client #" + i + " > publish success");
//				} else {
//					connection.disconnect();
//					connlist.remove(i);
//					System.out.println("ClientManager> Client #" + i + " > publish failed");
//					System.out.println("ClientManager> Client #" + i + " : " + "removed");
//
//					i--;
//				}
			} catch (Exception e) {
				connection.disconnect();
				connlist.remove(i);
				System.out.println(clientName + " Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.publishMessage()");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		try {
			System.out.println(clientName + " ClientManager> Client is connected.");

			while (true) {
				System.out.println(clientName + " ClientManager> ready for receive message.");

				String recvMessage = this.conn.receiveMessage();
 				System.out.println(clientName + " ClientManager> message received first of run() : " + recvMessage);
				if (recvMessage == null || "".equals(recvMessage)) {
					System.out.println(clientName + " ClientManager> Client is not connected.");
					break;
				} 
				
				System.out.println(clientName + " ClientManager> topic : " + recvMessage);

				Message message = JSONManager.parseMessage(recvMessage);
				if (message == null)
					break;

				System.out.println(clientName + " ClientManager> topic : " + message.topic);
				System.out.println(clientName + " ClientManager> message : " + message.message);

				
				if (Protocol.TOPIC_JOIN_SUBSCRIBER.equals(message.topic)) {
					String topic = message.message;
					ArrayList<Connection> connList;

					checkConnectionList(topic);
					
					if (MQTTBroker.subConnMap.containsKey(topic)) {
						System.out.println(clientName + " ClientManager> New subscriber joined (topic : " + topic + ")");
						connList = MQTTBroker.subConnMap.get(topic);
					} else {
						System.out.println(clientName + " ClientManager> New subscriber joined with new topic (topic : " + topic + ")");
						connList = new ArrayList<Connection>();
					}
					
					connList.add(this.conn);
					MQTTBroker.subConnMap.put(topic, connList);

				} else if (Protocol.TOPIC_REGISTER_PUBLISHER.equals(message.topic)) {
					
				} else if (Protocol.TOPIC_UNREGISTER_PUBLISHER.equals(message.topic)) {
					
				} else {
					System.out.println(clientName + " ClientManager> publish message for topic");
					publishMessage(message);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception occurred> dsem.mqtt4j.mqtt_broker.SubscriberManager.run()");
			System.out.println(e.getMessage());
//			e.printStackTrace();
		}
		
		System.out.println(clientName + " ClientManager> publisher is disconnected");
	}
}