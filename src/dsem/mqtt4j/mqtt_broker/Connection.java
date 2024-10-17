package dsem.mqtt4j.mqtt_broker;

import java.io.*;
import java.net.*;

public class Connection {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	public Connection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.mqtt_broker.Connection.Connection()");
			System.out.println(e.getMessage());
		}
	}
	
	public void setConnection(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.mqtt_broker.Connection.setConnection(Socket socket)");
			System.out.println(e.getMessage());
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
	public boolean sendMessage(String message) {
		this.writer.println(message);
		this.writer.flush();
		return true;
	}
	
	public void disconnect() {
		try {
			this.reader.close();
			this.writer.close();
			this.socket.close();
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.mqtt_broker.Connection.disconnect()");
			System.out.println(e.getMessage());
		}
	}
	
	public String receiveMessage() {
		StringBuffer sb = new StringBuffer();
		String line;
		String message = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
	        }
			
			message = sb.toString();
		} catch (Exception e) {
			System.out.println("dsem.mqtt4j.mqtt_broker.Connection.receiveMessage()");
			System.out.println(e.getMessage());
		}
		
		return message;
	}
}
