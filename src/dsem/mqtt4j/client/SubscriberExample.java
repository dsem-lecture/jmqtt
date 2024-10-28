package dsem.mqtt4j.client;

import dsem.mqtt4j.global.*;

public class SubscriberExample {
	public static void main(String[] args) {
		String broker_ip = GlobalConfig.default_broker_ip;
		int broker_port = GlobalConfig.default_broker_port;
		
		BrokerConnector bc = new BrokerConnector(broker_ip, broker_port);
		
		if (bc.connectBroker()) {
			System.out.println("Subscriber> MQTTBroker connected.");

			String topic = "default/topic/test";
			if (bc.joinSubscriber(topic)) {
				System.out.println("Subscriber> subscribe (" + topic + ")");
				while (true) {
					String message = bc.subscirbe();
					if (message != null) {
						System.out.println("Subscriber> " + topic + " : " + message);
					}
				}
			}
			
		}
		
	}
}
