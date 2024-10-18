package dsem.mqtt4j.global;

import org.json.simple.*;
import org.json.simple.parser.*;

import dsem.mqtt4j.global.structure.*;

import java.io.*;

public class JSONManager {
	public static Message parseMessage(String jsonMessage) {
		JSONParser parser = new JSONParser();
		JSONObject obj;
		
		System.out.println("JSONManager> jsonMessage : " + jsonMessage);
		
		try {
			obj = (JSONObject) parser.parse(jsonMessage);
			String topic = (String) obj.get("topic");
			String message = (String) obj.get("message");

			System.out.println("JSONManager> topic : " + topic);
			System.out.println("JSONManager> message : " + message);
			
			return new Message(topic, message);
		}catch (Exception e) {
			System.out.println("Exception occurred> JSONManager.parseMessage()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String createJSONMessage(Message msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("topic", msg.topic);
			obj.put("message", msg.message);
		} catch (Exception e) {
			System.out.println("Exception occurred> JSONManager.createJSONMessage()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return obj.toString();
	}

	public static Message receiveJSONMessage(BufferedReader reader) {
		StringBuffer jsonMsg = new StringBuffer();
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				jsonMsg.append(line);
	        }
			
			return parseMessage(jsonMsg.toString());
		} catch (Exception e) {
			System.out.println("Exception occurred> JSONManager.receiveJSONMessage()");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}	
	
}
