package dsem.mqtt4j.global;

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
	
	@Override
	public String toString() {
		return message + "(" + topic + ")"; 
	}
}
