package dsem.mqtt4j.mqtt_broker;

import java.util.*;
import dsem.mqtt4j.global.*;

class ClientManager extends Thread {
	Connection conn;
	String clientName;
	static int clientNum=0;

	public ClientManager(Connection conn) {
		super();
		this.conn = conn;
		clientNum++;
		this.clientName = "Client " + clientNum;
	}
	
	public void printLog(String log) {
		System.out.println("MQTTBroker " + this.clientName + "> " + log);
	}

	public synchronized void publishMessage(Message message) {
		if (message == null)
			return;
		
		ArrayList<Connection> connlist = MQTTBroker.subConnMap.get(message.topic);

		if (connlist == null) {
			printLog("there is no subscriber(topic : " + message.topic + ")");
			return;
		} 

		String sendMessage = JSONManager.createJSONMessage(message);
		if (sendMessage == null)
			return;

		for (int i = 0; i < connlist.size(); i++) {
			Connection connection = connlist.get(i);
			try {
				printLog("send message to Subscirber #" + i);
				connection.sendMessage(sendMessage);
			} catch (Exception e) {
				connection.disconnect();
				connlist.remove(i);
				printLog("Exception occured> dsem.mqtt4j.mqtt_broker.PublishListener.publishMessage()");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			printLog("Client is connected.");

			while (true) {
				printLog("ready for receive message.");

				String recvMessage = this.conn.receiveMessage();
				if (recvMessage == null || "".equals(recvMessage)) {
					printLog("Client is not connected.");
					break;
				} 
				
				Message message = JSONManager.parseMessage(recvMessage);
				if (message == null)
					break;
				
				printLog("publish : " + message.toString());

				if (Protocol.TOPIC_JOIN_SUBSCRIBER.equals(message.topic)) {
					String topic = message.message;
					ArrayList<Connection> connList;
					
					if (MQTTBroker.subConnMap.containsKey(topic)) {
						printLog("New subscriber joined (topic : " + topic + ")");
						connList = MQTTBroker.subConnMap.get(topic);
					} else {
						printLog("New subscriber joined with new topic (topic : " + topic + ")");
						connList = new ArrayList<Connection>();
					}
					
					connList.add(this.conn);
					MQTTBroker.subConnMap.put(topic, connList);

				} else {
					printLog("publish message for topic (" + message.topic + ")");
					publishMessage(message);
				}
			}
		} catch (Exception e) {
			printLog("Exception occurred> dsem.mqtt4j.mqtt_broker.SubscriberManager.run()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		printLog("Publisher is disconnected");
	}
}