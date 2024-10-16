package dsem.mqtt4j.structure;

public class Message {
	public String topic;
	public String message;
	
	public Message() {
		this("","");
	}
	
	public Message(String topic, String message) {
		this.topic = topic;
		this.message = message;
	}
}
